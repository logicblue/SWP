package com.rkoliver.net.swp;

public interface SWPWritable {

	public int write(byte[] bytes, int offset, int length) throws SWPException;
}
