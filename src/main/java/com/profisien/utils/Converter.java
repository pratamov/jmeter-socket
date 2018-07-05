package com.profisien.utils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class Converter {
	
	public static void main(String[] args) {
		String guid = "AAGRtg==";
		byte[] decoded = Base64.decodeBase64(guid);
		String hexString = Hex.encodeHexString(decoded);
		System.out.println(hexString);
		// print: 000191b6
	}
	
}
