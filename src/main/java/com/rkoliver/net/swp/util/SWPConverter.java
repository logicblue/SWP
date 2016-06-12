package com.rkoliver.net.swp.util;

public class SWPConverter {

	public static int convertUInt16BE(byte[] bytes, int offset) {
		
		return ((bytes[offset]) << 8 ) | (bytes[offset + 1] & 0xff);	
	}
}
