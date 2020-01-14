package com.rkoliver.net.swp.test;

import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.data.SWPUVarInt;
import com.rkoliver.net.swp.util.New;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

public class SWPVarIntTest {

    @Test
    public void test() {

        try {

            byte[] bytes = New.bytes(36);
            bytes[0] = (byte) 0x7F;

            bytes[1] = (byte) 0xbf;
            bytes[2] = (byte) 0xff;

            bytes[3] = (byte) 0xdf;
            bytes[4] = (byte) 0xff;
            bytes[5] = (byte) 0xff;

            bytes[6] = (byte) 0xef;
            bytes[7] = (byte) 0xff;
            bytes[8] = (byte) 0xff;
            bytes[9] = (byte) 0xff;

            bytes[10] = (byte) 0xf7;
            bytes[11] = (byte) 0xff;
            bytes[12] = (byte) 0xff;
            bytes[13] = (byte) 0xff;
            bytes[14] = (byte) 0xff;

            bytes[15] = (byte) 0xfb;
            bytes[16] = (byte) 0xff;
            bytes[17] = (byte) 0xff;
            bytes[18] = (byte) 0xff;
            bytes[19] = (byte) 0xff;
            bytes[20] = (byte) 0xff;

            bytes[21] = (byte) 0xfd;
            bytes[22] = (byte) 0xff;
            bytes[23] = (byte) 0xff;
            bytes[24] = (byte) 0xff;
            bytes[25] = (byte) 0xff;
            bytes[26] = (byte) 0xff;
            bytes[27] = (byte) 0xff;

            bytes[28] = (byte) 0xfe;
            bytes[29] = (byte) 0xff;
            bytes[30] = (byte) 0xff;
            bytes[31] = (byte) 0xff;
            bytes[32] = (byte) 0xff;
            bytes[33] = (byte) 0xff;
            bytes[34] = (byte) 0xff;
            bytes[35] = (byte) 0xff;

            int[] outLength = New.ints(1);
            int length;
            byte[] actual = New.bytes(8);

            assertEquals(SWPUVarInt.decodeInt(bytes, 0, 1), 127);
            assertEquals(SWPUVarInt.decodeInt(bytes, 0, outLength), 127);
            assertEquals(1, outLength[0]);
            length = SWPUVarInt.encodeInt(127, actual, 0, 1);
            assertEquals(length, 1);
            SWPTestUtils.assertEquals(actual, 0, bytes, 0, 1);

            assertEquals(SWPUVarInt.decodeInt(bytes, 1, 2), 16383);
            assertEquals(SWPUVarInt.decodeInt(bytes, 1, outLength), 16383);
            assertEquals(2, outLength[0]);
            length = SWPUVarInt.encodeInt(16383, actual, 0, 2);
            assertEquals(length, 2);
            SWPTestUtils.assertEquals(actual, 0, bytes, 1, 2);

            assertEquals(SWPUVarInt.decodeInt(bytes, 3, 3), 2097151);
            assertEquals(SWPUVarInt.decodeInt(bytes, 3, outLength), 2097151);
            assertEquals(3, outLength[0]);
            length = SWPUVarInt.encodeInt(2097151, actual, 0, 3);
            assertEquals(length, 3);
            SWPTestUtils.assertEquals(actual, 0, bytes, 3, 3);

            assertEquals(SWPUVarInt.decodeInt(bytes, 6, 4), 268435455);
            assertEquals(SWPUVarInt.decodeInt(bytes, 6, outLength), 268435455);
            assertEquals(4, outLength[0]);
            length = SWPUVarInt.encodeInt(268435455, actual, 0, 4);
            assertEquals(length, 4);
            SWPTestUtils.assertEquals(actual, 0, bytes, 6, 4);

            assertEquals(SWPUVarInt.decodeLong(bytes, 10, 5), 34359738367L);
            assertEquals(SWPUVarInt.decodeLong(bytes, 10, outLength), 34359738367L);
            assertEquals(5, outLength[0]);
            length = SWPUVarInt.encodeLong(34359738367L, actual, 0, 5);
            assertEquals(length, 5);
            SWPTestUtils.assertEquals(actual, 0, bytes, 10, 5);

            assertEquals(SWPUVarInt.decodeLong(bytes, 15, 6), 4398046511103L);
            assertEquals(SWPUVarInt.decodeLong(bytes, 15, outLength), 4398046511103L);
            assertEquals(6, outLength[0]);
            length = SWPUVarInt.encodeLong(4398046511103L, actual, 0, 6);
            assertEquals(length, 6);
            SWPTestUtils.assertEquals(actual, 0, bytes, 15, 6);

            assertEquals(SWPUVarInt.decodeLong(bytes, 21, 7), 562949953421311L);
            assertEquals(SWPUVarInt.decodeLong(bytes, 21, outLength), 562949953421311L);
            assertEquals(7, outLength[0]);
            length = SWPUVarInt.encodeLong(562949953421311L, actual, 0, 7);
            assertEquals(length, 7);
            SWPTestUtils.assertEquals(actual, 0, bytes, 21, 7);

            assertEquals(SWPUVarInt.decodeLong(bytes, 28, 8), 72057594037927935L);
            assertEquals(SWPUVarInt.decodeLong(bytes, 28, outLength), 72057594037927935L);
            assertEquals(8, outLength[0]);
            length = SWPUVarInt.encodeLong(72057594037927935L, actual, 0, 8);
            assertEquals(length, 8);
            SWPTestUtils.assertEquals(actual, 0, bytes, 28, 8);

            try {

                SWPUVarInt.decodeInt(bytes, 10, outLength);
                fail("Expected exception but none was thrown.");
            }
            catch (SWPException e) {
            }

            try {

                SWPUVarInt.encodeLong(Long.MAX_VALUE, actual, 0, 8);
                fail("Expected exception but none was thrown.");
            }
            catch (SWPException e) {
            }

            SWPUVarInt.encodeInt(Integer.MAX_VALUE, actual, 0, 5);
            int i = SWPUVarInt.decodeInt(actual, 0, 5);
            assertEquals(i, Integer.MAX_VALUE);

            SWPUVarInt varint = new SWPUVarInt(123L);
            SWPTestUtils.println(varint.bytesValue());
        }
        catch (SWPException e) {

            fail(e.getMessage());
        }
    }
}