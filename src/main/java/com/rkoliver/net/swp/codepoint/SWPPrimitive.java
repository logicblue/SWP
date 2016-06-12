package com.rkoliver.net.swp.codepoint;

import com.rkoliver.net.swp.SWPConstants;
import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.SWPReadable;
import com.rkoliver.net.swp.data.SWPData;

public class SWPPrimitive<T extends SWPData> implements SWPCodePoint {

	private int codePoint;
	T primitive;
	
	public SWPPrimitive(int codePoint) {
		
		this.codePoint = codePoint;
	}

	public SWPPrimitive(int codePoint, T primitive) {
		
		this.codePoint = codePoint;
		this.primitive = primitive;
	}

	public void setPrimitive(T primitive) {
		
		this.primitive = primitive;
	}

	public T getPrimitive() {
		
		return primitive;
	}
	
	@Override
	public boolean isNull() {
		
		return (primitive == null);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int read(byte[] bytes, int offset, int codePointLength) throws SWPException {

		int nullIndicator = bytes[offset];
		int i = 0;
		if (nullIndicator == SWPConstants.NULL_INDICATOR) {
			
			primitive = null;
		}
		else {
		
			SWPReadable readable = SWPData.getInstance(codePoint);
			i = readable.read(bytes, offset + 1, codePointLength - 1);
			primitive = (T)readable;
		}
		return i + 1;
	}

	@Override
	public int write(byte[] bytes, int offset, int length) throws SWPException {

		// TODO: Handle 4-byte lengths
		int codePointLength = getLength();
		SWPException.requireTrue(length >= codePointLength, "Buffer overflow.");
		bytes[offset] = (byte)((codePointLength << 8) & 0xff);
		bytes[offset + 1] = (byte)(codePointLength & 0xff);
		bytes[offset + 2] = (byte)(codePoint >>> 8);
		bytes[offset + 3] = (byte)(codePoint & 0xff);
		if (isNull()) {
			
			bytes[offset + 4] = SWPConstants.NULL_INDICATOR;
		}
		else {
			
			bytes[offset + 4] = SWPConstants.NOT_NULL_INDICATOR;
			primitive.write(bytes, offset + 5, length - 5);
		}
		return codePointLength;
	}

	@Override
	public int getCodePoint() {

		return codePoint;
	}

	@Override
	public int getLength() {

		if (isNull()) {
			
			return 5;
		}
		
		return 5 + primitive.getWrittenLength();
	}
	
	@Override
	public String toString() {
		
		if (isNull()) {
			
			return "<null>";
		}
		
		return primitive.toString();
	}
}
