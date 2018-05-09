package org.buglocator.sourcecode.ast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;



public class ASTCreator
{
	private String content = null;
	
	public void getFileContent(File file) {
		getFileContent(file.getAbsolutePath());
	}
	
	public void getFileContent(String absoluteFilePath) {
		try {
			StringBuffer contentBuffer = new StringBuffer();
			String line = null;
			BufferedReader reader = new BufferedReader(new FileReader(
				absoluteFilePath));
			while ((line = reader.readLine()) != null)
				contentBuffer.append(line + "\r\n");
			this.content = contentBuffer.toString();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public CompilationUnit getCompilationUnit() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(this.content.toCharArray());
		CompilationUnit cu = (CompilationUnit)parser.createAST(null);
		return cu;
	}
}
