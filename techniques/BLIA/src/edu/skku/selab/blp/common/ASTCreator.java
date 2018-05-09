/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.common;

import java.io.BufferedReader;

import java.io.File;
import java.io.FileReader;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class ASTCreator {
	private String content;

	public ASTCreator() {
		content = null;
	}

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
				contentBuffer.append((new StringBuilder(String.valueOf(line)))
						.append("\r\n").toString());
			content = contentBuffer.toString();
			reader.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public CompilationUnit getCompilationUnit() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(content.toCharArray());
		CompilationUnit compilationUnit = (CompilationUnit) parser.createAST(null);
		return compilationUnit;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}
}
