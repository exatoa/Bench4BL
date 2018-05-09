package org.buglocator;

import java.util.TreeSet;


/**
 * Public property로 bug클래스 재정의
 * @author Zeck
 *
 */
public class Bug
{
	public String bugId;
	public String openDate;
	public String fixDate;
	public String bugSummary;
	public String bugDescription;
	public TreeSet<String> set = new TreeSet<String>();
}