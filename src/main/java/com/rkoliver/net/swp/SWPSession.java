package com.rkoliver.net.swp;

import java.util.concurrent.TimeUnit;

public interface SWPSession {

    void open() throws SWPException;

    void openWithRetry(int times, long pause, TimeUnit timeUnit) throws SWPException;

    void close() throws SWPException;

    boolean isOpen() throws SWPException;

    void write(SWPWritable writable) throws SWPException;

    SWPReadable read() throws SWPException;

    void send() throws SWPException;

    void receive() throws SWPException;
}