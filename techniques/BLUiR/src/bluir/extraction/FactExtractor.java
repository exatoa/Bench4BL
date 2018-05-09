package bluir.extraction;

import bluir.core.GenericVisitorFact;
import bluir.core.Property;
import bluir.utility.FileSystem;
import bluir.utility.PreProcessor;
import bluir.utility.StructureDiffUtils;
import bluir.utility.FileDetector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;

public class FactExtractor
{
//	public static void main(String[] args)
//	{
//		try
//		{
//			extractEclipseFacts("/Users/ripon/ICSM2013Repo/eclipse-src", "/users/ripon/Desktop/eclipse-facts");
//		}
//		catch (IOException e) {
//			e.printStackTrace();
//		}
//		catch (CoreException e) {
//			e.printStackTrace();
//		}
//	}
//	
	
	private static Document fDocument;
	
	public static boolean extractEclipseFacts(String codeDirectory, String destinationDirectory) throws IOException, CoreException
	{
		File destDir = new File(destinationDirectory);
		if (!destDir.exists()) {
			boolean success = destDir.mkdirs();
			if (!success) {
				System.out.println("Could not create the document collection directory.");
				System.out.println("Stopping Execution....");
				return false;
			}
		}
		//경로 보정
		if (!codeDirectory.endsWith("\\"))	codeDirectory = codeDirectory + "\\";
		codeDirectory = codeDirectory.replace("\\", "/");
		
		//File listing
		FileDetector detector = new FileDetector("java"); // java file Filter
		File[] files = detector.detect(codeDirectory);
		
		int count = 1;
		int fileCount = 0;
		
		BufferedWriter bwIndex = new BufferedWriter(new FileWriter(Property.getInstance().WorkDir + "FileIndex.txt"));
		
		for (File srcFile : files){		
			fileCount++;			
			String filePath = srcFile.getAbsolutePath();
			String sourceFileName = srcFile.getName();
			String relativeFilePath;
			
			//경로 보정
			filePath = filePath.replace("\\", "/");
			relativeFilePath = filePath.substring(codeDirectory.length());
			
			//Source code File Load
			fDocument = new Document(StructureDiffUtils.readFile(filePath));
						
			String fileName = "doc-" + count;			
			File file = new File(destinationDirectory + "/" + fileName);
			if (!file.exists())
				file.createNewFile();
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			
			ASTParser parser = ASTParser.newParser(3);
			String source = FileSystem.readFile(filePath);
			
			parser.setSource(source.toCharArray());
			parser.setKind(8);
			CompilationUnit cu = (CompilationUnit)parser.createAST(null);
			
			GenericVisitorFact visitor = new GenericVisitorFact();
			cu.accept(visitor);
			
			//package값이 없는 경우에 대한 처리 (추가)
			String fqn = relativeFilePath;
			PackageDeclaration packageNameObj = cu.getPackage();
			if (packageNameObj!=null && !Property.getInstance().ProjectName.startsWith("ASPECTJ")) 
				fqn = packageNameObj.getName().getFullyQualifiedName() + "." + sourceFileName;
			
			//String packageName = cu.getPackage().getName().getFullyQualifiedName();
			//System.out.println("Extracted Class FQN: " + packageName + "." + sourceFileName);
			
			//파일 ID정보 저장.
			bwIndex.write(fileCount + "\t" + relativeFilePath + "\t" + fqn);
			bwIndex.newLine();
			bwIndex.flush();
			//System.out.println(fileCount+": "+relativeFilePath + "\t" + fqn);
			
			//DOC정보 생성
			bw.write("<DOC>\n<DOCNO>" + fqn + " </DOCNO>\n<text>");
			bw.newLine();
			
			//클래스 정보 추출 및 저장
			List<String> classes = visitor.getClassNames();
			bw.write("<class>");
			bw.newLine();
			for (String cls : classes)
			{
				bw.write(PreProcessor.transform(cls));
				bw.newLine();
			}
			bw.write("</class>");
			bw.newLine();
			
			
			//method 정보 추출
			List<String> methods = visitor.getMethodNames();
			bw.write("<method>");
			bw.newLine();			
			if (methods.size() == 0) {
				bw.write("NONE");
				bw.newLine();
			}
			for (String methodName : methods)
			{
				bw.write(PreProcessor.transform(methodName));
				bw.newLine();
			}
			bw.write("</method>");
			bw.newLine();
			
			List<String> idNames = visitor.getIdentifierNames();
			
			//식별자 정보 추출
			bw.write("<identifier>");
			bw.newLine();
			for (String idName : idNames)
			{
				bw.write(PreProcessor.transform(idName));
				bw.newLine();
			}
			bw.write("</identifier>");
			bw.newLine();
			
			//comments 정보 추출
			List<ASTNode> comments = cu.getCommentList();
			bw.write("<comments>");
			bw.newLine();
			for (ASTNode comment: comments) {
				bw.write(PreProcessor.process(getCommentString(comment)));
				bw.newLine();
			}
			
			bw.write("</comments>");
			bw.newLine();
			bw.write("</text>\n</DOC>");
			bw.newLine();
			bw.close();
			count++;
		}
		bwIndex.close();
		
		Property.getInstance().FileCount = fileCount;
		return true;
	}
	
	public static void extractFacts(String codeDirectory, String destinationDirectory) throws IOException, CoreException
	{
		int offset = codeDirectory.length();

		
		//File listing
		FileDetector detector = new FileDetector("java"); // java file Filter
		File[] files = detector.detect(codeDirectory);
		
		int count = 1;
		int fileCount = 0;
		//BufferedWriter bwIndex = new BufferedWriter(new FileWriter(Property.getInstance().WorkDir + "FileIndex.txt"));
		for (File srcFile : files){	
			fileCount++;
			
			String filePath = srcFile.getAbsolutePath();
			System.out.println(fileCount + "\t" + filePath);
			
			fDocument = new Document(StructureDiffUtils.readFile(filePath));
			
			String classFQN = filePath.substring(offset + 1).replace("/", ".");
			String key = "";			
			String fileName = "doc/file" + count;
			
			File file = new File(destinationDirectory + "/" + fileName);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			key = classFQN.substring(4);
			bw.write("<DOC>\n<DOCNO>" + key + " </DOCNO>\n<text>");
			bw.newLine();
			

			ASTParser parser = ASTParser.newParser(3);
			String source = FileSystem.readFile(filePath);
			
			parser.setSource(source.toCharArray());
			parser.setKind(8);
			CompilationUnit cu = (CompilationUnit)parser.createAST(null);
			
			GenericVisitorFact visitor = new GenericVisitorFact();
			cu.accept(visitor);
			
			List<String> classes = visitor.getClassNames();
			
			bw.write("<class>");
			bw.newLine();
			for (String cls : classes) {
				bw.write(cls);
				bw.newLine();
				
				bw.write(PreProcessor.transform(cls));
				bw.newLine();
			}
			bw.write("</class>");
			bw.newLine();
			
			List<String> methods = visitor.getMethodNames();
			
			bw.write("<method>");
			bw.newLine();
			
			if (methods.size() == 0) {
				bw.write("NONE");
				bw.newLine();
			}
			for (String methodName : methods) {
				bw.write(methodName);
				bw.newLine();
				bw.write(PreProcessor.transform(methodName));
				bw.newLine();
			}
			bw.write("</method>");
			bw.newLine();
			
			List<String> idNames = visitor.getIdentifierNames();
			
			bw.write("<identifier>");
			bw.newLine();
			for (String idName : idNames) {
				bw.write(idName);
				bw.newLine();
				bw.write(PreProcessor.transform(idName));
				bw.newLine();
			}
			bw.write("</identifier>");
			bw.newLine();
			
			List<ASTNode> list = cu.getCommentList();
			bw.write("<comments>");
			bw.newLine();
			for (int i = 0; i < list.size(); i++) {
				bw.write(PreProcessor.process(getCommentString(list.get(i))));
				bw.newLine();
			}
			
			bw.write("</comments>");
			bw.newLine();
			bw.write("</text>\n</DOC>");
			bw.newLine();
			bw.close();
			count++;
		}
		
		System.out.println("End");
	}
	
	public static void extractFactsDefault(String codeDirectory, String destinationDirectory) throws IOException, CoreException {
		
		int offset = codeDirectory.length();
		

		//File listing
		FileDetector detector = new FileDetector("java"); // java file Filter
		File[] files = detector.detect(codeDirectory);
		
		int count = 1;
		int fileCount = 0;
		
		BufferedWriter bwIndex = new BufferedWriter(new FileWriter(Property.getInstance().WorkDir + "FileIndex.txt"));
		
		for (File srcFile : files){		
			fileCount++;			
			String filePath = srcFile.getAbsolutePath();
			//bwIndex.write(fileCount + "\t" + filePath.substring(codeDirectory.length()+1)+"\n");
			System.out.println(fileCount + "\t" + filePath);
						
			//Source code File Load
			fDocument = new Document(StructureDiffUtils.readFile(filePath));
			
			String classFQN = filePath.substring(offset + 1).replace("/", ".");
			String key = "";
			String fileName = "doc_default/file" + count;
			
			File file = new File(destinationDirectory + "/" + fileName);
			

			if (!file.exists()) {
				file.createNewFile();
			}
			
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			
			key = classFQN;
			bw.write("<DOC>\n<DOCNO>" + key + " </DOCNO>\n<text>");
			bw.newLine();
			

			ASTParser parser = ASTParser.newParser(3);
			String source = FileSystem.readFile(filePath);
			
			parser.setSource(source.toCharArray());
			parser.setKind(8);
			CompilationUnit cu = (CompilationUnit)parser.createAST(null);
			
			GenericVisitorFact visitor = new GenericVisitorFact();
			cu.accept(visitor);
			
			List<String> classes = visitor.getClassNames();
			
			bw.write("<class>");
			bw.newLine();
			for (String cls : classes)
			{
				bw.write(PreProcessor.transform(cls).trim());
				bw.newLine();
			}
			bw.write("</class>");
			bw.newLine();
			
			List<String> methods = visitor.getMethodNames();
			
			bw.write("<method>");
			bw.newLine();
			
			if (methods.size() == 0) {
				bw.write("NONE");
				bw.newLine();
			}
			for (String methodName : methods)
			{
				bw.write(PreProcessor.transform(methodName));
				bw.newLine();
			}
			bw.write("</method>");
			bw.newLine();
			
			List<String> idNames = visitor.getIdentifierNames();
			
			bw.write("<identifier>");
			bw.newLine();
			for (String idName : idNames)
			{
				bw.write(PreProcessor.transform(idName));
				bw.newLine();
			}
			bw.write("</identifier>");
			bw.newLine();
			
			List<ASTNode> comments = cu.getCommentList();
			bw.write("<comments>");
			bw.newLine();
			for (ASTNode comment: comments) {
				bw.write(PreProcessor.process(getCommentString(comment)));
				bw.newLine();
			}			
			bw.write("</comments>");
			bw.newLine();
			bw.write("</text>\n</DOC>");
			bw.newLine();
			bw.close();
			count++;
		}
		
		System.out.println("End");
	}

	
	private static String getCommentString(ASTNode node)
	{
		try {
			return fDocument.get(node.getStartPosition(), node.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
		return null;
	}
}

