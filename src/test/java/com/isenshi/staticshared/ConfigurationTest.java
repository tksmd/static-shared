package com.isenshi.staticshared;

import static com.isenshi.staticshared.Tests.baseDir;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.junit.Test;

import com.isenshi.staticshared.Configuration;

public class ConfigurationTest {

	private Configuration config = new Configuration();

	@Test
	public void validate1() throws Exception {
		String actual = config.validate();
		assertThat(actual, is("basedir must not be null."));
	}

	@Test
	public void validate2() throws Exception {
		File file = new File(baseDir(), "notexists");
		config.setBaseDir(file);
		String actual = config.validate();
		assertThat(actual, is(file + " must exists."));
	}

	@Test
	public void validate3() throws Exception {
		File file = new File(baseDir(), "scripts/jquery-1.9.1.min.js");
		config.setBaseDir(file);
		String actual = config.validate();
		assertThat(actual, is(file + " must be a directory."));
	}

	@Test
	public void validate4() throws Exception {
		File file = new File(baseDir(), "scripts");
		config.setBaseDir(file);
		String actual = config.validate();
		assertThat(actual, nullValue());
	}

	@Test
	public void nocache1() throws Exception {
		assertThat(config.isNocache(), is(false));
		
		config.setNocache(true);
		assertThat(config.isNocache(), is(true));				

		config.setNocache(false);
		assertThat(config.isNocache(), is(false));		
	}

}
