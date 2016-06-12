package com.rkoliver.net.swp;


public class SWPException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private SWPException(Exception e) {

		super(e);
	}

	private SWPException(String message) {

		super(message);
	}

	public static SWPException convert(Exception e) {
		
		return new SWPException(e);
	}

	public static SWPException sessionAlreadyOpen() {

		return new SWPException("Session already open.");
	}

	public static SWPException numericOverflow() {
		
		return new SWPException("Numeric overflow.");
	}	

	private static SWPException assertionError(String message) {

		return new SWPException(message);
	}
	
	public static <T> void requireNonNull(T obj, String message) throws SWPException {
		
		requireTrue(obj != null, message);
	}
	
	public static void requireTrue(boolean b, String message) throws SWPException {
		
		if (!b) {
			
			throw SWPException.assertionError(message);
		}
	}

	public static SWPException dataError(String string) {

		return new SWPException(string);
	}	

	public static void dataErrorIfFalse(boolean b, String string) throws SWPException {

		if (!b) {
			
			throw dataError(string);
		}
	}

	public static SWPException partialCodePoint() {

		return new SWPException("Partial code point encountered.");
	}

	public static SWPException invalidCodePoint(int codePoint) {

		return new SWPException("Invalid code point: " + codePoint);
	}

	public static SWPException codePointFactoryNotFound(int codePoint) {

		return new SWPException("Codepoint factory not found for codepoint: " + codePoint);
	}
}
