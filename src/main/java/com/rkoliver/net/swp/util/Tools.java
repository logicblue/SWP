package com.rkoliver.net.swp.util;

import com.rkoliver.net.swp.SWPException;

import java.util.concurrent.TimeUnit;

public class Tools {
    static public void retry(ThrowingRunnable<SWPException> action, int times, long pause, TimeUnit timeUnit) throws SWPException {
        int retriesRemaining = times;
        SWPException exception;

        while (true) {
            try {
                action.run();
                return;
            }
            catch (SWPException e) {
                exception = e;
            }

            if (retriesRemaining > 0) {
                try {
                    timeUnit.sleep(pause);
                }
                catch (InterruptedException ignored) {
                    // no-op
                }
                retriesRemaining--;
                continue;
            }
            break;
        }

        throw exception;

    }
}
