package com.isenshi.staticshared;

import static com.isenshi.staticshared.Tests.baseDir;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import com.isenshi.staticshared.CatFile;

public class CatFileTest {

	private final CatFile catfile = new CatFile(baseDir());

	/**
	 * 単一ファイルの連結
	 * 
	 * @throws Exception
	 */
	@Test
	public void execute1() throws Exception {
		File out = createTempfile();
		catfile.execute(out, "scripts/jquery-1.9.1.min.js");
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
		catfile.execute(out, "scripts/jquery-1.9.1.min.js",
				"scripts/underscore-1.4.4.min.js");
		assertThat(out, isSameAs("CatfileTest_execute2_expected.js"));
	}

	/**
	 * ベースディレクトリ以上のファイルの取得の禁止
	 * 
	 * @throws Exception
	 */
	@Test
	public void execute3() throws Exception {
		File out = createTempfile();
		catfile.execute(out, "scripts/jquery-1.9.1.min.js", "../../build.gradle");
		assertThat(out, isSameAs("scripts/jquery-1.9.1.min.js"));
	}

	static File createTempfile() throws IOException {
		File ret = File.createTempFile("ConcatTest-", ".js");
		ret.deleteOnExit();
		return ret;
	}

	static final Matcher<File> isSameAs(String path) {
		final File expected = new File(baseDir(), path);

		return new TypeSafeMatcher<File>() {

			File tested;

			@Override
			protected boolean matchesSafely(File item) {
				this.tested = item;
				try {
					String actual = Tests.toString(item);
					String concat = Tests.toString(expected);
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
