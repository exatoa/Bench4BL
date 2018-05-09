package org.buglocator.sourcecode.ast;

import java.io.*;
import java.util.ArrayList;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import org.buglocator.utils.Splitter;

public class FileParser {

	private CompilationUnit cu = null;

	/**
	 * 占쏙옙占쌍몌옙占쏙옙占퐅ava占식쇽옙占쏙옙迦占쏙옙CompilationUnit
	 * 
	 * @param file:java
	 *            占식쇽옙
	 * 
	 */
	public FileParser(File file) {
		ASTCreator creator = new ASTCreator();
		creator.getFileContent(file);
		cu = creator.getCompilationUnit();
	}

	/**
	 * 占쏙옙혤java占식쇽옙占식댐옙占쏙옙占쏙옙占쏙옙
	 * 
	 * @return 占쏙옙占쏙옙占쏙옙占쏙옙
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
	 * 占쏙옙혤占쏙옙占쏙옙캇占쏙옙캔占쏙옙占�
	 * 
	 * @return 占쏙옙占쏙옙캇占쏙옙캔占쏙옙占쏙옙占쏙옙占�
	 */
	public String[] getContent() {
		String[] tokensInSourceCode = Splitter.splitSourceCode(this.deleteNoNeededNode());
		StringBuffer sourceCodeContentBuffer = new StringBuffer();
		for (String token : tokensInSourceCode) {
			sourceCodeContentBuffer.append(token + " ");
		}
		String content = sourceCodeContentBuffer.toString().toLowerCase();
		return content.split(" ");
	}

	public String[] getClassNameAndMethodName() {
		String content = (this.getAllClassName() + " " + this.getAllMethodName()).toLowerCase();
		return content.split(" ");
	}

	/**
	 * 占쏙옙혤占식쇽옙占쏙옙占쌘곤옙占쏙옙
	 * 
	 * @return 占쏙옙占쏙옙
	 */
	public String getPackageName() {

		return cu.getPackage() == null ? "" : cu.getPackage().getName().getFullyQualifiedName();
	}

	/**
	 * 占쏙옙혤占식쇽옙占싻듸옙占쏙옙占싻뤄옙占쏙옙占쏙옙
	 * 
	 * @return 占쏙옙占쏙옙占쏙옙占쏙옙占쏙옙囹占�
	 */
	private String getAllMethodName() {
		ArrayList<String> methodNameList = new ArrayList<String>();
		for (int i = 0; i < cu.types().size(); i++) {
			TypeDeclaration type = (TypeDeclaration) cu.types().get(i);
			MethodDeclaration[] methodDecls = type.getMethods();
			for (MethodDeclaration methodDecl : methodDecls) {
				String methodName = methodDecl.getName().getFullyQualifiedName();
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
	 * 占쏙옙혤占식쇽옙占싻듸옙占쏙옙占쏙옙占쏙옙占쏙옙
	 * 
	 * @return 占쏙옙占쏙옙占쏙옙占쏙옙囹占�
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
	 * 占쏙옙占식쇽옙占싻뀐옙占쏙옙狼占쏙옙占쏙옙口
	 * 
	 * @return 占식쇽옙占쏙옙占쌍뤄옙占십�
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
}
