package org.buglocator.sourcecode;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.TreeSet;
import org.buglocator.property.Property;
import org.buglocator.sourcecode.ast.Corpus;
import org.buglocator.sourcecode.ast.FileDetector;
import org.buglocator.sourcecode.ast.FileParser;
import org.buglocator.utils.Stem;
import org.buglocator.utils.Stopword;

public class CodeCorpusCreator
{
	private final String workDir = Property.getInstance().WorkDir;
	private final String codePath = Property.getInstance().SourceCodeDir;
	private final String pathSeparator = Property.getInstance().Separator;
	private final String lineSeparator = Property.getInstance().LineSeparator;
	private final String projectName = Property.getInstance().ProjectName;
	
	public CodeCorpusCreator() throws IOException, ParseException
	{}
	
	/**
	 * 시작 함수.
	 * @throws Exception
	 */
	public void create() throws Exception
	{
		int count = 0;
		TreeSet<String> nameSet = new TreeSet<String>();
		
		//File listing
		FileDetector detector = new FileDetector("java"); // java file Filter
		File[] files = detector.detect(codePath);
		
		//preparing output File.
		FileWriter writeCorpus = new FileWriter(workDir + pathSeparator + "CodeCorpus.txt");
		FileWriter writer = new FileWriter(workDir + pathSeparator + "ClassName.txt");
		
		//make corpus each file
		for (File file: files) {
			Corpus corpus = this.create(file);	//Corpus 생성.
			if (corpus == null)	continue;
			
			//file filtering  (중복방지)
			String FullClassName = corpus.getJavaFileFullClassName();
			if (projectName.startsWith("ASPECTJ")){
				FullClassName = file.getPath().substring(codePath.length()); //경로명을 통한 인식.
				FullClassName = FullClassName.replace("\\", "/");
				if (FullClassName.startsWith("/")) 
					FullClassName = FullClassName.substring(1); //경로명을 통한 인식.
				
			}
			if (nameSet.contains(FullClassName)) continue;
			
		
			//Write File.
			if (!FullClassName.endsWith(".java"))	FullClassName +=  ".java";
			writer.write(count + "\t" + FullClassName + this.lineSeparator);
			writeCorpus.write(FullClassName + "\t" + corpus.getContent() + this.lineSeparator);
			writer.flush();
			writeCorpus.flush();
			
			//Update Filter			
			nameSet.add(FullClassName); //corpus.getJavaFileFullClassName());
			count++;
		}
		Property.getInstance().FileCount = count;
		writeCorpus.close();
		writer.close();

	}
	
	/**
	 * 각 파일에 대해서 corpus를 생성
	 * @param file
	 * @return
	 */
	public Corpus create(File file) {
		FileParser parser = new FileParser(file);
		
		//파일의 패키지 정보 얻기
		String fileName = parser.getPackageName();
		if (fileName.trim().equals("")) {
			fileName = file.getName();
		} else {
			fileName = fileName + "." + file.getName();
		}
		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		
		//content를 분리하여 stemming, removing stopwords 수행
		String[] content = parser.getContent();
		StringBuffer contentBuf = new StringBuffer();
		for (String word : content) {	//camel case 분리 tokenize된 content들임.
			String stemWord = Stem.stem(word.toLowerCase());
			if ((!Stopword.isKeyword(word)) && (!Stopword.isEnglishStopword(word)))
			{
				contentBuf.append(stemWord);
				contentBuf.append(" ");
			}
		}
		String sourceCodeContent = contentBuf.toString();
		
		//클래스명, 메소드명에 대해서 별도로 corpus를 한번 더 생성.
		String[] classNameAndMethodName = parser.getClassNameAndMethodName();
		StringBuffer nameBuf = new StringBuffer();
		
		for (String word: classNameAndMethodName) {			
			String stemWord = Stem.stem(word.toLowerCase());
			nameBuf.append(stemWord);
			nameBuf.append(" ");
		}
		String names = nameBuf.toString();
		
		//corpus객체 생성.
		Corpus corpus = new Corpus();
		corpus.setJavaFilePath(file.getAbsolutePath());
		corpus.setJavaFileFullClassName(fileName);
		corpus.setContent(sourceCodeContent + " " + names);	//content내에 두 corpus가 결합.
		return corpus;
	}
}
