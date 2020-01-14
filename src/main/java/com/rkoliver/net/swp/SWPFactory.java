package com.rkoliver.net.swp;

import com.rkoliver.net.swp.codepoint.SWPCodePoint;
import com.rkoliver.net.swp.codepoint.SWPPrimitive;
import com.rkoliver.net.swp.data.SWPVarInt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class SWPFactory {

    static private SWPFactory primitiveFactory;
    static private SWPFactory objectFactory;
    static private SWPFactory subProtocolPrimitiveFactory;
    static private SWPFactory subProtocolObjectFactory;


    public static SWPCodePoint getCodePoint(int codePoint) throws SWPException {
        SWPFactory factory = getFactory(codePoint);

        if (factory == null) {

            throw SWPException.codePointFactoryNotFound(codePoint);
        }

        SWPCodePoint codePointObject = factory.createCodePoint(codePoint);
        return codePointObject;
    }


    public static void releaseCodePoint(SWPCodePoint codePoint) throws SWPException {

        SWPException.requireNonNull(codePoint, "codePoint must not be null.");

        SWPFactory factory = getFactory(codePoint.getCodePoint());

        if (factory == null) {

            throw SWPException.codePointFactoryNotFound(codePoint.getCodePoint());
        }

        factory.cacheCodePoint(codePoint);
    }


    private static SWPFactory getFactory(int codePoint) {

        switch (codePoint & 0xf0000000) {

            case 0x00000000:
                return primitiveFactory;
            case 0x10000000:
                return objectFactory;
            case 0x20000000:
                return subProtocolPrimitiveFactory;
            case 0x30000000:
                return subProtocolObjectFactory;
        }

        return null;
    }


    public abstract SWPCodePoint createCodePoint(int codePoint);

    public abstract void cacheCodePoint(SWPCodePoint codePoint);


    static {

        primitiveFactory = new SWPFactory() {

            private Map<Integer, List<SWPCodePoint>> cache = new HashMap<>();


            @Override
            public SWPCodePoint createCodePoint(int codePoint) {

                switch (SwpCodePoint.fromValue(codePoint)) {

                    case VARINT:
                        return new SWPPrimitive<SWPVarInt>(codePoint);
                }

                return null;
            }


            @Override
            public void cacheCodePoint(SWPCodePoint codePoint) {

                if (codePoint == null) {

                    return;
                }

                Integer key = Integer.valueOf(codePoint.getCodePoint());
                List<SWPCodePoint> value = cache.get(key);
                if (value == null) {

                    value = new ArrayList<>();
                    cache.put(key, value);
                }

                value.add(codePoint);
            }
        };

        objectFactory = new SWPFactory() {

            private Map<Integer, List<SWPCodePoint>> cache = new HashMap<>();


            @Override
            public SWPCodePoint createCodePoint(int codePoint) {

                return null;
            }


            @Override
            public void cacheCodePoint(SWPCodePoint codePoint) {

                if (codePoint == null) {

                    return;
                }

                Integer key = Integer.valueOf(codePoint.getCodePoint());
                List<SWPCodePoint> value = cache.get(key);
                if (value == null) {

                    value = new ArrayList<>();
                    cache.put(key, value);
                }

                value.add(codePoint);
            }
        };
    }
}
