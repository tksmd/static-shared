package staticshared;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.Ignore;

@Ignore
public class Tests {

	static File baseDir() {
		URL url = Tests.class.getResource("");
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	static String toString(File file) throws IOException {

		StringBuilder buf = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buf.append(line + "\n");
			}
			
			String ret = buf.toString();
			if(ret.endsWith("\n")){
				ret = ret.substring(0, ret.length() - 1);
			}			
			return ret;
		} finally {
			Utils.closeQuietly(reader);
		}
	}
}
