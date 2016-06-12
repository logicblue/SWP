package com.rkoliver.net.swp.data;

import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.util.New;

public class SWPVarInt extends SWPData {
	
	public static final int MIN_1_BYTE_VALUE = -63;
	public static final int MAX_1_BYTE_VALUE = 63;
	
	public static final int MIN_2_BYTE_VALUE = -8191;
	public static final int MAX_2_BYTE_VALUE = 8191;

	public static final int MIN_3_BYTE_VALUE = -1048575;
	public static final int MAX_3_BYTE_VALUE = 1048575;

	public static final int MIN_4_BYTE_VALUE = -134217727;
	public static final int MAX_4_BYTE_VALUE = 134217727;

	public static final long MIN_5_BYTE_VALUE = -17179869183L;
	public static final long MAX_5_BYTE_VALUE = 17179869183L;

	public static final long MIN_6_BYTE_VALUE = -2199023255551L;
	public static final long MAX_6_BYTE_VALUE = 2199023255551L;

	public static final long MIN_7_BYTE_VALUE = -281474976710655L;
	public static final long MAX_7_BYTE_VALUE = 281474976710655L;

	public static final long MIN_8_BYTE_VALUE = -36028797018963967L;
	public static final long MAX_8_BYTE_VALUE = 36028797018963967L;

	public static final long MIN_VALUE = MIN_8_BYTE_VALUE;
	public static final long MAX_VALUE = MAX_8_BYTE_VALUE;

	private long value;
	private int[] outLength = New.ints(1);
	
	public SWPVarInt(int i) {
	
		value = i;
	}
	
	public SWPVarInt(long l) throws SWPException { 
	
		if (l < MIN_VALUE || l > MAX_VALUE) {
			
			throw SWPException.numericOverflow();
		}
		
		value = l;
	}
	
	public SWPVarInt(byte[] bytes, int offset, int length) throws SWPException {
	
		value = decodeLong(bytes, offset, length);
	}
	
	public SWPVarInt(byte[] bytes, int offset) throws SWPException {
		
		value = decodeLong(bytes, offset, null);
	}
	
	public SWPVarInt() {

		this(0);
	}

	public int intValue() throws SWPException {
		
		if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
			
			throw SWPException.numericOverflow();
		}
		
		return (int)value;
	}
	
	public byte[] bytesValue() throws SWPException {
		
		byte[] bytes = new byte[getMinimumLength(value)];
		encodeLong(value, bytes, 0, bytes.length);
		return bytes;
	}
	
	public long longValue() {
		
		return value;
	}
	
	public static int decodeInt(byte[] bytes, int offset, int length) throws SWPException {
	
		long l = decodeLong(bytes, offset, length);
		if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
			
			throw SWPException.numericOverflow();
		}
		
		return (int)l;
	}
	
	public static int decodeInt(byte[] bytes, int offset, int[] outLength) throws SWPException {

		SWPException.requireNonNull(bytes, "bytes must not be null.");
		SWPException.requireTrue(offset >= 0 && offset < bytes.length, "Invalid offset.");

		outLength[0] = decodeLength(bytes[offset]);
		return decodeInt(bytes, offset, outLength[0]);		
	}
	
	public static long decodeLong(byte[] bytes, int offset, int length) throws SWPException {
		
		SWPException.requireNonNull(bytes, "bytes must not be null.");
		SWPException.requireTrue(offset >= 0 && offset < bytes.length, "Invalid offset.");
		SWPException.requireTrue(offset + length <= bytes.length, "Invalid length.");
		
		byte b0 = bytes[offset];		
		byte b1;
		byte b2;
		byte b3;
		byte b4;
		byte b5;
		byte b6;
		byte b7;
				
		int i0;
		int i1;

		int decodedLength = decodeLength(b0);
		SWPException.dataErrorIfFalse(decodedLength == length, "Invalid length provided for VarInt."); 

		switch (decodedLength) {
		
			case 1:
				
				if ((b0 & 0x40) == 0x40) {
					
					return -(b0 & 0x3f);
				}
				
				return b0;
				
			case 2:

				b1 = bytes[offset + 1];
				
				if ((b0 & 0xa0) == 0xa0) {
					
					return -(long)((b0 & 0x1f) << 8 | b1 & 0xff);
				}
				
				return (long)((b0 & 0x3f) << 8 | b1 & 0xff);
				
			case 3:

				b1 = bytes[offset + 1];
				b2 = bytes[offset + 2];				
				return (long)((b0 & 0x1f) << 16 | (b1 & 0xff) << 8 | b2 & 0xff);
				
			case 4:

				b1 = bytes[offset + 1];
				b2 = bytes[offset + 2];
				b3 = bytes[offset + 3];
				return (long)((b0 & 0x0f) << 24 | (b1 & 0xff) << 16 | (b2 & 0xff) << 8 | b3 & 0xff);

			case 5:

				b1 = bytes[offset + 1];
				b2 = bytes[offset + 2];
				b3 = bytes[offset + 3];
				b4 = bytes[offset + 4];
				
				i0 = (int)(b0 & 0x07);
				i1 = (int)((b1 & 0xff) << 24 | (b2 & 0xff) << 16 | (b3 & 0xff) << 8 | b4 & 0xff);
				return (i0 & 0xffffffffL) << 32 | i1 & 0xffffffffL;

			case 6:

				b1 = bytes[offset + 1];
				b2 = bytes[offset + 2];
				b3 = bytes[offset + 3];
				b4 = bytes[offset + 4];
				b5 = bytes[offset + 5];

				i0 = (int)((b0 & 0x03) << 8 | b1 & 0xff);
				i1 = (int)((b2 & 0xff) << 24 | (b3 & 0xff) << 16 | (b4 & 0xff) << 8 | b5 & 0xff);
				return (i0 & 0xffffffffL) << 32 | i1 & 0xffffffffL;

			case 7:

				b1 = bytes[offset + 1];
				b2 = bytes[offset + 2];
				b3 = bytes[offset + 3];
				b4 = bytes[offset + 4];
				b5 = bytes[offset + 5];
				b6 = bytes[offset + 6];

				i0 = (int)((b0 & 0x01) << 16 | (b1 & 0xff) << 8 | b2 & 0xff);
				i1 = (int)((b3 & 0xff) << 24 | (b4 & 0xff) << 16 | (b5 & 0xff) << 8 | b6 & 0xff);
				return (i0 & 0xffffffffL) << 32 | i1 & 0xffffffffL;

			case 8:

				b1 = bytes[offset + 1];
				b2 = bytes[offset + 2];
				b3 = bytes[offset + 3];
				b4 = bytes[offset + 4];
				b5 = bytes[offset + 5];
				b6 = bytes[offset + 6];
				b7 = bytes[offset + 7];
				
				i0 = (int)((b0 & 0x00) << 24 | (b1 & 0xff) << 16 | (b2 & 0xff) << 8 | b3 & 0xff);
				i1 = (int)((b4 & 0xff) << 24 | (b5 & 0xff) << 16 | (b6 & 0xff) << 8 | b7 & 0xff);
				return (i0 & 0xffffffffL) << 32 | i1 & 0xffffffffL;
		}
		
		throw SWPException.dataError("Invalid VarInt detected.");
	}

	public static long decodeLong(byte[] bytes, int offset, int[] outLength) throws SWPException {
		
		SWPException.requireNonNull(bytes, "bytes must not be null.");
		SWPException.requireTrue(offset >= 0 && offset < bytes.length, "Invalid offset.");

		if (outLength == null) {
			
			outLength = New.ints(1);
		}
		
		outLength[0] = decodeLength(bytes[offset]);
		return decodeLong(bytes, offset, outLength[0]);
	}
	
	private static int decodeLength(byte b0) throws SWPException {

		// 0xxx xxxx
		if ((b0 & 0x80) == 0x00) {

			return 1;
		}
		
		// 10xx xxxx xxxx xxxx
		if ((b0 & 0xc0) == 0x80) {

			return 2;
		}
		
		// 110x xxxx xxxx xxxx xxxx xxxx
		if ((b0 & 0xe0) == 0xc0) {

			return 3;
		}

		// 1110 xxxx xxxx xxxx xxxx xxxx xxxx xxxx
		if ((b0 & 0xf0) == 0xe0) {

			return 4;
		}

		// 1111 0xxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx
		if ((b0 & 0xf8) == 0xf0) {
			
			return 5;
		}
		
		// 1111 10xx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx
		if ((b0 & 0xfc) == 0xf8) {
			
			return 6;
		}
		
		// 1111 110x xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx
		if ((b0 & 0xfe) == 0xfc) {
			
			return 7;
		}
		
		// 1111 1110 xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx xxxx
		if ((b0 & 0xff) == 0xfe) {
			
			return 8;
		}

		throw SWPException.dataError("Invalid VarInt detected.");
	}


	public static int encodeInt(int i, byte[] bytes, int offset, int length) throws SWPException {

		SWPException.requireNonNull(bytes, "bytes must not be null.");
		SWPException.requireTrue(offset >= 0 && offset < bytes.length, "Invalid offset.");
		SWPException.requireTrue(offset + length <= bytes.length, "Invalid length.");
		
		int minLength = getMinimumLength(i);
		SWPException.requireTrue(length >= minLength, "Not enough room in bytes.");
		
		switch (minLength) {
		
			case 1: 

				bytes[offset] = (byte)(i & 0x7f);
				break;
		
			case 2:
			
				bytes[offset] = (byte)(i >>> 8 | 0x80);
				bytes[offset + 1] = (byte)i;
				break;
				
			case 3:
			
				bytes[offset] = (byte)(i >>> 16 | 0xc0);
				bytes[offset + 1] = (byte)(i >>> 8);
				bytes[offset + 2] = (byte)i;
				break;

			case 4:
			
				bytes[offset] = (byte)(i >>> 24 | 0xe0);
				bytes[offset + 1] = (byte)(i >>> 16);
				bytes[offset + 2] = (byte)(i >>> 8);
				bytes[offset + 3] = (byte)i;
				break;

			case 5:
				
				bytes[offset] = (byte)0xf0;
				bytes[offset + 1] = (byte)(i >>> 24);
				bytes[offset + 2] = (byte)(i >>> 16);
				bytes[offset + 3] = (byte)(i >>> 8);
				bytes[offset + 4] = (byte)i;
				break;
				
			default:
				
				throw SWPException.dataError("Unexpected error.");
		}
		
		return minLength;
	}
	
	public static int encodeLong(long l, byte[] bytes, int offset, int length) throws SWPException {
		
		SWPException.requireNonNull(bytes, "bytes must not be null.");
		SWPException.requireTrue(offset >= 0 && offset < bytes.length, "Invalid offset.");
		SWPException.requireTrue(offset + length <= bytes.length, "Invalid length.");

		int minLength = getMinimumLength(l);
		if (minLength > 0 && minLength <= 4) {
			
			return encodeInt((int)l, bytes, offset, length);
		}
		
		SWPException.requireTrue(length >= minLength, "Not enough room in bytes.");

		switch (minLength) {
				
			case 5:
				
				bytes[offset] = (byte)((l >>> 32) | 0xf0);
				bytes[offset + 1] = (byte)(l >>> 24);
				bytes[offset + 2] = (byte)(l >>> 16);
				bytes[offset + 3] = (byte)(l >>> 8);
				bytes[offset + 4] = (byte)l;
				break;
			
			case 6:
				
				bytes[offset] = (byte)((l >>> 40) | 0xf8);
				bytes[offset + 1] = (byte)(l >>> 32);
				bytes[offset + 2] = (byte)(l >>> 24);
				bytes[offset + 3] = (byte)(l >>> 16);
				bytes[offset + 4] = (byte)(l >>> 8);
				bytes[offset + 5] = (byte)l;
				break;
				
			case 7:
				
				bytes[offset] = (byte)((l >>> 48) | 0xfc);
				bytes[offset + 1] = (byte)(l >>> 40);
				bytes[offset + 2] = (byte)(l >>> 32);
				bytes[offset + 3] = (byte)(l >>> 24);
				bytes[offset + 4] = (byte)(l >>> 16);
				bytes[offset + 5] = (byte)(l >>> 8);
				bytes[offset + 6] = (byte)l;
				break;
				
			case 8:
				
				bytes[offset] = (byte)((l >>> 56) | 0xfe);
				bytes[offset + 1] = (byte)(l >>> 48);
				bytes[offset + 2] = (byte)(l >>> 40);
				bytes[offset + 3] = (byte)(l >>> 32);
				bytes[offset + 4] = (byte)(l >>> 24);
				bytes[offset + 5] = (byte)(l >>> 16);
				bytes[offset + 6] = (byte)(l >>> 8);
				bytes[offset + 7] = (byte)l;
				break;
				
				default:
		
					throw SWPException.numericOverflow();
		}
		
		return minLength;
	}
	
	public static int getMinimumLength(int i) {
		
		return getMinimumLength((long)i);
	}

	public static int getMinimumLength(long l) {

		if (l >= MIN_1_BYTE_VALUE && l <= MAX_1_BYTE_VALUE) {
			
			return 1;
		}
		
		if (l >= MIN_2_BYTE_VALUE && l <= MAX_2_BYTE_VALUE) {
			
			return 2;
		}
		
		if (l >= MIN_3_BYTE_VALUE && l <= MAX_3_BYTE_VALUE) {
			
			return 3;
		}
		
		if (l >= MIN_4_BYTE_VALUE && l <= MAX_4_BYTE_VALUE) {
			
			return 4;
		}

		if (l >= MIN_5_BYTE_VALUE && l <= MAX_5_BYTE_VALUE) {
			
			return 5;		
		}
		
		if (l >= MIN_6_BYTE_VALUE && l <= MAX_6_BYTE_VALUE) {
			
			return 6;
		}
		
		if (l >= MIN_7_BYTE_VALUE && l <= MAX_7_BYTE_VALUE) {
			
			return 7;
		}
		
		if (l >= MIN_8_BYTE_VALUE && l <= MAX_8_BYTE_VALUE) {
			
			return 8;
		}
		
		return 0;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) throws SWPException {

		return encodeLong(value, bytes, offset, length);
	}

	@Override
	public int read(byte[] bytes, int offset, int length) throws SWPException {

		value = decodeLong(bytes, offset, outLength);
		return outLength[0];
	}
	
	@Override
	public String toString() {
		
		return Long.toString(value);
	}

	@Override
	public int getWrittenLength() {

		return getMinimumLength(value);
	}
}
