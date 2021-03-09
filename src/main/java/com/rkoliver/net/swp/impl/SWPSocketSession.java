package com.rkoliver.net.swp.impl;

import com.rkoliver.net.swp.*;
import com.rkoliver.net.swp.codepoint.SWPCodePoint;
import com.rkoliver.net.swp.util.New;
import com.rkoliver.net.swp.util.SWPConverter;
import com.rkoliver.net.swp.util.Tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class SWPSocketSession implements SWPSession {

    private final String host;
    private final int port;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private final byte[] buffer = New.bytes(0x00007fff);
    private int offset = 0;
    private int bytesLeftInBuffer = buffer.length;
    private int bytesLeftInPacket = 0;
    private boolean additionalPackets = true;
    private boolean startPacket = true;
    private int packetStart = SwpConstants.SWP_UNKNOWN;

    public SWPSocketSession(Socket socket) throws SWPException {

        SWPException.requireNonNull(socket, "socket must not be null.");
        this.host = socket.getInetAddress().getHostName();
        this.port = socket.getPort();
        this.socket = socket;

        try {

            this.in = socket.getInputStream();
            this.out = socket.getOutputStream();
        }
        catch (IOException e) {

            this.socket = null;
            this.in = null;
            this.out = null;
            throw SWPException.convert(e);
        }
    }

    public SWPSocketSession(String host, int port) {

        this.host = host;
        this.port = port;
    }

    public SWPSocketSession(int port) {
        this(null, port);
    }

    @Override
    public void openWithRetry(final int times, final long pause, final TimeUnit timeUnit) throws SWPException {
        Tools.retry(this::open, times, pause, timeUnit);
    }

    @Override
    public void open() throws SWPException {

        if (socket != null) {

            if (!socket.isClosed()) {

                throw SWPException.sessionAlreadyOpen();
            }

            socket = null;
            in = null;
            out = null;
        }

        try {

            socket = new Socket(host, port);
            in = socket.getInputStream();
            out = socket.getOutputStream();
        }
        catch (IOException e) {

            socket = null;
            in = null;
            out = null;
            throw SWPException.convert(e);
        }
    }

    @Override
    public void close() throws SWPException {

        if (socket != null) {

            if (!socket.isClosed()) {

                try {

                    socket.close();
                }
                catch (IOException e) {

                    throw SWPException.convert(e);
                }
                finally {

                    socket = null;
                    in = null;
                    out = null;
                }
            }

            socket = null;
            in = null;
            out = null;
        }
    }

    @Override
    public boolean isOpen() throws SWPException {

        return (socket != null && !socket.isClosed());
    }

    @Override
    public SWPReadable read() throws SWPException {

        if (bytesLeftInPacket == 0) {

            if (!additionalPackets) {

                return null;
            }

            readPacketHeader();
        }

        int originalLength = bytesLeftInBuffer;
        int codePointLength = readUInt16();
        int overhead = 4;
        if ((codePointLength & 0x00008000) == 0x00008000) {

            // 4-byte length
            codePointLength = (codePointLength << 16 | readUInt16()) & 0x7fffffff;
            overhead += 2;
        }

        int codePoint = readUInt16();
        if ((codePoint & 0x00008000) == 0x00008000) {

            // 4-byte codepoint
            codePoint = (codePoint << 16 | readUInt16()) & 0x7fffffff;
            overhead += 2;
        }

        if (codePointLength > originalLength) {
            // TODO: Support for codepoint split across multiple packets?
            throw SWPException.partialCodePoint();
        }

        SWPCodePoint cp = SWPFactory.getCodePoint(codePoint);
        int i = cp.read(buffer, offset, codePointLength - overhead);
        SWPException.requireTrue(i == codePointLength - overhead, "Actual codepoint length did not match reported codepoint length.");
        offset += i;
        bytesLeftInBuffer -= i;
        bytesLeftInPacket -= i;
        return cp;
    }

    @Override
    public void write(SWPWritable writable) throws SWPException {

        SWPException.requireNonNull(writable, "writable must not be null.");

        if (startPacket) {

            // Write a placeholder length
            packetStart = offset;
            writeUInt16(SwpConstants.SWP_UNKNOWN);
            startPacket = false;
        }

        int i = writable.write(buffer, offset, bytesLeftInBuffer);
        offset += i;
        bytesLeftInBuffer -= i;
    }

    @Override
    public void send() throws SWPException {

        SWPException.requireTrue(isOpen(), "Session is not open.");

        if (packetStart != SwpConstants.SWP_UNKNOWN) {

            // Must update the packet header
            int packetLength = offset - packetStart;
            writeUInt16(packetLength, packetStart);
            packetStart = SwpConstants.SWP_UNKNOWN;
        }

        try {

            out.write(buffer, 0, offset);
            out.flush();
        }
        catch (IOException e) {

            throw SWPException.convert(e);
        }
        finally {

            offset = 0;
            bytesLeftInBuffer = buffer.length;
        }
    }

    @Override
    public void receive() throws SWPException {

        SWPException.requireTrue(isOpen(), "Session is not open.");

        try {

            bytesLeftInBuffer = in.read(buffer, 0, buffer.length);
        }
        catch (IOException e) {

            bytesLeftInBuffer = 0;
            throw SWPException.convert(e);
        }
        finally {

            offset = 0;
        }
    }

    private int readUInt16() throws SWPException {

        // TODO: Check to see if there are enough bytes in the packet but not in the
        // buffer.  If so, go back to the socket to read more data.
        SWPException.requireTrue(bytesLeftInBuffer >= 2, "No more data available.");
        SWPException.requireTrue(bytesLeftInPacket >= 2, "No more data available.");
        int value = SWPConverter.convertUInt16BE(buffer, offset);
        offset += 2;
        bytesLeftInBuffer -= 2;
        bytesLeftInPacket -= 2;
        return value;
    }

    private void readPacketHeader() throws SWPException {

        // TODO: Check to see if there are enough bytes in the packet but not in the
        // buffer.  If so, go back to the socket to read more data.
        SWPException.requireTrue(bytesLeftInBuffer >= 2, "No more data available.");

        bytesLeftInPacket = SWPConverter.convertUInt16BE(buffer, offset);

        if ((bytesLeftInPacket & 0x00008000) == 0x00008000) {

            // High bit set.  Additional packets follow.
            additionalPackets = true;
            bytesLeftInPacket = (bytesLeftInPacket & 0x00007fff);
        }
        else {

            additionalPackets = false;
        }

        offset += 2;
        bytesLeftInBuffer -= 2;
        bytesLeftInPacket -= 2;
    }

    private void writeUInt16(int value) throws SWPException {

        SWPException.requireTrue(bytesLeftInBuffer >= 2, "Ran out of buffer room.");
        SWPConverter.convertUInt16BE(value, buffer, offset);
        offset += 2;
        bytesLeftInBuffer -= 2;
    }

    private void writeUInt16(int value, int offset) throws SWPException {

        SWPException.requireTrue(offset + 2 < buffer.length, "Ran out of buffer room.");
        SWPConverter.convertUInt16BE(value, buffer, offset);
        if (offset + 2 > this.offset) {

            this.offset = offset + 2;
        }
    }
}
