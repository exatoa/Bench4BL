package org.amalgam.models;

import java.util.HashMap;
import java.util.Set;

public class FileObjs {
	private static int globalID = 0;
	private static HashMap<String, Integer> objs = new HashMap<String, Integer>();
	private static HashMap<Integer, String> revs = new HashMap<Integer, String>();
	
	
	public static int put(String _filename)
	{
		Integer id =  objs.get(_filename);
		if (id == null){
			id = globalID++;
			objs.put(_filename, id);
			revs.put(id, _filename);
		}
		return id;
	}
	
	public static Integer get(String _filename)
	{
		return objs.get(_filename);
	}
	

	public static boolean contains(String _filename){
		return objs.containsKey(_filename);
	}

	
	public static Set<String> getFiles(){
		return objs.keySet();
	}
	
	
	public static String get(Integer _fid)
	{
		return revs.get(_fid);
	}
	
	public static boolean contains(Integer _fid){
		return revs.containsKey(_fid);
	}
	public static Set<Integer> getFileIDs(){
		return revs.keySet();
	}
}
