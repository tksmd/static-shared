package staticshared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.Files;

public class Concat {

	private final File baseDir;

	private final String baseDirPath;

	public Concat(final File baseDir) {
		if (baseDir == null || !baseDir.exists() || !baseDir.isDirectory()) {
			throw new IllegalArgumentException(baseDir
					+ " must exists or be a directory ( not file ).");
		}
		this.baseDir = baseDir;
		this.baseDirPath = baseDir.getAbsolutePath();
	}

	public void execute(File out, String... resources) throws IOException {
		execute(new FileOutputStream(out), resources);
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
			Files.copy(from, os);
		}
	}

	boolean contains(File target) {
		return target.getAbsolutePath().startsWith(baseDirPath);
	}

}
