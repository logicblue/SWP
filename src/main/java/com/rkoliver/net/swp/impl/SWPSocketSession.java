package com.rkoliver.net.swp.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.SWPFactory;
import com.rkoliver.net.swp.SWPReadable;
import com.rkoliver.net.swp.SWPSession;
import com.rkoliver.net.swp.SWPWritable;
import com.rkoliver.net.swp.codepoint.SWPCodePoint;
import com.rkoliver.net.swp.util.New;
import com.rkoliver.net.swp.util.SWPConverter;

public class SWPSocketSession implements SWPSession {

	private final String host;
	private final int port;
	private Socket socket;
	private OutputStream out;
	private InputStream in;
	private byte[] buffer = New.bytes(0x00007fff);
	private int offset = 0;
	private int length = buffer.length;

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
		
		int originalLength = length;
		int codePointLength = readUInt16();
		int overhead = 4;
		if ((codePointLength & 0x80000000) == 0x80000000) {
			
			// 4-byte length
			codePointLength = (codePointLength << 16 | readUInt16()) & 0x7fffffff;
			overhead += 2;
		}
		
		int codePoint = readUInt16();
		if ((codePoint & 0x80000000) == 0x80000000) {
			
			// 4-byte codepoint
			codePoint = (codePoint << 16 | readUInt16()) & 0x7fffffff;
			overhead += 2;
		}
		
		if (codePointLength > originalLength) {
		
			throw SWPException.partialCodePoint();
		}
		
		SWPCodePoint cp = SWPFactory.getCodePoint(codePoint);
		int i = cp.read(buffer, offset, codePointLength - overhead);
		SWPException.requireTrue(i == codePointLength - overhead, "Actual codepoint length did not match reported codepoint length.");
		offset += i;
		length -= i;
		return cp;
	}

	@Override
	public void write(SWPWritable writable) throws SWPException {

		SWPException.requireNonNull(writable, "writable must not be null.");
		int i = writable.write(buffer, offset, length);
		offset += i;
		length -= i;
	}

	@Override
	public void send() throws SWPException {

		SWPException.requireTrue(isOpen(), "Session is not open.");

		try {
			
			out.write(buffer, 0, offset);
			out.flush();
		} 
		catch (IOException e) {

			throw SWPException.convert(e);
		}
		finally {
		
			offset = 0;
			length = buffer.length;
		}
	}

	@Override
	public void receive() throws SWPException {

		SWPException.requireTrue(isOpen(), "Session is not open.");

		try {
			
			length = in.read(buffer, 0, buffer.length);
		} 
		catch (IOException e) {

			length = 0;
			throw SWPException.convert(e);
		}
		finally {
			
			offset = 0;
		}
	}

	private int readUInt16() throws SWPException {

		SWPException.requireTrue(length >= 2, "No more data available.");
		int value = SWPConverter.convertUInt16BE(buffer, offset);
		offset += 2;
		length -= 2;
		return value;
	}
}
