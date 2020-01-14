package com.rkoliver.net.swp;

public enum SwpCodePoint {
    CODEPOINT(0x00000100),
    BYTE(0x00000101),
    SBYTE(0x00000102),
    USHORT(0x00000103),
    SHORT(0x00000104),
    UINT(0x00000105),
    INT(0x00000106),
    ULONG(0x00000107),
    LONG(0x00000108),
    VARINT(0x00000109),
    SINGLE(0x0000010a),
    DOUBLE(0x0000010b),
    DFP32(0x0000010c),
    DFP64(0x0000010d),
    DFP128(0x0000010e),
    BYTES(0x0000010f),
    STRING_ASCII(0x00000110),
    STRING_UTF8(0x00000111),
    STRING_UTF16(0x00000112);

    private final int value;


    SwpCodePoint(int value) {
        this.value = value;
    }


    public int getValue() {
        return value;
    }


    public static SwpCodePoint fromValue(int value) throws SWPException {
        for (SwpCodePoint codePoint : values()) {
            if (codePoint.value == value) {
                return codePoint;
            }
        }
        throw SWPException.invalidCodePoint(value);
    }
}
