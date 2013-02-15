package staticshared;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import com.google.common.io.Files;

public class Concat {

	private final File baseDir;

	public Concat(final File baseDir) {
		this.baseDir = baseDir;
	}

	public void execute(File out, String... resources) throws IOException {
		execute(new FileOutputStream(out), resources);
	}

	public void execute(OutputStream os, String... resources)
			throws IOException {
		for (String relative : resources) {
			File from = new File(baseDir, relative).getCanonicalFile();
			// TODO ここで、baseDir 以下にあるかをチェック
			Files.copy(from, os);
		}
	}
}
