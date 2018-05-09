package org.brtracer.sourcecode;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.brtracer.property.Property;
import org.brtracer.sourcecode.ast.FileDetector;
import org.brtracer.sourcecode.ast.FileParser;

public class LineofCode {
	private String fileName;
	private Integer loc;

	public void beginCount() throws Exception {
		FileDetector detector = new FileDetector("java");
		File[] files = detector.detect(Property.getInstance().SourceCodeDir);
		FileWriter writer = new FileWriter(
				Property.getInstance().WorkDir + Property.getInstance().Separator + "LOC.txt");

		for (File file : files) {
			loc = count(file);
			if (fileName.endsWith(".java")) {
				writer.write(fileName + "\t" + loc + Property.getInstance().LineSeparator);
			} else {
				writer.write(fileName + ".java" + "\t" + loc + Property.getInstance().LineSeparator);
			}
			writer.flush();
		}
		writer.close();
	}

	public Integer count(File file) throws IOException {
		FileParser parser = new FileParser(file);

		fileName = parser.getPackageName();
		if (fileName.trim().equals("")) {
			fileName = file.getName();
		} else {
			fileName += "." + file.getName();
		}

		/* modification for AspectJ */
		if (Property.getInstance().Project.startsWith("ASPECTJ")) {
			fileName = file.getPath();
			fileName = fileName.substring(Property.getInstance().Offset);
		}
		/* ************************** */

		fileName = fileName.substring(0, fileName.lastIndexOf("."));
		BufferedReader reader = new BufferedReader(new FileReader(file));
		Integer LoC = 0;
		String tmp;
		while (true) {
			tmp = reader.readLine();
			if (tmp == null)
				break;
			LoC++;
		}
		reader.close();
		return LoC;
	}
}
