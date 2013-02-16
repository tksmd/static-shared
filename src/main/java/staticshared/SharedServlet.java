package staticshared;

import static staticshared.Utils.ext;
import static staticshared.Utils.now;
import static staticshared.Utils.sha1Hex;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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

	private Concat concat;

	private Map<String, String> contentTypes;

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		initConcat(config);
		initContentTypes(config);
	}

	void initConcat(ServletConfig config) {
		String base = config.getInitParameter("base");
		File baseDir = new File(base);
		if (!baseDir.exists()) {
			String realPath = config.getServletContext().getRealPath(base);
			baseDir = new File(realPath);
		}
		concat = new Concat(baseDir);
	}

	void initContentTypes(ServletConfig config) {
		Map<String, String> m = new HashMap<String, String>();
		String js = config.getInitParameter("js");
		m.put("js", (js != null) ? js : "text/javascript");
		String css = config.getInitParameter("css");
		m.put("css", (css != null) ? css : "text/css");
		contentTypes = Collections.unmodifiableMap(m);
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

		String prefix = m.group(1); // .shared.js or .shared.css etc
		String version = m.group(2); // v1.1 or R20130216
		String files = m.group(3); // scripts/jquery-1.9.1.js,scripts/underscore.js

		String etag = sha1Hex(version + ":" + files);
		String current = req.getHeader("If-None-Match");

		if (current != null && current.equals(etag)) {
			resp.sendError(HttpServletResponse.SC_NOT_MODIFIED);
			return;
		}

		resp.setContentType(contentTypes.get(ext(prefix)));
		// cache is alive 10 years later.
		long ttl = 315360000L;
		resp.setHeader("Cache-Control",
				String.format("public; max-age=%d; s-maxage=%d", ttl, ttl));
		resp.setDateHeader("Expires", now() + ttl);
		resp.setDateHeader("Last-Modified", 0L);
		resp.setHeader("ETag", etag);

		ServletOutputStream os = resp.getOutputStream();
		String[] resources = files.split(",");
		concat.execute(os, resources);
		os.flush();
	}

}
