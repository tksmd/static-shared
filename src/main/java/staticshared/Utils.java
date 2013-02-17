package staticshared;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.sun.istack.internal.Nullable;

class Utils {

	private static final int BUF_SIZE = 0x1000; // 4K

	private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

	static TimeProvider PROVIDER = new TimeProvider();

	static final String sha1Hex(String str) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-1");
			return new String(encodeHex(digest.digest(str.getBytes())));
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e); // あり得ない例外
		}
	}

	static final String ext(String name) {
		int idx = name.lastIndexOf('.');
		return (idx == -1) ? "" : name.substring(idx + 1);
	}

	static final long now() {
		return PROVIDER.now();
	}

	/**
	 * commons-codec の Hex クラスより拝借
	 */
	private static char[] encodeHex(byte[] data) {

		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
		return out;
	}

	static final long copy(File from, OutputStream os) throws IOException {

		InputStream is = null;
		try {
			is = new FileInputStream(from);
			byte[] buf = new byte[BUF_SIZE];
			long total = 0;
			while (true) {
				int r = is.read(buf);
				if (r == -1) {
					break;
				}
				os.write(buf, 0, r);
				total += r;
			}
			return total;
		} finally {
			closeQuietly(is);
		}
	}

	static void closeQuietly(@Nullable Closeable closeable) {
		try {
			if (closeable == null) {
				return;
			}
			closeable.close();
		} catch (IOException e) {
		}
	}

	protected static class TimeProvider {
		long now() {
			return System.currentTimeMillis();
		}
	}

}
