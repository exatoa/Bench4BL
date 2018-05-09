package bluir.extraction;

import bluir.entity.BugReport;
import bluir.parser.XMLParser;
import bluir.utility.PreProcessor;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;






public class QueryExtractor
{
	public static int extractSumDesField(String XMLPath, String outputPath) throws IOException
	{
		XMLParser parser = new XMLParser();
		List<BugReport> bugRepo = parser.createRepositoryList(XMLPath);
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(outputPath));
		bw.write("<parameters>");
		bw.newLine();
		for (int i = 0; i < bugRepo.size(); i++)
		{

			BugReport bug = (BugReport)bugRepo.get(i);
			
			bw.write("\t<query>\n\t\t<number>" + bug.getBugId() + "</number>");
			bw.newLine();
			
			bw.write("\t\t<text> #weight(" + 
			
				addField(PreProcessor.process(bug.getSummary()), "class", 1.0D) + " " + 
				addField(PreProcessor.process1(bug.getSummary()), "class", 1.0D) + " " + 
				addField(PreProcessor.process(bug.getDescription()), "class", 1.0D) + " " + 
				addField(PreProcessor.process1(bug.getDescription()), "class", 1.0D) + " " + 
				
				addField(PreProcessor.process(bug.getSummary()), "method", 1.0D) + " " + 
				addField(PreProcessor.process1(bug.getSummary()), "method", 1.0D) + " " + 
				addField(PreProcessor.process(bug.getDescription()), "method", 1.0D) + " " + 
				addField(PreProcessor.process1(bug.getDescription()), "method", 1.0D) + " " + 
				
				addField(PreProcessor.process(bug.getSummary()), "identifier", 1.0D) + " " + 
				addField(PreProcessor.process1(bug.getSummary()), "identifier", 1.0D) + " " + 
				addField(PreProcessor.process(bug.getDescription()), "identifier", 1.0D) + " " + 
				addField(PreProcessor.process1(bug.getDescription()), "identifier", 1.0D) + " " + 
				
				addField(PreProcessor.process(bug.getSummary()), "comments", 1.0D) + " " + 
				
				addField(PreProcessor.process(bug.getDescription()), "comments", 1.0D) + " " + 
				


				")</text>\n\t</query>");
			bw.newLine();
		}

		bw.write("</parameters>");
		bw.newLine();
		
		bw.close();
		
		return bugRepo.size();
	}
	

	static String addField(String str, String fieldName)
	{
		String addedStr = "";
		
		String[] queryParts = str.split(" ");
		String[] arrayOfString1; int j = (arrayOfString1 = queryParts).length; for (int i = 0; i < j; i++) { String eachPart = arrayOfString1[i];
			if (!eachPart.equals("")) {
				eachPart = eachPart + ".(" + fieldName + ")";
				addedStr = addedStr + eachPart + " ";
			}
		}
		
		return addedStr;
	}
	
	static String addField(String str, String fieldName, double weight)
	{
		String addedStr = "";
		
		String[] queryParts = str.split(" ");
		String[] arrayOfString1; int j = (arrayOfString1 = queryParts).length; for (int i = 0; i < j; i++) { String eachPart = arrayOfString1[i];
			if (!eachPart.equals("")) {
				eachPart = weight + " " + eachPart + ".(" + fieldName + ")";
				addedStr = addedStr + eachPart + " ";
			}
		}
		
		return addedStr;
	}
	

	static String splitCamelCase(String s)
	{
		return s.replaceAll(
			String.format("%s|%s|%s", new Object[] {
			"(?<=[A-Z])(?=[A-Z][a-z])", 
			"(?<=[^A-Z])(?=[A-Z])", 
			"(?<=[A-Za-z])(?=[^A-Za-z])" }), 
			
			" ");
	}
	


	static void calculateRatio(String XMLPath)
	{
		XMLParser parser = new XMLParser();
		List<BugReport> bugRepo = parser.createRepositoryList(XMLPath);
		
		double ratioSum = 0.0D;
		
		int sumTotal = 0;
		int desTotal = 0;
		
		for (int i = 0; i < bugRepo.size(); i++) {
			BugReport bug = (BugReport)bugRepo.get(i);
			
			int summaryLength = countWord(bug.getSummary());
			int desLength = countWord(bug.getDescription());
			sumTotal += summaryLength;
			desTotal += desLength;
			double ratio = summaryLength / desLength;
			ratioSum += ratio;
			System.out.println(summaryLength + "\t" + desLength + "\t" + ratio);
		}
		System.out.println(ratioSum / bugRepo.size() + "\t" + sumTotal / desTotal);
	}
	

	static int countWord(String subject)
	{
		String[] word = PreProcessor.process(subject).split(" ");
		int c = 0;
		for (int j = 0; j < word.length; j++) {
			if (word[j].length() > 2) {
				c++;
			}
		}
		
		return c;
	}
}

