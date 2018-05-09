/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeSet;

import edu.skku.selab.blp.Property;
import edu.skku.selab.blp.common.SourceFileCorpus;
import edu.skku.selab.blp.common.FileDetector;
import edu.skku.selab.blp.common.FileParser;
import edu.skku.selab.blp.db.dao.BaseDAO;
import edu.skku.selab.blp.db.dao.SourceFileDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class StructuredSourceFileCorpusCreator extends SourceFileCorpusCreator {
	public SourceFileCorpus create(File file) {
		FileParser parser = new FileParser(file);
		String fileName = parser.getPackageName();
		if (fileName.trim().equals("")) {
			fileName = file.getName();
		} else {
			fileName = (new StringBuilder(String.valueOf(fileName)))
					.append(".").append(file.getName()).toString();
		}
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		
		// parser.getImportedClassed() function should be called before calling parser.getContents()
		ArrayList<String> importedClasses = parser.getImportedClasses();
		
		String classIdentifiers[] = parser.getStructuredContentWithFullyIdentifier(FileParser.CLASS_PART);		
		String classPart = stemContent(classIdentifiers);
		String classContents[] = parser.getStructuredContent(FileParser.CLASS_PART);
		classPart += " " + stemContent(classContents);

		String methodIdentifiers[] = parser.getStructuredContentWithFullyIdentifier(FileParser.METHOD_PART);
		String methodPart = stemContent(methodIdentifiers);
		String methodContents[] = parser.getStructuredContent(FileParser.METHOD_PART);
		methodPart += " " + stemContent(methodContents);

//		String variablePart = parser.getStructuredContentWithFullyIdentifier(FileParser.VARIABLE_PART);	
//		String variableContents[] = parser.getStructuredContent(FileParser.VARIABLE_PART);
		
		String variableContents[] = parser.getStructuredContent(FileParser.VARIABLE_PART);
		String variablePart = stemContent(variableContents);

		String commentContents[] = parser.getStructuredContent(FileParser.COMMENT_PART);
		String commentPart = stemContent(commentContents);
		
		String sourceCodeContent = classPart + " " + methodPart + " " + variablePart + " " + commentPart;
		
		SourceFileCorpus corpus = new SourceFileCorpus();
		corpus.setJavaFilePath(file.getAbsolutePath());
		corpus.setJavaFileFullClassName(fileName);
		corpus.setImportedClasses(importedClasses);
		corpus.setContent(sourceCodeContent);
		corpus.setClassPart(classPart);
		corpus.setMethodPart(methodPart);
		corpus.setVariablePart(variablePart);
		corpus.setCommentPart(commentPart);
		return corpus;
    }
	
	////////////////////////////////////////////////////////////////////	
	/* (non-Javadoc)
	 * @see edu.skku.selab.blia.indexer.ICorpus#create()
	 */
	public void create(String version) throws Exception {
		Property property = Property.getInstance();
		FileDetector detector = new FileDetector("java");
		File files[] = detector.detect(property.sourceCodeDirList);
		
		SourceFileDAO sourceFileDAO = new SourceFileDAO();
		String productName = property.productName;
		int totalCoupusCount = SourceFileDAO.INIT_TOTAL_COUPUS_COUNT;
		double lengthScore = SourceFileDAO.INIT_LENGTH_SCORE;

		// debug code
//		System.out.printf("Source code dir: %s\n", property.getSourceCodeDir());
//		
//		FileWriter tempWriter = new FileWriter(".\\temp.txt");
//		for (int i = 0; i < files.length; i++) {
//			tempWriter.write("[" + (i+1) + "] " + files[i].getAbsolutePath() + "\n"); 
//			System.out.printf("[%d] %s\n", i + 1, files[i].getAbsolutePath());
//		}
//		tempWriter.close();

		int count = 0;
		TreeSet<String> nameSet = new TreeSet<String>();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			SourceFileCorpus corpus = create(file);

			if (corpus != null && !nameSet.contains(corpus.getJavaFileFullClassName())) {
				String className = corpus.getJavaFileFullClassName();
				if (!corpus.getJavaFileFullClassName().endsWith(".java")) {
					className += ".java";
				}
				
				//String fileName = file.getAbsolutePath().replace("\\", "/");
				//fileName = fileName.replace("/", ".");
				
				String fileName = file.getAbsolutePath().replace("\\", "/");
				if (Property.getInstance().productName.compareTo(Property.ASPECTJ)==0)
					fileName = fileName.substring(Property.getInstance().sourceCodeDir.length());
				else{
					fileName = fileName.replace("/", ".");
				
					// Wrong file that has invalid package or path
					if (!fileName.endsWith(className)) {
						System.err.printf("[StructuredSourceFileCorpusCreator.create()] %s, %s\n", fileName, className);
						continue;
					}
					fileName = className;
				}
				
				int sourceFileID = sourceFileDAO.insertSourceFile(fileName, className, productName);
				if (BaseDAO.INVALID == sourceFileID) {
					System.err.printf("[StructuredSourceFileCorpusCreator.create()] %s insertSourceFile() failed.\n", className);
					throw new Exception(); 
				}
				
				int sourceFileVersionID = sourceFileDAO.insertStructuredCorpusSet(sourceFileID, version, corpus, totalCoupusCount, lengthScore);
				if (BaseDAO.INVALID == sourceFileVersionID) {
					System.err.printf("[StructuredSourceFileCorpusCreator.create()] %s insertCorpusSet() failed.\n", className);
					throw new Exception(); 
				}

				sourceFileDAO.insertImportedClasses(sourceFileVersionID, corpus.getImportedClasses());
				nameSet.add(corpus.getJavaFileFullClassName());
				count++;
			}
		}

		property.fileCount = count;
	}
}
