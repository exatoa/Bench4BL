package utils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class ChangeLocator {
	public static HashSet<String> shortChangeMap = null;
	
	public static HashSet<String> getShortChangeMap() {
		if (shortChangeMap == null) {
			shortChangeMap = readShortChangeMap();
		} 
		return shortChangeMap;
	}
	
	public static HashMap<String,Long> getChangeTime() throws ParseException {
		HashMap<String,Long> changeTime = new HashMap<String,Long>();
		List<String> lines = FileToLines.fileToLines(main.Main.settings.get("workingLoc") + File.separator + "logOneline.txt");
		for (String line : lines) {
			String[] split = line.split("\t");
			
			Date date = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy Z", Locale.ENGLISH).parse(split[2]);
			changeTime.put(split[0], date.getTime());
		}
		return changeTime;
	}
	
	public static HashSet<String> readShortChangeMap() {
		HashSet<String> changeMap = new HashSet<String>();
		List<String> lines = FileToLines.fileToLines(main.Main.settings.get("workingLoc") + File.separator + "logOneline.txt");
		for (String line : lines) {
			String[] split = line.split("\t");
			changeMap.add(split[0]);
		}
		return changeMap;
	}
}
