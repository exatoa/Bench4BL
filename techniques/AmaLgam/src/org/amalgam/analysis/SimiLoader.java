package org.amalgam.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.regex.*;

import org.amalgam.common.Property;
import org.amalgam.common.Utils;
import org.amalgam.common.Weights;
import org.amalgam.models.Bug;


public class SimiLoader {
	static String simiFile =  Property.getInstance().WorkDir + Property.getInstance().Separator + Weights.BugSimilarityName;
	static String BLUiRPath = "../BLUiR/release/BLUiR.jar";

	
	public static boolean create()
	{
		File workingDir = new File(Property.getInstance().WorkDir);
		Path src = Paths.get(workingDir.getParent(), "BLUiR_" + Property.getInstance().ProjectName, "indriQueryResult");
	    Path nwdir = Paths.get(simiFile);
	    
//		File f = new File(nwdir.toString());
//		if(f.exists() && !f.isDirectory()) { 
//			return true;
//		}
	    try
	    { 
	       Files.copy(src, nwdir, StandardCopyOption.REPLACE_EXISTING);
	       //System.out.println("File Copied");
	    }
	    catch(IOException e)
	    {
	        e.printStackTrace();
	        return false;
	    }
	    return true;
	}
	
	public static boolean load(HashMap<String, Bug> bugObjs) {
		final String regex = "^[0-9]+ Q[0-9] [a-zA-Z]+.*indri$";
		Pattern pattern = Pattern.compile(regex);

		boolean flag = true;
		BufferedReader br = null;
		try {
			String line = null;
			
			br = new BufferedReader(new FileReader(simiFile));
			while((line = br.readLine())!=null){
				
				Matcher match = pattern.matcher(line);
				if (match.find()==false) continue;
				
				//라인 파싱
				String[] spart = line.split(" ");
				String bugID = spart[0];
				String fixedFile = spart[2];
				fixedFile = Utils.getUniqueClassName(fixedFile);
				double score = Double.parseDouble(spart[4]);
					
				//버그 정보 생성.
				if(bugObjs.containsKey(bugID))
					bugObjs.get(bugID).addSimilarityScore(fixedFile, score);
//				}else{
//					Bug b = new Bug(bugID);
//					b.addSimilarityScore(fixedFile, score);
//					bugObjs.put(bugID, b);
//				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			flag = false;
		}
		finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
				flag = false;
			}
		}
		return flag;
	}

}
