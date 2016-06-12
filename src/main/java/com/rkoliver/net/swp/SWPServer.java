package com.rkoliver.net.swp;

public interface SWPServer {

	void listen() throws SWPException;
	boolean isListening() throws SWPException;
	void close() throws SWPException;
	SWPSession accept() throws SWPException;
}
