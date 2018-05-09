package org.brtracer;

import java.io.File;

import org.brtracer.property.Property;

public class BRTracer {
	public static void main(String[] args) {
        /* You need to manually specify three program arguments in Eclipse or Intellij before running */
		try {
			if (args.length == 0)
				throw null;

			boolean isLegal = parseArgs(args);
			if (!isLegal)
				throw null;

			Core core = new Core();
			core.process();

		} catch (Exception ex) {
			showHelp();
		}		
	}
	
	/**
	 * 
	 */
	private static void showHelp() {
		String usage = "Usage:java -jar BRTracer [-options] \r\n" + "where options must include:\r\n"
				+ "-b\tindicates the bug information file\r\n" + "-s\tindicates the source code directory\r\n"
				+ "-a\tindicates the alpha value for combining vsmScore and simiScore\r\n"
				+ "-w\tindicates the working directory\r\n"
				+ "-n\tindicates the working name (this uses for result file name.)\r\n"
				+ "  \tOn the below of the {working directory}\r\n"
				+ "  \tThis program will make temp directory : BRTracer_{working name}\\\r\n"
				+ "  \t                and final result file : BRTracer_{working name}_output.txt";


		System.out.println(usage);
	}

	/**
	 * 입력 파라메터를 파싱하여 Property 객체 생성
	 * @param args
	 * @return
	 */
	private static boolean parseArgs(String[] args) {
		int i = 0;
		String bugFilePath = "";
		String sourceCodeDir = "";
		String alphaStr = "";
		float alpha = 0.3f;
		String outputFile = "";
		String workingPath = "";
		String projectStr = "";
		
		while (i < args.length - 1) {
			if (args[i].equals("-b")) {
				i++;
				bugFilePath = args[i];
				bugFilePath = bugFilePath.replace("\\", "/");
				bugFilePath = bugFilePath.replace("//", "/");				
			} else if (args[i].equals("-s")) {
				i++;
				sourceCodeDir = args[i];
				sourceCodeDir = sourceCodeDir.replace("\\", "/");
				sourceCodeDir = sourceCodeDir.replace("//", "/");
			} else if (args[i].equals("-a")) {
				i++;
				alphaStr = args[i];
			} else if (args[i].equals("-w")) {
				i++;
				workingPath = args[i];
				workingPath = workingPath.replace("\\", "/");
				workingPath = workingPath.replace("//", "/");
			} else if (args[i].equals("-n")) {
				i++;
				projectStr = args[i];
			}
			i++;
		}
		
		boolean isLegal = true;
		if ((bugFilePath.equals("")) || (bugFilePath == null)) {
			isLegal = false;
			System.out.println("you must indicate the bug information file");
		}
		if ((sourceCodeDir.equals("")) || (sourceCodeDir == null)) {
			isLegal = false;
			System.out.println("you must indicate the source code directory");
		}
		if ((!alphaStr.equals("")) && (alphaStr != null)) {
			try {
				alpha = Float.parseFloat(alphaStr);
			} catch (Exception ex) {
				isLegal = false;
				System.out.println("-a argument is ilegal,it must be a float value");
			}
		}
		if (workingPath.equals("") || workingPath == null) {
			isLegal = false;
			System.out.println("you must indicate the working directory (temp directory)");
		}
		if (projectStr.equals("") || projectStr == null) {
			isLegal = false;
			System.out.println("you must indicate the working name (for result file or directory)");
		}
		
		//File System check (minimum 2GB)
		File file = new File(System.getProperty("user.dir"));
		if (file.getFreeSpace() / 1024 / 1024 / 1024 < 2) {
			System.out.println("Not enough free disk space, please ensure your current disk space are bigger than 2G.");
			isLegal = false;
		}	
		
		//Check this state.
		if (!isLegal) {
			return isLegal;
		}
		
		// prepare working directory and create properties. 
		// make workingPath
		if (workingPath.endsWith("/") == false) workingPath += "/";
		workingPath += "BRTracer_" + projectStr + "/";
		
		//make outputFile path.
		File dir = new File(workingPath);
		if (!dir.exists())
			dir.mkdirs();
		outputFile = workingPath.substring(0, workingPath.length() - 1) + "_output.txt";
	
		Property.createInstance(projectStr.toUpperCase(), bugFilePath, sourceCodeDir, workingPath, alpha, outputFile);

		return isLegal;
	}
}