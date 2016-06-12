package com.rkoliver.net.swp;

import java.util.ArrayList;
import java.util.List;

import com.rkoliver.net.swp.codepoint.SWPCodePoint;

public class SWPPacket {

	private final List<SWPCodePoint> codePoints = new ArrayList<>();

	public void addCodePoint(SWPCodePoint codePoint) {
		
		codePoints.add(codePoint);
	}

	public int getLength() {
		// TODO Auto-generated method stub
		return 0;
	}	
}
