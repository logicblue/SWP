package com.rkoliver.net.swp.codepoint;

import com.rkoliver.net.swp.SWPException;

public enum CpPrimitive implements SWPCodePoint {
    ;


    @Override
    public int getCodePoint() {
        return 0;
    }


    @Override
    public int getLength() {
        return 0;
    }


    @Override
    public boolean isNull() {
        return false;
    }


    @Override
    public int read(byte[] bytes, int offset, int length) throws SWPException {
        return 0;
    }


    @Override
    public int write(byte[] bytes, int offset, int length) throws SWPException {
        return 0;
    }
}
