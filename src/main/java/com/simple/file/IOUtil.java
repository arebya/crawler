package com.simple.file;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class IOUtil {

	public static void writeTofile(String file, String content) {
		try {
			File f = new File(file);
			if (!f.exists()) {
				f.mkdirs();
			}
			FileUtils.writeStringToFile(f, content, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
