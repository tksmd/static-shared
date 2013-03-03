package com.isenshi.staticshared;

import static com.isenshi.staticshared.Tests.baseDir;
import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.isenshi.staticshared.Configuration;
import com.isenshi.staticshared.SharedServlet;
import com.isenshi.staticshared.Utils;

public class SharedServletTest {

	private SharedServlet servlet = new SharedServlet();

	private Configuration config = new Configuration();

	private HttpServletRequest request;

	private HttpServletResponse response;

	private String contextPath = "/static-shared";

	private static final long FIXED_NOW = 20 * 365 * 24 * 60 * 60 * 1000L;

	@Before
	public void before() throws Exception {

		request = createMock(HttpServletRequest.class);
		response = createMock(HttpServletResponse.class);

		config.setBaseDir(baseDir());
		servlet.config = config;

		Utils.CLOCK = new Utils.ClockWrapper() {
			@Override
			long now() {
				return FIXED_NOW;
			}
		};
	}

	@After
	public void after() throws Exception {
		verify(request, response);
	}

	@Test
	public void badRequest1() throws Exception {
		expectRequest(contextPath
				+ "/.shared.js?v1=scripts/jquery.js,scripts/underscore.js");
		response.sendError(HttpServletResponse.SC_BAD_REQUEST);
		expectLastCall();
		replay(request, response);

		servlet.doGet(request, response);
	}

	@Test
	public void notModified1() throws Exception {
		expectRequest(contextPath
				+ "/.shared.js/v1:scripts/jquery-1.9.1.min.js,scripts/underscore-1.4.4.min.js");
		expect(request.getHeader("If-None-Match")).andReturn(
				"a9fc916e4de4a9a84a17a3130dd4d38e90e9f1cd");
		response.sendError(HttpServletResponse.SC_NOT_MODIFIED);
		expectLastCall();
		replay(request, response);

		servlet.doGet(request, response);
	}

	@Test
	public void cacheNotFound1() throws Exception {

		String etag = "a9fc916e4de4a9a84a17a3130dd4d38e90e9f1cd";

		expectRequest(contextPath
				+ "/.shared.js/v1:scripts/jquery-1.9.1.min.js,scripts/underscore-1.4.4.min.js");

		expect(request.getHeader("If-None-Match")).andReturn(null);
		expectSuccessResponse("text/javascript", 106079, etag);

		MockServletOutputStream os = new MockServletOutputStream();
		expect(response.getOutputStream()).andReturn(os);

		replay(request, response);

		servlet.doGet(request, response);

		File content = new File(SharedServlet.CACHE_DIR, etag);
		assertThat(os.getString(), is(Tests.toString(content)));
	}

	@Test
	public void cacheFound1() throws Exception {

		String etag = "a9fc916e4de4a9a84a17a3130dd4d38e90e9f1cd";

		expectRequest(contextPath
				+ "/.shared.js/v1:scripts/jquery-1.9.1.min.js,scripts/underscore-1.4.4.min.js");

		expect(request.getHeader("If-None-Match")).andReturn(null);
		expectSuccessResponse("text/javascript", 106079, etag);

		MockServletOutputStream os = new MockServletOutputStream();
		expect(response.getOutputStream()).andReturn(os);

		replay(request, response);

		File content = new File(SharedServlet.CACHE_DIR, etag);
		content.deleteOnExit();
		assertFalse(content.exists());
		FileOutputStream cache = new FileOutputStream(content);
		Utils.copy(new File(baseDir(), "SharedServletTest_cachefound.js"),
				cache);
		Utils.closeQuietly(cache);

		servlet.doGet(request, response);

		assertThat(os.getString(), is(Tests.toString(content)));
	}

	@Test
	public void nocacheVersion1() throws Exception {
		
		config.setNocache(true);
		
		expectRequest(contextPath
				+ "/.shared.js/v1:scripts/jquery-1.9.1.min.js,scripts/underscore-1.4.4.min.js");

		response.setContentType("text/javascript");
		expectLastCall();

		MockServletOutputStream os = new MockServletOutputStream();
		expect(response.getOutputStream()).andReturn(os);

		replay(request, response);

		File expected = new File(baseDir(), "SharedServletTest_cachefound.js");
		servlet.doGet(request, response);

		assertThat(os.getString(), is(Tests.toString(expected)));
	}

	void expectRequest(String requestURI) {
		expect(request.getContextPath()).andReturn(contextPath);
		expect(request.getRequestURI()).andReturn(requestURI);
	}

	void expectSuccessResponse(String contentType, int contentLength,
			String etag) {
		response.setContentType(contentType);
		expectLastCall();
		response.setContentLength(contentLength);
		expectLastCall();
		response.setHeader("Cache-Control",
				"public; max-age=315360000; s-maxage=315360000");
		expectLastCall();
		response.setDateHeader("Expires", FIXED_NOW + 315360000L);
		expectLastCall();
		response.setDateHeader("Last-Modified", 0L);
		expectLastCall();
		response.setHeader("ETag", etag);
		expectLastCall();
	}

	static class MockServletOutputStream extends ServletOutputStream {

		StringWriter writer = new StringWriter();

		@Override
		public void write(int arg0) throws IOException {
			writer.write(arg0);
		}

		public String getString() {
			writer.flush();
			return writer.toString();
		}
	}

}
