package utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadFileToList {
	public static void readFiles(String file, List<String> lines){
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while(line!=null){
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		}catch(Exception e ){
			System.out.println("when reading file " + file +  " come across exeception.");
		}
	}
	
	public static void readString(String str, List<String> lines){
		try{
			BufferedReader reader = new BufferedReader(new StringReader(str));
			String line = reader.readLine();
			while(line!=null){
				lines.add(line);
				line = reader.readLine();
			}
			reader.close();
		}catch(Exception e ){
			System.out.println("when reading String " + str +  " come across exeception.");
		}
	}
	
	public static String readFiles(String file){
		try{
			StringBuffer buffer = new StringBuffer();
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while(line!=null){
				buffer.append(line+ "\n");
				line = reader.readLine();
			}
			reader.close();
			return buffer.toString();
		}catch(Exception e ){
			e.printStackTrace();
			System.out.println("when reading file " + file +  " come across exeception.");
			return null;
		}
	}
	
	public static HashMap<String, List<String>>  readStringListsWithString(String file){
		HashMap<String, List<String>> map = new HashMap<String, List<String>>();
		try {
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line = reader.readLine();
			while(line!=null){
				String[] splits = line.split("\t");
				
				List<String> list = new ArrayList<String>();
				if (!splits[1].equals("[]")){
					String[] items = splits[1].substring(1, splits[1].length()-1).split(", ");
					for (String item:items) list.add(item.trim());
				}		
				
				map.put(splits[0], list);

				line = reader.readLine();
			}
			reader.close();
		} catch(Exception e) {
			System.err.println("error happens when writing lines to file "+ file);
			e.printStackTrace();
			return null;
		}
		return map;
	}
	
}
