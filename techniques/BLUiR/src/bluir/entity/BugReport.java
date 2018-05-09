package bluir.entity;

import java.util.Set;


public class BugReport
{
	String bugId;
	String summary;
	String description;
	Set<String> fixedFiles;
	
	public BugReport() {}
	
	public BugReport(String bugId, String summary, String description, Set<String> fixedFiles)
	{
		this.bugId = bugId;
		this.summary = summary;
		this.description = description;
		this.fixedFiles = fixedFiles;
	}
	
	public String getBugId()
	{
		return this.bugId;
	}
	
	public void setBugId(String bugId)
	{
		this.bugId = bugId;
	}
	
	public String getSummary()
	{
		return this.summary;
	}
	
	public void setSummary(String summary)
	{
		this.summary = summary;
	}
	
	public String getDescription()
	{
		return this.description;
	}
	
	public void setDescription(String description)
	{
		this.description = description;
	}
	
	public Set<String> getFixedFiles()
	{
		return this.fixedFiles;
	}
	
	public void setFixedFiles(Set<String> fixedFiles)
	{
		this.fixedFiles = fixedFiles;
	}
	
	public String toString()
	{
		return 
		
			"BugReport [bugId=" + this.bugId + ", summary=" + this.summary + ", description=" + this.description + ", fixedFiles=" + this.fixedFiles + "]";
	}
}
