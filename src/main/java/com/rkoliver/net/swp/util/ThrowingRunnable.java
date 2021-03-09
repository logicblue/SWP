package com.rkoliver.net.swp.util;

public interface ThrowingRunnable<E extends Throwable> {
    void run() throws E;
}
