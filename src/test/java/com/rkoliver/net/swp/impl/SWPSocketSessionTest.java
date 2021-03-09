package com.rkoliver.net.swp.impl;

import com.rkoliver.net.swp.SWPException;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class SWPSocketSessionTest {

    @Test
    public void testOpenWithRetry() {
        SWPSocketSession session = new SWPSocketSession(54321);
        long start = System.currentTimeMillis();
        try {
            session.openWithRetry(3, 10, TimeUnit.SECONDS);
        }
        catch (SWPException ignored) {
            // expected
        }
        long end = System.currentTimeMillis();
        long duration = (end - start) / 1000;
        assertTrue(duration > 25 && duration < 35, "Total time to retry was not expected (" + duration + ")");
    }
}