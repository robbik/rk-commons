package rk.commons.util;

import java.nio.charset.Charset;
import java.util.UUID;

public abstract class StringHelper {
	
	public static final String[] EMPTY_STRING_ARRAY = new String[0];
	
	public static final Charset UTF8_CHARSET;
	
	static {
		UTF8_CHARSET = Charset.forName("UTF-8");
	}

	public static boolean hasText(String s) {
		if (s == null) {
			return false;
		}

		s = s.trim();
		return !s.isEmpty();
	}

	public static boolean hasText(String s, boolean trim) {
		if (s == null) {
			return false;
		}

		if (trim) {
			s = s.trim();
		}

		return !s.isEmpty();
	}

	public static String valueOf(UUID uuid) {
		return uuid.toString().replace("-", "");
	}
	
	public static String newUTF8(byte[] bytes) {
		return new String(bytes, UTF8_CHARSET);
	}
	
	public static String newUTF8(byte[] bytes, int offset, int length) {
		return new String(bytes, offset, length, UTF8_CHARSET);
	}
	
	public static String escapeJava(String str) {
		return StringEscapeUtils.escapeJava(str);
	}
	
	public static String unescapeJava(String str) {
		return StringEscapeUtils.unescapeJava(str);
	}
}
