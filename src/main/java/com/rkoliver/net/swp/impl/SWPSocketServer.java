package com.rkoliver.net.swp.impl;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;

import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.SWPServer;
import com.rkoliver.net.swp.SWPSession;

public class SWPSocketServer implements SWPServer {

	private int port;
	private ServerSocket server;
	
	public SWPSocketServer(int port) {
		
		this.port = port;
	}
	
	@Override
	public void listen() throws SWPException {
		
		try {

			SocketAddress endpoint = new InetSocketAddress(port);
			server = new ServerSocket();
			server.bind(endpoint);
		}
		catch (IOException e) {
			
			throw SWPException.convert(e);
		}
	}

	@Override
	public boolean isListening() throws SWPException {

		return (server != null && !server.isClosed());
	}

	@Override
	public void close() throws SWPException {

		try {
			
			server.close();
		} 
		catch (IOException e) {

			throw SWPException.convert(e);
		}
		finally {
			
			server = null;
		}
	}

	@Override
	public SWPSession accept() throws SWPException {

		try {
			
			Socket socket = server.accept();
			return new SWPSocketSession(socket);
		}
		catch (IOException e) {

			throw SWPException.convert(e);
		}
	}
}
