package org.brtracer.sourcecode.ast;

import java.io.*;
import java.util.ArrayList;

import org.brtracer.utils.Splitter;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class FileParser {

	private CompilationUnit cu = null;

	/**
	 * ?��?��?��?��?��몌옙?��?��?��?��?��ava?��?��?��?��?��?��?���??��?��?��CompilationUnit
	 * 
	 * @param file:java ?��?��?��?��
	 *            
	 */
	public FileParser(File file) {
		ASTCreator creator = new ASTCreator();
		creator.getFileContent(file);
		cu = creator.getCompilationUnit();
	}

	/**
	 * ?��?��?��?��java?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��
	 * 
	 * @return ?��?��?��?��?��?��?��?��?��?��?��?��
	 */
	public int getLinesOfCode() {
		this.deleteNoNeededNode();
		String[] lines = cu.toString().split("\n");
		int len = 0;
		for (String strLine : lines) {
			if (!strLine.trim().equals("")) {
				len++;

			}
		}
		return len;
	}

	/**
	 * ?��?��?��?��?��?��?��?��?��?��캇占?��?��캔占?��?��?���?
	 * 
	 * @return ?��?��?��?��?��?��캇占?��?��캔占?��?��?��?��?��?��?��?��?���?
	 */
	public String[] getContent() {
		String[] tokensInSourceCode = Splitter.splitSourceCode(this
				.deleteNoNeededNode());
		StringBuffer sourceCodeContentBuffer = new StringBuffer();
		for (String token : tokensInSourceCode) {
			sourceCodeContentBuffer.append(token + " ");
		}
		String content = sourceCodeContentBuffer.toString().toLowerCase();
		return content.split(" ");
	}

	public String[] getClassNameAndMethodName() {
		String content = (this.getAllClassName() + " " + this
				.getAllMethodName()).toLowerCase();
		return content.split(" ");
	}

	/**
	 * ?��?��?��?��?��?��?��?��?��?��?��?��?��곤옙?��?��?��
	 * 
	 * @return ?��?��?��?��?��?��
	 */
	public String getPackageName() {

		return cu.getPackage() == null ? "" : cu.getPackage().getName()
				.getFullyQualifiedName();
	}

	/**
	 * ?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��뤄옙?��?��?��?��?��?��
	 * 
	 * @return ?��?��?��?��?��?��?��?��?��?��?��?��?�占?��?��?��?���?
	 */
	private String getAllMethodName() {
		ArrayList<String> methodNameList = new ArrayList<String>();
		for (int i = 0; i < cu.types().size(); i++) {
			TypeDeclaration type = (TypeDeclaration) cu.types().get(i);
			MethodDeclaration[] methodDecls = type.getMethods();
			for (MethodDeclaration methodDecl : methodDecls) {
				String methodName = methodDecl.getName()
						.getFullyQualifiedName();
				methodNameList.add(methodName);
			}
		}
		String allMethodName = "";
		for (String methodName : methodNameList) {
			allMethodName += methodName + " ";
		}
		return allMethodName.trim();

	}

	/**
	 * ?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��?��
	 * 
	 * @return ?��?��?��?��?��?��?��?��?��?�占?��?��?��?���?
	 */
	private String getAllClassName() {
		ArrayList<String> classNameList = new ArrayList<String>();
		for (int i = 0; i < cu.types().size(); i++) {
			TypeDeclaration type = (TypeDeclaration) cu.types().get(i);
			String name = type.getName().getFullyQualifiedName();
			classNameList.add(name);
		}
		String allClassName = "";
		for (String className : classNameList) {
			allClassName += className + " ";
		}
		return allClassName.trim();
	}

	/**
	 * ?�占?��?��?��?��?��?��?��?��?�옙?��?��?��狼占?��?��?��?��?��?��
	 * 
	 * @return ?��?��?��?��?��?��?��?��?��뤄옙?��?���?
	 */
	private String deleteNoNeededNode() {
		cu.accept(new ASTVisitor() {
			public boolean visit(AnnotationTypeDeclaration node) {
				if (node.isPackageMemberTypeDeclaration()) {

					node.delete();
				}
				return super.visit(node);
			}
		});
		cu.accept(new ASTVisitor() {
			public boolean visit(PackageDeclaration node) {
				node.delete();
				return super.visit(node);
			}
		});
		cu.accept(new ASTVisitor() {
			public boolean visit(ImportDeclaration node) {
				node.delete();
				return super.visit(node);
			}
		});
		return cu.toString();
	}

	public void getImport(final FileWriter writeImport){
		cu.accept(new ASTVisitor() {
			@Override
			public boolean visit(ImportDeclaration node) {
				try {
					writeImport.write(node.getName() + " ");
				} catch (IOException e) {
					e.printStackTrace();
				}
				return super.visit(node);
			}
		});
	}
}
