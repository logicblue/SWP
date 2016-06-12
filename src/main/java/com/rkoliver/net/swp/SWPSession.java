package com.rkoliver.net.swp;

public interface SWPSession {
	
	void open() throws SWPException;
	void close() throws SWPException;
	boolean isOpen() throws SWPException;
	void write(SWPWritable writable) throws SWPException;
	SWPReadable read() throws SWPException;
	void send() throws SWPException;
	void receive() throws SWPException;
}