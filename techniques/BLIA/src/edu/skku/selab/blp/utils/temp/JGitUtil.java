/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.utils.temp;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class JGitUtil {

	/**
	 * 
	 */
	public JGitUtil() {
	}

	/**
	 * @param args
	 * @throws IOException 
	 * @throws GitAPIException 
	 * @throws NoHeadException 
	 */
	public static void main(String[] args) throws IOException, NoHeadException, GitAPIException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File("D:\\workspace\\aspectj\\org.aspectj\\.git"))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		
		Git git = new Git(repository);
		Iterator<RevCommit> commitLogs = git.log().call().iterator();
		if (commitLogs.hasNext()) {
			RevCommit currentCommit = commitLogs.next();
			RevCommit parentCommit = currentCommit.getParent(0);
			
			// prepare the two iterators to compute the diff between
			ObjectReader reader = repository.newObjectReader();
			CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
			oldTreeIter.reset(reader, parentCommit.getTree().getId());
			CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
			newTreeIter.reset(reader, currentCommit.getTree().getId());

			long timestamp = (long) currentCommit.getCommitTime() * 1000;
			Date commitTime = new Date(timestamp);
			System.out.printf("Committer: %s, Time: %s, Msg: %s\n",
					currentCommit.getCommitterIdent().getName(),
					commitTime.toString(), 
					currentCommit.getShortMessage());
			System.out.printf(">> Commit ID: %s\n", currentCommit.getId().name());
			
			// finally get the list of changed files
			List<DiffEntry> diffs = new Git(repository).diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
			for (DiffEntry entry : diffs) {
				System.out.printf("ChagngeType: %s, Path: %s\n", entry.getChangeType().toString(), entry.getPath(DiffEntry.Side.NEW));
			}
		}

		repository.close();
	}
}
