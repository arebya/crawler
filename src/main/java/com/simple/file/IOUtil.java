package com.simple.file;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class IOUtil {

	public static void writeTofile(String file, String content) {
		try {
			File f = new File(file);
			if (!f.exists()) {
				f.createNewFile();
			}
			String oldContent = FileUtils.readFileToString(f, "UTF-8");
			StringBuffer buffer = new StringBuffer(oldContent);
			if (content != null ) {
				buffer.append(content);
			}
			FileUtils.writeStringToFile(f, buffer.toString(), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
