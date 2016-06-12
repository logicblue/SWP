package com.rkoliver.net.swp.data;

import com.rkoliver.net.swp.SWPConstants;
import com.rkoliver.net.swp.SWPException;
import com.rkoliver.net.swp.SWPReadable;
import com.rkoliver.net.swp.SWPWritable;

public abstract class SWPData implements SWPReadable, SWPWritable {
	
	public abstract int getWrittenLength();

	public static SWPReadable getInstance(int codePoint) throws SWPException {

		SWPException.requireTrue((codePoint & 0xf0000000) == 0x00000000, "Non-primitive codepoint.");
		
		switch (codePoint) {
		
			case SWPConstants.SWP_CP_VARINT: return new SWPVarInt();
		}
		
		throw SWPException.invalidCodePoint(codePoint);
	}
}
