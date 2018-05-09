package org.amalgam.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Utils {
	public static int postIdN= 5;
	
	//get the last four identifier
	public static String getUniqueClassName(String path){
		
		String[] strs = path.split("(\\.)|(/)");
		ArrayList<String> ids = new ArrayList<String>();
		for(int i = strs.length-1 ; i >= strs.length-postIdN && i >=0; i--){
			ids.add(strs[i]);
		}
		
		String result = "";
		for(int i =ids.size()-1;i>=0; i--){
			result += ids.get(i)+".";
		}
		return result;
	}
	
	/**
	 * 입력된 명령어에 따라서 프로그램 실행
	 * @param _command
	 * @return
	 */
	public static boolean execute(String[] _command, String _workingDir){ 
		try {
			Process p = Runtime.getRuntime().exec(_command, null, new File(_workingDir));

			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));

			String line = "";
			while ((line = reader.readLine()) != null) {
				System.out.println(line);
			}
			int exitVal = p.waitFor();
			//System.out.println("exec result : "+exitVal);
			reader.close();			
			
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}


}
