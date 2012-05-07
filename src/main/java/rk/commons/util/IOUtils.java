package rk.commons.util;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public abstract class IOUtils {

	public static void readFully(InputStream in, byte[] bbuf, int length) throws IOException {
		readFully(in, bbuf, 0, length);
	}

	public static void readFully(InputStream in, byte[] bbuf, int off, int length) throws IOException {
		int offset = off;
		int remaining = length;

		int nbread;

		while (remaining > 0) {
			nbread = in.read(bbuf, offset, remaining);
			if (nbread == -1) {
				throw new EOFException();
			}

			remaining -= nbread;
			offset += nbread;
		}
	}
	
	public static int readUntilEof(InputStream in, byte[] bbuf, int length) throws IOException {
		return readUntilEof(in, bbuf, 0, length);
	}

	public static int readUntilEof(InputStream in, byte[] bbuf, int off, int length) throws IOException {
		int offset = off;
		int remaining = length;

		int nbread;
		int totalread = 0;

		while (remaining > 0) {
			nbread = in.read(bbuf, offset, remaining);
			if (nbread == -1) {
				break;
			}

			remaining -= nbread;
			offset += nbread;
			
			totalread += nbread;
		}
		
		return totalread;
	}

	public static void readFully(Reader in, char[] bbuf, int length) throws IOException {
		readFully(in, bbuf, 0, length);
	}

	public static void readFully(Reader in, char[] bbuf, int off, int length) throws IOException {
		int offset = off;
		int remaining = length;

		int nbread;

		while (remaining > 0) {
			nbread = in.read(bbuf, offset, remaining);
			if (nbread == -1) {
				throw new EOFException();
			}

			remaining -= nbread;
			offset += nbread;
		}
	}
	
	public static int readUntilEof(Reader in, char[] bbuf, int length) throws IOException {
		return readUntilEof(in, bbuf, 0, length);
	}
	
	public static int readUntilEof(Reader in, char[] bbuf, int off, int length) throws IOException {
		int offset = off;
		int remaining = length;

		int nbread;
		int totalread = 0;

		while (remaining > 0) {
			nbread = in.read(bbuf, offset, remaining);
			if (nbread == -1) {
				break;
			}

			remaining -= nbread;
			offset += nbread;
			
			totalread += nbread;
		}
		
		return totalread;
	}
	
	public static byte[] readUntilEof(InputStream in) throws IOException {
		byte[] merged = null;
		int offset = 0;
		
		byte[] buf = new byte[1024];
		int nbread;
		
		while ((nbread = IOUtils.readUntilEof(in, buf, 1024)) > 0) {
			if (merged == null) {
				merged = new byte[nbread];
				System.arraycopy(buf, 0, merged, 0, nbread);
				
				offset = nbread;
			} else {
				byte[] tmp = new byte[offset + nbread];
				
				System.arraycopy(merged, 0, tmp, 0, offset);
				System.arraycopy(buf, 0, tmp, offset, nbread);
				
				merged = tmp;
			}
		}
		
		return merged;
	}
	
	public static char[] readUntilEof(Reader in) throws IOException {
		char[] merged = null;
		int offset = 0;
		
		char[] buf = new char[1024];
		int nbread;
		
		while ((nbread = IOUtils.readUntilEof(in, buf, 1024)) > 0) {
			if (merged == null) {
				merged = new char[nbread];
				System.arraycopy(buf, 0, merged, 0, nbread);
				
				offset = nbread;
			} else {
				char[] tmp = new char[offset + nbread];
				
				System.arraycopy(merged, 0, tmp, 0, offset);
				System.arraycopy(buf, 0, tmp, offset, nbread);
				
				merged = tmp;
			}
		}
		
		return merged;
	}
}
