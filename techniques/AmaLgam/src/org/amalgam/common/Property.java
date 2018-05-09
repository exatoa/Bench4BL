package org.amalgam.common;

public class Property {
	

	private static Property p = null;
	
	public static void createInstance(String projectStr, 
										String bugFilePath, 
										String sourceCodeDir, 
										String gitRepo, 
										String workDir, 
										float alpha, 
										String outputFile) 
	{
		if (p == null)
			p = new Property(projectStr, bugFilePath, sourceCodeDir, gitRepo, workDir, alpha, outputFile);
	}
	
	public static Property getInstance() {
		return p;
	}
	
	private Property(String ProjectStr, String bugFilePath, String sourceCodeDir, String gitRepo, String workDir, float alpha, String outputFile) {
		this.ProjectName = ProjectStr;
		this.BugFilePath = bugFilePath;
		this.SourceCodeDir = sourceCodeDir;
		this.SourceCodeRepo= gitRepo;
		this.WorkDir = workDir;
		this.Alpha = alpha;
		this.OutputFile = outputFile;
		
		this.Separator = System.getProperty("file.separator");
        this.LineSeparator = System.getProperty("line.separator");
        
	}
	

	public final String Separator;
	public final String LineSeparator;
	
	
	public final String BugFilePath;
	public final String SourceCodeDir;
	public final String SourceCodeRepo;
	public final String WorkDir;
	public final String OutputFile;
	public float Alpha;
	public final String ProjectName;
	
	
}
