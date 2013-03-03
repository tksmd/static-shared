package com.isenshi.staticshared;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.isenshi.staticshared.Utils;

public class UtilsTest {

	@Test
	public void getExtention1() {
		assertThat(Utils.getExtention(".share.js"), is("js"));
		assertThat(Utils.getExtention(".share.css"), is("css"));
		assertThat(Utils.getExtention("share"), is(""));
		assertThat(Utils.getExtention(""), is(""));
	}

	@Test(expected = NullPointerException.class)
	public void getExtention2() {
		assertThat(Utils.getExtention(null), is("js"));
	}

}
