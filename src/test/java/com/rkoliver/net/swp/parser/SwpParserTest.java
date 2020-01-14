package com.rkoliver.net.swp.parser;

import com.rkoliver.net.swp.SWPConstants;
import com.rkoliver.net.swp.util.New;
import org.junit.Test;

public class SwpParserTest {
    @Test
    public void test() {
        byte[] bytes = New.bytes(
                SWPConstants.SWP_TOKEN_CP,
                (byte) (SWPConstants.SWP_CP_STRING_UTF8 >> 8),
                (byte) (SWPConstants.SWP_CP_STRING_UTF8 & 0x00ff),
                SWPConstants.NOT_NULL_INDICATOR,
                (byte) 0x00,
                (byte) 0x0a,
                (byte) 0x00,
                (byte) 0x68,
                (byte) 0x00,
                (byte) 0x65,
                (byte) 0x00,
                (byte) 0x6c,
                (byte) 0x00,
                (byte) 0x6c,
                (byte) 0x00,
                (byte) 0x6f
        );

        SwpParser parser = new SwpParser(
                SwpParser.tokenCodePoint(
                        SwpParser.codePointStringUTF8()
                )
        );
    }
}
