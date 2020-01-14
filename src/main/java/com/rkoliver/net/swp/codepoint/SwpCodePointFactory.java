package com.rkoliver.net.swp.codepoint;

public interface SwpCodePointFactory<T extends SWPCodePoint> {
    T createNull();
    T create(byte[] bytes, int offset, int length);
}
