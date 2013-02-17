package staticshared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class CatFile {

	private final File baseDir;

	private final String baseDirPath;

	public CatFile(final File baseDir) {
		this.baseDir = baseDir;
		this.baseDirPath = baseDir.getAbsolutePath();
	}

	public void execute(File out, String... resources) throws IOException {
		FileOutputStream os = null;
		try {
			os = new FileOutputStream(out);
			execute(new FileOutputStream(out), resources);
		} finally {
			Utils.closeQuietly(os);
		}
	}

	public void execute(OutputStream os, String... resources)
			throws IOException {
		for (String relative : resources) {
			File from = new File(baseDir, relative).getCanonicalFile();

			if (!contains(from)) {
				// prevent directory traversal
				// TODO log
				continue;
			}
			Utils.copy(from, os);
		}
	}

	boolean contains(File target) {
		return target.getAbsolutePath().startsWith(baseDirPath);
	}

}
