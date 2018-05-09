package org.brtracer.property;

public class Property {

	private static Property p = null;

	public static void createInstance(String projectName, String bugFilePath, String sourceCodeDir,String workDir,float alpha,String outputFile) {
		if (p == null)
			p = new Property(projectName, bugFilePath, sourceCodeDir,workDir,alpha,outputFile);
	}

	public static Property getInstance() {
		return p;
	}

	/**
	 * »ý¼ºÀÚ.
	 * @param bugFilePath
	 * @param sourceCodeDir
	 * @param workDir
	 * @param alpha
	 * @param outputFile
	 * @param offset
	 */
	private Property(String projectName, String bugFilePath, String sourceCodeDir,String workDir,float alpha,String outputFile) {
		this.BugFilePath = bugFilePath;
		this.SourceCodeDir = sourceCodeDir;
		this.WorkDir=workDir;
		this.Alpha=alpha;
		this.OutputFile=outputFile;
        this.Project = projectName;
        this.Offset = sourceCodeDir.length();
        
        this.Separator = System.getProperty("file.separator");
        this.LineSeparator = System.getProperty("line.separator");
	}
	
	public final String Separator;
	public final String LineSeparator;
	
	public final String BugFilePath;
	public final String SourceCodeDir;
	public final String WorkDir;
	public int FileCount;
	public int WordCount;
	public int BugReportCount;
	public int BugTermCount;
	public final float Alpha;
	public final String OutputFile;
	
	//newly added
	public int OriginFileCount;
	public int Offset; // for AspectJ    //old  Aspectj_filename_offset;
	public String Project;
}
