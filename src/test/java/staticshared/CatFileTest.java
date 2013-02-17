package staticshared;

import static org.junit.Assert.assertThat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class CatFileTest {

	private final CatFile concat = new CatFile(baseDir());

	/**
	 * 単一ファイルの連結
	 * 
	 * @throws Exception
	 */
	@Test
	public void execute1() throws Exception {
		File out = createTempfile();
		concat.execute(out, "scripts/jquery-1.9.1.min.js");
		assertThat(out, isSameAs("scripts/jquery-1.9.1.min.js"));
	}

	/**
	 * 複数ファイルの連結
	 * 
	 * @throws Exception
	 */
	@Test
	public void execute2() throws Exception {
		File out = createTempfile();
		concat.execute(out, "scripts/jquery-1.9.1.min.js",
				"scripts/underscore-1.4.4.min.js");
		assertThat(out, isSameAs("ConcatTest_execute2_expected.js"));
	}

	/**
	 * ベースディレクトリ以上のファイルの取得の禁止
	 * 
	 * @throws Exception
	 */
	@Test
	public void execute3() throws Exception {
		File out = createTempfile();
		concat.execute(out, "scripts/jquery-1.9.1.min.js", "../../build.gradle");
		assertThat(out, isSameAs("scripts/jquery-1.9.1.min.js"));
	}

	static File createTempfile() throws IOException {
		File ret = File.createTempFile("ConcatTest-", ".js");
		ret.deleteOnExit();
		return ret;
	}

	static File baseDir() {
		URL url = CatFileTest.class.getResource("");
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	static String fileToString(File file) throws IOException {

		StringBuilder buf = new StringBuilder();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(file));
			buf.append(reader.readLine());
			return buf.toString();
		} finally {
			Utils.closeQuietly(reader);
		}
	}

	static final Matcher<File> isSameAs(String path) {
		final File expected = new File(baseDir(), path);

		return new TypeSafeMatcher<File>() {

			File tested;

			@Override
			protected boolean matchesSafely(File item) {
				this.tested = item;
				try {
					String actual = fileToString(item);
					String concat = fileToString(expected);
					return actual.equals(concat);
				} catch (IOException e) {
					return false;
				}
			}

			@Override
			public void describeTo(Description description) {
				description.appendText(" that ");
				description.appendValue(tested);
				description.appendText("is same as ");
				description.appendValue(expected);
			}
		};
	}

}
