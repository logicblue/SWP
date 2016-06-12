package com.rkoliver.net.swp.test;

import org.junit.Test;

import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.data.SWPUVarInt;
import com.rkoliver.net.swp.util.New;

public class SWPVarIntTest { 
	
    @Test
	public void test() {
		
    	try {
    	
    		byte[] bytes = New.bytes(36);
    		bytes[0] = (byte)0x7F;
    		
    		bytes[1] = (byte)0xbf;
    		bytes[2] = (byte)0xff;
    		
    		bytes[3] = (byte)0xdf;
    		bytes[4] = (byte)0xff;
    		bytes[5] = (byte)0xff;
    		
    		bytes[6] = (byte)0xef;
    		bytes[7] = (byte)0xff;
    		bytes[8] = (byte)0xff;
    		bytes[9] = (byte)0xff;
    		
    		bytes[10] = (byte)0xf7;
    		bytes[11] = (byte)0xff;
    		bytes[12] = (byte)0xff;
    		bytes[13] = (byte)0xff;
    		bytes[14] = (byte)0xff;

    		bytes[15] = (byte)0xfb;
    		bytes[16] = (byte)0xff;
    		bytes[17] = (byte)0xff;
    		bytes[18] = (byte)0xff;
    		bytes[19] = (byte)0xff;
    		bytes[20] = (byte)0xff;
    		
    		bytes[21] = (byte)0xfd;
    		bytes[22] = (byte)0xff;
    		bytes[23] = (byte)0xff;
    		bytes[24] = (byte)0xff;
    		bytes[25] = (byte)0xff;
    		bytes[26] = (byte)0xff;
    		bytes[27] = (byte)0xff;

    		bytes[28] = (byte)0xfe;
    		bytes[29] = (byte)0xff;
    		bytes[30] = (byte)0xff;
    		bytes[31] = (byte)0xff;
    		bytes[32] = (byte)0xff;
    		bytes[33] = (byte)0xff;
    		bytes[34] = (byte)0xff;
    		bytes[35] = (byte)0xff;

    		int[] outLength = New.ints(1);
    		int length;
    		byte[] actual = New.bytes(8);
    		
    		SWPTestUtils.assertEquals(127, SWPUVarInt.decodeInt(bytes, 0, 1));
    		SWPTestUtils.assertEquals(127, SWPUVarInt.decodeInt(bytes, 0, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 1);
    		length = SWPUVarInt.encodeInt(127, actual, 0, 1);
    		SWPTestUtils.assertEquals(1, length);
    		SWPTestUtils.assertEquals(bytes, 0, 1, actual, 0);
    		
    		SWPTestUtils.assertEquals(16383, SWPUVarInt.decodeInt(bytes, 1, 2));
    		SWPTestUtils.assertEquals(16383, SWPUVarInt.decodeInt(bytes, 1, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 2);
    		length = SWPUVarInt.encodeInt(16383, actual, 0, 2);
    		SWPTestUtils.assertEquals(2, length);
    		SWPTestUtils.assertEquals(bytes, 1, 2, actual, 0);

    		SWPTestUtils.assertEquals(2097151, SWPUVarInt.decodeInt(bytes, 3, 3));
    		SWPTestUtils.assertEquals(2097151, SWPUVarInt.decodeInt(bytes, 3, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 3);
    		length = SWPUVarInt.encodeInt(2097151, actual, 0, 3);
    		SWPTestUtils.assertEquals(3, length);
    		SWPTestUtils.assertEquals(bytes, 3, 3, actual, 0);

    		SWPTestUtils.assertEquals(268435455, SWPUVarInt.decodeInt(bytes, 6, 4));
    		SWPTestUtils.assertEquals(268435455, SWPUVarInt.decodeInt(bytes, 6, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 4);
    		length = SWPUVarInt.encodeInt(268435455, actual, 0, 4);
    		SWPTestUtils.assertEquals(4, length);
    		SWPTestUtils.assertEquals(bytes, 6, 4, actual, 0);
    		
    		SWPTestUtils.assertEquals(34359738367L, SWPUVarInt.decodeLong(bytes, 10, 5));
    		SWPTestUtils.assertEquals(34359738367L, SWPUVarInt.decodeLong(bytes, 10, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 5);
    		length = SWPUVarInt.encodeLong(34359738367L, actual, 0, 5);
    		SWPTestUtils.assertEquals(5, length);
    		SWPTestUtils.assertEquals(bytes, 10, 5, actual, 0);

    		SWPTestUtils.assertEquals(4398046511103L, SWPUVarInt.decodeLong(bytes, 15, 6));
    		SWPTestUtils.assertEquals(4398046511103L, SWPUVarInt.decodeLong(bytes, 15, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 6);
    		length = SWPUVarInt.encodeLong(4398046511103L, actual, 0, 6);
    		SWPTestUtils.assertEquals(6, length);
    		SWPTestUtils.assertEquals(bytes, 15, 6, actual, 0);
    		
    		SWPTestUtils.assertEquals(562949953421311L, SWPUVarInt.decodeLong(bytes, 21, 7));
    		SWPTestUtils.assertEquals(562949953421311L, SWPUVarInt.decodeLong(bytes, 21, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 7);
    		length = SWPUVarInt.encodeLong(562949953421311L, actual, 0, 7);
    		SWPTestUtils.assertEquals(7, length);
    		SWPTestUtils.assertEquals(bytes, 21, 7, actual, 0);
    		
    		SWPTestUtils.assertEquals(72057594037927935L, SWPUVarInt.decodeLong(bytes, 28, 8));
    		SWPTestUtils.assertEquals(72057594037927935L, SWPUVarInt.decodeLong(bytes, 28, outLength));
    		SWPTestUtils.assertEquals(outLength[0], 8);
    		length = SWPUVarInt.encodeLong(72057594037927935L, actual, 0, 8);
    		SWPTestUtils.assertEquals(8, length);
    		SWPTestUtils.assertEquals(bytes, 28, 8, actual, 0);
    		
    		try {
    			
    			SWPUVarInt.decodeInt(bytes, 10, outLength);
    			SWPTestUtils.fail("Expected exception but none was thrown.");
    		}
    		catch (SWPException e) { }

    		try {
    			
    			SWPUVarInt.encodeLong(Long.MAX_VALUE, actual, 0, 8);
    			SWPTestUtils.fail("Expected exception but none was thrown.");
    		}
    		catch (SWPException e) { }

    		SWPUVarInt.encodeInt(Integer.MAX_VALUE, actual, 0, 5);
    		int i = SWPUVarInt.decodeInt(actual, 0, 5);
    		SWPTestUtils.assertEquals(Integer.MAX_VALUE, i);
    		
    		SWPUVarInt varint = new SWPUVarInt(123L);
    		SWPTestUtils.println(varint.bytesValue());    		
    	}
    	catch (SWPException e) {
    		
    		SWPTestUtils.fail(e.getMessage());
    	}
	}
}