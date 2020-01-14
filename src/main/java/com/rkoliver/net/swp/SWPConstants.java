package com.rkoliver.net.swp;

public class SWPConstants {

	public static final byte SWP_TOKEN_CP = 0x01;

	public static final int SWP_CP_CP = 0x00000100;
	public static final int SWP_CP_BYTE = 0x00000101;
	public static final int SWP_CP_SBYTE = 0x00000102;
	public static final int SWP_CP_USHORT = 0x00000103;
	public static final int SWP_CP_SHORT = 0x00000104;
	public static final int SWP_CP_UINT = 0x00000105;
	public static final int SWP_CP_INT = 0x00000106;
	public static final int SWP_CP_ULONG = 0x00000107;
	public static final int SWP_CP_LONG = 0x00000108;
	public static final int SWP_CP_VARINT = 0x00000109;
	public static final int SWP_CP_SINGLE = 0x0000010a;
	public static final int SWP_CP_DOUBLE = 0x0000010b;
	public static final int SWP_CP_DFP32 = 0x0000010c;
	public static final int SWP_CP_DFP64 = 0x0000010d;
	public static final int SWP_CP_DFP128 = 0x0000010e;
	public static final int SWP_CP_BYTES = 0x0000010f;
	public static final int SWP_CP_STRING_ASCII = 0x00000110;
	public static final int SWP_CP_STRING_UTF8 = 0x00000111;
	public static final int SWP_CP_STRING_UTF16 = 0x00000112;
	
	public static final byte NULL_INDICATOR = (byte)0x01;
	public static final byte NOT_NULL_INDICATOR = 0x00;
	
	public static final int SWP_UNKNOWN = 0xffffffff;
}
