package com.rkoliver.net.swp.codepoint;

import com.rkoliver.net.swp.SWPReadable;
import com.rkoliver.net.swp.SWPWritable;

public interface SWPCodePoint extends SWPReadable, SWPWritable {
	int getCodePoint();
	int getLength();
	boolean isNull();
}
