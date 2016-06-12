package com.rkoliver.net.swp.test;

import org.junit.Test;

import com.rkoliver.net.swp.SWPConstants;
import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.SWPReadable;
import com.rkoliver.net.swp.SWPServer;
import com.rkoliver.net.swp.SWPSession;
import com.rkoliver.net.swp.codepoint.SWPPrimitive;
import com.rkoliver.net.swp.data.SWPVarInt;
import com.rkoliver.net.swp.impl.SWPSocketServer;
import com.rkoliver.net.swp.impl.SWPSocketSession;

public class SWPTest {

	private final static int PORT = 55555;
	
	@Test
	public void test() {
		
		try {

			Thread serverThread = new Thread() { 
				
				@Override
				public void run() {
					
					server();
				}
			};
			
			serverThread.start();
			client();
			serverThread.join();
		} 
		catch (InterruptedException e) {

			SWPTestUtils.fail(e);
		}
	}
	
	private void server() {
		
		try {
		
			SWPServer server = new SWPSocketServer(PORT);
			server.listen();
			SWPSession session = server.accept();
			session.receive();
			for (SWPReadable readable = session.read(); readable != null; readable = session.read()) {
				
				SWPTestUtils.println(readable.toString());
			}
			server.close();
		}
		catch (SWPException e) {
			
			SWPTestUtils.fail(e);
		}
	}
	
	private void client() {
		
		try {
			
			SWPSession session = new SWPSocketSession(null, PORT);
			SWPVarInt varint = new SWPVarInt(1234567890);
			SWPPrimitive<SWPVarInt> cp = new SWPPrimitive<>(SWPConstants.SWP_CP_VARINT, varint);
			session.write(cp);
			session.open();
			session.send();
			session.close();
		}
		catch (SWPException e) {
			
			SWPTestUtils.fail(e);
		}
	}
}
