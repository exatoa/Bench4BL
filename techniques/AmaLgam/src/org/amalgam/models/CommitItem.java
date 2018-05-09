package org.amalgam.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * commit item Á¤ÀÇ
 * @author Zeck
 *
 */
public class CommitItem
{
	public String hash;
	public String comment;
	public Date commitDate;
	public ArrayList<String> files;
	
	public CommitItem() {
		hash = "";
		comment = "";
		files = new ArrayList<String>();
	}
}
