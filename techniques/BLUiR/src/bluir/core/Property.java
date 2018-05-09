package bluir.core;


public class Property {

	/**
	 * Singleton functions
	 */
	private static Property p = null;
	
	public static void createInstance(String projectName, String bugFilePath, String sourceCodeDir, String workDir, float alpha, String outputFile, String indriPath){
		if (p == null)
			p = new Property(projectName, bugFilePath, sourceCodeDir, workDir, alpha, outputFile, indriPath);
	}
	
	public static Property getInstance() {
		return p;
	}
	
	private Property(String projectName, String bugFilePath, String sourceCodeDir, String workDir, float alpha, String outputFile, String indriPath) {
		this.ProjectName = projectName;
		this.BugFilePath = bugFilePath;
		this.SourceCodeDir = sourceCodeDir;
		this.WorkDir = workDir;
		this.Alpha = alpha;
		this.OutputFile = outputFile;
		this.IndriPath = indriPath;
		
		this.Separator = System.getProperty("file.separator");
		this.LineSeparator = System.getProperty("line.separator");
		
		this.topN = 100;
		
	}
	


	public final String Separator;
	public final String LineSeparator;
	
	public String ProjectName;
	public String IndriPath;
	public final String BugFilePath;
	public final String SourceCodeDir;
	public final String WorkDir;
	public final float Alpha;
	public final String OutputFile;
	public int topN;
	
	public int FileCount;

}