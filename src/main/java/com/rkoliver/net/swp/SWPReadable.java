package com.rkoliver.net.swp;

public interface SWPReadable {

	public int read(byte[] bytes, int offset, int length) throws SWPException;
}
