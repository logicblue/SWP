package com.rkoliver.net.swp.util;

import java.util.ArrayList;
import java.util.HashMap;

public class New {

	public static byte[] bytes(byte... bytes) {
		
		return bytes;
	}
	
	public static byte[] bytes(int length) {
		
		return new byte[length];
	}

	public static int[] ints(int length) {

		return new int[length];
	}

	public static <T> ArrayList<T> arrayList() {
		
		return new ArrayList<T>();
	}

	public static <K, V> HashMap<K, V> hashMap() {

		return new HashMap<K, V>();
	}
}
