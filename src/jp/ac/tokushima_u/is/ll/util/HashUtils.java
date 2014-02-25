package jp.ac.tokushima_u.is.ll.util;

import java.io.IOException;

import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;


public class HashUtils {

	public static String md5Hex(byte[] b) {
		try {
			return ByteStreams.hash(ByteStreams.newInputStreamSupplier(b), Hashing.md5()).toString();
		} catch (IOException e) {
			return "";
		}
	}
}
