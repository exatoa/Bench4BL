package utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileListUnderDirectory {
	
	public static List<String> getFileListUnder(String dir, String suffix) {
		List<String> list = new ArrayList<String>();
		ArrayList<File> files = new ArrayList<File>();
		listf(dir, files);
		for (File file : files) {
			if (file.getName().endsWith(suffix))
				list.add(file.getPath());
		}
		return list;
	}
	
	public static void listf(String directoryName, ArrayList<File> files) {
	    File directory = new File(directoryName);

	    // get all the files from a directory
	    File[] fList = directory.listFiles();
	    for (File file : fList) {
	        if (file.isFile()) {
	            files.add(file);
	        } else if (file.isDirectory()) {
	            listf(file.getAbsolutePath(), files);
	        }
	    }
	}
}
