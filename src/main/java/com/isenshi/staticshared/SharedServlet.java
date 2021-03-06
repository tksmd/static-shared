package com.isenshi.staticshared;

import static com.isenshi.staticshared.Utils.copy;
import static com.isenshi.staticshared.Utils.createTempDir;
import static com.isenshi.staticshared.Utils.getExtention;
import static com.isenshi.staticshared.Utils.now;
import static com.isenshi.staticshared.Utils.sha1Hex;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author someda
 * @see <a
 *      href="https://github.com/cho45/Plack-Middleware-StaticShared/blob/master/lib/Plack/Middleware/StaticShared.pm">StaticShared.pm</a>
 */
public class SharedServlet extends HttpServlet {

	private static final long serialVersionUID = -312127685117751877L;

	Configuration config;

	static final File CACHE_DIR;

	static {
		CACHE_DIR = createTempDir();
		CACHE_DIR.deleteOnExit();
	}

	@Override
	public void init(ServletConfig sc) throws ServletException {
		super.init(sc);
		this.config = new Configuration();

		this.config.setBaseDir(getBaseDir(sc));
		String js = sc.getInitParameter("js");
		if (js != null) {
			this.config.addContentType("js", js);
		}
		String css = sc.getInitParameter("css");
		if (css != null) {
			this.config.addContentType("css", css);
		}

		String nocache = sc.getInitParameter("nocache");
		if (nocache != null) {
			this.config.setNocache(Boolean.valueOf(nocache));
		}

		String error = this.config.validate();
		if (error != null) {
			throw new ServletException(error);
		}
	}

	File getBaseDir(ServletConfig sc) {
		String base = sc.getInitParameter("base");
		File baseDir = new File(base);
		if (!baseDir.exists()) {
			String realPath = sc.getServletContext().getRealPath(base);
			baseDir = new File(realPath);
		}
		return baseDir;
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		String context = req.getContextPath();
		Pattern p = Pattern.compile(context + "/(.*?)/([^:\\s]+):(.+)");
		Matcher m = p.matcher(req.getRequestURI());

		if (!m.matches()) {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return;
		}

		String prefix = m.group(1); // .shared.js or .shared.css and so on
		String version = m.group(2); // v1.1 or R20130216 and so on
		String files = m.group(3); // scripts/jquery-1.9.1.js,scripts/underscore.js

		if (config.isNocache()) {
			// for development mode

			resp.setContentType(config.getContentType(getExtention(prefix)));
			ServletOutputStream os = resp.getOutputStream();
			CatFile catfile = new CatFile(config.getBaseDir());
			catfile.execute(os, files.split(","));
			os.flush();

		} else {

			String etag = sha1Hex(version + ":" + files);
			String current = req.getHeader("If-None-Match");

			if (current != null && current.equals(etag)) {
				resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}

			File content = getContent(etag, files.split(","));
			long ttl = 315360000L;

			resp.setContentType(config.getContentType(getExtention(prefix)));
			resp.setContentLength((int) content.length());
			// cache will be alive until 10 years later.
			resp.setHeader("Cache-Control",
					String.format("public; max-age=%d; s-maxage=%d", ttl, ttl));
			resp.setDateHeader("Expires", now() + ttl);
			resp.setDateHeader("Last-Modified", 0L);
			resp.setHeader("ETag", etag);

			ServletOutputStream os = resp.getOutputStream();
			copy(content, os);
			os.flush();
		}

	}

	protected File getContent(String etag, String[] resources)
			throws IOException {
		File ret = new File(CACHE_DIR, etag);
		if (!ret.exists()) {
			CatFile catfile = new CatFile(config.getBaseDir());
			catfile.execute(ret, resources);
			ret.deleteOnExit();
		}
		return ret;
	}

}
