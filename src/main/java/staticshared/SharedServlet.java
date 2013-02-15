package staticshared;

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

		Pattern p = Pattern.compile(".?*/(.*)/([^:\\s]+):(.+)");
		Matcher m = p.matcher(req.getRequestURI());
		if (m.matches()) {
			String prefix = m.group(1);
			// String version = m.group(2);
			String files = m.group(3);

			resp.setContentType(contentTypes.get(ext(prefix)));

			ServletOutputStream os = resp.getOutputStream();
			String[] resources = files.split(",");
			concat.execute(os, resources);
			os.flush();

		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}

	}

	static String ext(String name) {
		int idx = name.lastIndexOf('.');
		return (idx == -1) ? "" : name.substring(idx + 1);
	}

}
