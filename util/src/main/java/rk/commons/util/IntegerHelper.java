package rk.commons.util;

import java.io.IOException;

public abstract class IntegerHelper {
	
	private static final int[][] digitsToInt;

	private static final byte[] intToDigits;

	private static final int MAX_DIGIT = 10;

	static {
        digitsToInt = new int[10][MAX_DIGIT];

        for (int i = 0, len = digitsToInt.length; i < len; ++i) {
            int tens = 1;

            for (int j = 0; j < MAX_DIGIT; ++j) {
                digitsToInt[i][j] = i * tens;
                tens *= 10;
            }
        }

        intToDigits = new byte[] { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9' };
	}

	public static byte[] toAsciiBytes(int value, int length) throws IOException {
		int rem = value;

		byte[] buf = new byte[length];

		for (int i = length - 1; i >= 0; --i) {
			buf[i] = intToDigits[rem % 10];
			rem = rem / 10;
		}

		return buf;
	}

	public static int fromAsciiBytes(byte[] bbuf, int length) throws IOException {
		int value = 0;

		for (int i = length - 1, j = 0; i >= 0; --i, ++j) {
            int digitInt = bbuf[j];
            if ((digitInt < '0') || (digitInt > '9')) {
                throw new NumberFormatException((char) bbuf[i] + " is not a number.");
            }

            value += digitsToInt[digitInt - '0'][i];
		}

		return value;
	}

    public static byte[] toNboBytes(int value, int length) {
    	byte[] bytes = new byte[length];

        int i = bytes.length - 1;

        while ((value > 0) && (i >= 0)) {
            bytes[i] = (byte) (value & 0xFF);

            value = value >> 8;
            --i;
        }
        
        return bytes;
    }

    public static int fromNboBytes(byte[] bytes, int length) {
        int r = 0;

        for (int i = 0; i < length; ++i) {
            r = (r << 8) | (bytes[i] & 0xFF);
        }

        return r;
    }
}
