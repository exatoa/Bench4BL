/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.common;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class FileDetector {
	private LinkedList<File> fileList;
	private String fileType;

	public FileDetector() {
		fileList = new LinkedList<File>();
		fileType = null;
	}

	public FileDetector(String fileType) {
		fileList = new LinkedList<File>();
		this.fileType = null;
		this.fileType = fileType;
	}
	
	public File[] detect(String absoluteFilePath) {
		File files[] = listFiles(absoluteFilePath);
		if (files != null)
			classifyFileAndDirectory(files);
		return (File[]) fileList.toArray(new File[fileList.size()]);
	}

	public File[] detect(String[] absoluteFilePathList) {
		File files[] = listFiles(absoluteFilePathList);
		if (files != null)
			classifyFileAndDirectory(files);
		return (File[]) fileList.toArray(new File[fileList.size()]);
	}
	
	private File[] listFiles(String[] absoluteFilePathList) {
		ArrayList<File[]> filesArray = new ArrayList<File[]>(); 
		for (int i = 0; i < absoluteFilePathList.length; i++) {
			File dir = new File(absoluteFilePathList[i]);
			filesArray.add(dir.listFiles());
		}
		
		int fileCount = 0;
		for (int i = 0; i < filesArray.size(); i++) {
			fileCount += filesArray.get(i).length;
		}
		
		File[] totalListFile = new File[fileCount];
		int k = 0;
		for (int i = 0; i < filesArray.size(); i++) {
			for (int j = 0; j < filesArray.get(i).length; j++) {
				totalListFile[k++] = filesArray.get(i)[j];
			}
		}
		
		return totalListFile;
	}
	
	private File[] listFiles(String absoluteFilePath) {
		File dir = new File(absoluteFilePath);
		return dir.listFiles();
	}

	private void classifyFileAndDirectory(File files[]) {
		File afile[];
		int j = (afile = files).length;
		for (int i = 0; i < j; i++) {
			File file = afile[i];
			if (file.isDirectory())
				detect(file.getAbsolutePath());
			else
				addFile(file);
		}

	}

	private void addFile(File file) {
		if (fileType == null)
			fileList.add(file);
		else
			addFileBySuffix(file);
	}

	private void addFileBySuffix(File file) {
		if (file.getName().endsWith(fileType))
			fileList.addLast(file);
	}
}
