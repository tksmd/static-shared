package staticshared;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

public class UtilsTest {

	@Test
	public void ext1() {
		assertThat(Utils.ext(".share.js"), is("js"));
		assertThat(Utils.ext(".share.css"), is("css"));
		assertThat(Utils.ext("share"), is(""));
		assertThat(Utils.ext(""), is(""));
	}

	@Test(expected = NullPointerException.class)
	public void ext2() {
		assertThat(Utils.ext(null), is("js"));
	}

}
