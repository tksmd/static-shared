package staticshared;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import com.google.common.base.Charsets;
import com.google.common.io.Files;

public class ConcatTest {

	private final Concat concat = new Concat(baseDir());

	@Test(expected = IllegalArgumentException.class)
	public void instance1() throws Exception {
		new Concat(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void instance2() throws Exception {
		File file = new File(baseDir(), "notexists");
		new Concat(file);
	}

	@Test(expected = IllegalArgumentException.class)
	public void instance3() throws Exception {
		File file = new File(baseDir(), "scripts/jquery-1.9.1.min.js");
		new Concat(file);
	}

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
		URL url = ConcatTest.class.getResource("");
		try {
			return new File(url.toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
	}

	static final Matcher<File> isSameAs(String path) {
		return isSameAs(new File(baseDir(), path));
	}

	static final Matcher<File> isSameAs(final File expected) {

		return new TypeSafeMatcher<File>() {

			File tested;

			@Override
			protected boolean matchesSafely(File item) {
				this.tested = item;
				try {
					String actual = Files.toString(item, Charsets.UTF_8);
					String concat = Files.toString(expected, Charsets.UTF_8);
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
