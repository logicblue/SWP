package com.rkoliver.net.swp.test;


import com.rkoliver.net.swp.*;
import com.rkoliver.net.swp.codepoint.SWPPrimitive;
import com.rkoliver.net.swp.data.SWPVarInt;
import com.rkoliver.net.swp.impl.SWPSocketServer;
import com.rkoliver.net.swp.impl.SWPSocketSession;
import org.testng.annotations.Test;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.fail;

public class SWPTest {

    private int port = SwpConstants.SWP_UNKNOWN;

    @Test
    public void test() {

        try {

            Thread serverThread = new Thread(this::server);

            serverThread.start();
            Thread.sleep(1000);
            client();
            serverThread.join();
        } catch (InterruptedException e) {

            fail(e.getMessage());
        }
    }


    private void server() {

        try {
            SWPServer server = null;
            port = SwpConstants.SWP_UNKNOWN;
            int retries = 3;
            for (int i = 0; i < retries; i++) {
                try {
                    port = new Random().nextInt(65535 - 49152) + 49152;
                    server = new SWPSocketServer(port);
                    server.listen();
                    break;
                }
                catch (SWPException e) {
                    if (i == retries - 1) {
                        throw e;
                    }
                }
            }
            SWPSession session = server.accept();
            session.receive();
            for (SWPReadable readable = session.read(); readable != null; readable = session.read()) {

                SWPTestUtils.println(readable.toString());
            }
            server.close();
        } catch (SWPException e) {

            fail(e.getMessage());
        }
    }


    private void client() {

        try {

            SWPSession session = new SWPSocketSession(port);
            SWPVarInt varint = new SWPVarInt(1234567890);
            SWPPrimitive<SWPVarInt> cp = new SWPPrimitive<>(SwpCodePoint.VARINT.getValue(), varint);
            session.write(cp);
            session.openWithRetry(3, 10, TimeUnit.SECONDS);
            session.send();
            session.close();
        } catch (SWPException e) {

            fail(e.getMessage());
        }
    }
}
