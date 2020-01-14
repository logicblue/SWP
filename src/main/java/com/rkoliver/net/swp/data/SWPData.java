package com.rkoliver.net.swp.data;

import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.SWPReadable;
import com.rkoliver.net.swp.SWPWritable;
import com.rkoliver.net.swp.SwpCodePoint;

public abstract class SWPData implements SWPReadable, SWPWritable {

    public abstract int getWrittenLength();


    public static SWPReadable getInstance(int codePoint) throws SWPException {

        SWPException.requireTrue((codePoint & 0xf0000000) == 0x00000000, "Non-primitive codepoint.");

        switch (SwpCodePoint.fromValue(codePoint)) {

            case VARINT:
                return new SWPVarInt();
        }

        throw SWPException.invalidCodePoint(codePoint);
    }
}
