package com.rkoliver.net.swp.util;

public class SWPConverter {

	public static int convertUInt16BE(byte[] bytes, int offset) {
		
		return ((bytes[offset]) << 8 ) | (bytes[offset + 1] & 0xff);	
	}

	public static void convertUInt16BE(int value, byte[] buffer, int offset) {
		buffer[offset] = (byte)((value >> 8) & 0xff);
		buffer[offset + 1] = (byte)(value & 0xff);
	}
}
