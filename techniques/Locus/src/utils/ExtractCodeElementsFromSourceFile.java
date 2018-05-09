package utils;

import java.io.File;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.EnumConstantDeclaration;
import org.eclipse.jdt.core.dom.EnumDeclaration;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;



public class ExtractCodeElementsFromSourceFile {
	
	public static HashSet<String> extractCodeElements(String source) {
		File file = new File(source);
		if (!file.exists()) return new HashSet<String>();
		String sourceCode = ReadFileToList.readFiles(source);
//		System.out.println(sourceCode);
		ASTParser parser = ASTParser.newParser(AST.JLS8);
		parser.setSource(sourceCode.toCharArray());
		parser.setKind(ASTParser.K_COMPILATION_UNIT);
		CompilationUnit cu = (CompilationUnit) parser.createAST(null);
		CodeElementsVisitor visitor = new CodeElementsVisitor();
		cu.accept(visitor);
		return visitor.getCodeElements();
	}
	
	public static void main(String[] args) {
		String filename = "../Datasets/ZXing/ZXing_/zxing/rim/src/com/google/zxing/client/rim/AboutScreen.java";
		HashSet<String> clts = extractCodeElements(filename);
		System.out.println(clts.toString());
	}
}


class CodeElementsVisitor extends ASTVisitor {
	private HashSet<String> codeElements = new HashSet<String>();
	private CompilationUnit node;
	
	public HashSet<String> getCodeElements() {
//		System.out.println(codeElements);
		return codeElements;
	}
	
	@Override
	public boolean visit(CompilationUnit node) {
		this.node = node;
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MethodDeclaration node) {
		codeElements.add(node.getName().toString());
		return super.visit(node);
	}

	
//	@Override
//	public boolean visit(FieldDeclaration node) {
//		String name = node.toString().trim();
//		name = name.replace(";","");
//		if (name.contains("=")) {
//			name = name.substring(0, name.indexOf("="));
//		}
//		if (name.contains(" ")) {
//			String[] tmp = name.split(" ");
//			codeElements.add(tmp[tmp.length - 1]);
//		} else codeElements.add(name);
//		return super.visit(node);
//	}
}