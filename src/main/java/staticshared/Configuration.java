package staticshared;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class Configuration {

	private File baseDir;

	private Map<String, String> contentTypes;

	private String nocacheVersion = "SNAPSHOT";

	Configuration() {
		this.contentTypes = new HashMap<String, String>();
		this.contentTypes.put("js", "text/javascript");
		this.contentTypes.put("css", "text/css");
	}

	void setBaseDir(File baseDir) {
		this.baseDir = baseDir;
	}

	File getBaseDir() {
		return baseDir;
	}

	void addContentType(String ext, String contentType) {
		this.contentTypes.put(ext, contentType);
	}

	String getContentType(String ext) {
		return this.contentTypes.get(ext);
	}

	void setNocacheVersion(String nocacheVersion) {
		this.nocacheVersion = nocacheVersion;
	}

	String getNocacheVersion() {
		return nocacheVersion;
	}

	boolean isNocache(String version) {
		return nocacheVersion.equalsIgnoreCase(version);
	}

	String validate() {
		if (baseDir == null) {
			return "basedir must not be null.";
		}
		if (!baseDir.exists()) {
			return baseDir + " must exists.";
		}
		if (!baseDir.isDirectory()) {
			return baseDir + " must be a directory.";
		}
		return null;
	}

	@Override
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("baseDir = ").append(baseDir);
		buf.append("contentTypes =").append(contentTypes);
		buf.append("nocacheVersion =").append(nocacheVersion);
		return buf.toString();
	}

}
