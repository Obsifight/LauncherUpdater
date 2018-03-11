package fr.thisismac.downloader;

import java.io.File;

public class Util {
	public static enum OS {
		WINDOWS, MACOS, SOLARIS, LINUX, UNKNOWN;
	}

	public static OS getPlatform() {
		final String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win"))
			return OS.WINDOWS;
		if (osName.contains("mac"))
			return OS.MACOS;
		if (osName.contains("linux"))
			return OS.LINUX;
		if (osName.contains("unix"))
			return OS.LINUX;
		return OS.UNKNOWN;
	}

	public static File getDesktop() {
		final String userHome = System.getProperty("user.home", ".");
		File workingDirectory;
		switch (getPlatform()) {
		case SOLARIS:
		case LINUX:
			workingDirectory = new File(userHome, "Desktop/");
			break;
		case WINDOWS:
			workingDirectory = new File(userHome, "/Desktop/");
			break;
		case MACOS:
			workingDirectory = new File(userHome, "Desktop/");
			break;
		default:
			workingDirectory = new File(userHome, "Desktop/");
		}

		return workingDirectory;
	}
}