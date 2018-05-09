package org.brtracer.sourcecode.ast;

/**
 * Name Content (class, method Name) 항목 추가
 * @author Zeck
 *
 */
public class Corpus {
	private String javaFileFullClassName;
	private String javaFilePath;
	private String content;
	private String nameContent;	//added feild
		
	public String getJavaFileFullClassName() {
		return javaFileFullClassName;
	}
	public void setJavaFileFullClassName(String javaFileFullClassName) {
		this.javaFileFullClassName = javaFileFullClassName;
	}
	public String getJavaFilePath() {
		return javaFilePath;
	}
	public void setJavaFilePath(String javaFilePath) {
		this.javaFilePath = javaFilePath;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	
	/*Added method*/
	public String getNameContent() {
		return nameContent;
	}
	public void setNameContent(String content) {
		this.nameContent = content;
	}
	
}
