/**
 * Copyright (c) 2014 by Software Engineering Lab. of Sungkyunkwan University. All Rights Reserved.
 * 
 * Permission to use, copy, modify, and distribute this software and its documentation for
 * educational, research, and not-for-profit purposes, without fee and without a signed licensing agreement,
 * is hereby granted, provided that the above copyright notice appears in all copies, modifications, and distributions.
 */
package edu.skku.selab.blp.blia.indexer;

import java.io.File;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffEntry.ChangeType;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

import edu.skku.selab.blp.db.CommitInfo;
import edu.skku.selab.blp.db.dao.CommitDAO;

/**
 * @author Klaus Changsun Youm(klausyoum@skku.edu)
 *
 */
public class GitCommitLogCollector implements ICommitLogCollector {
	private String repoDir;
	private String productName;
	
	/**
	 * 
	 */
	public GitCommitLogCollector(String productName, String repoDir) {
		this.repoDir = repoDir;
		this.productName = productName;
	}
	
	public void collectCommitLog(Date since, Date until, boolean collectForcely) throws Exception {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		Repository repository = builder.setGitDir(new File(repoDir))
		  .readEnvironment() // scan environment GIT_* variables
		  .findGitDir() // scan up the file system tree
		  .build();
		
		CommitDAO commitDAO = new CommitDAO();
		
		if (collectForcely) {
			commitDAO.deleteAllCommitInfo();
			commitDAO.deleteAllCommitFileInfo();
		}
		
		if (commitDAO.getCommitInfoCount(productName) == 0) {
			Git git = new Git(repository);
			Iterator<RevCommit> commitLogs = git.log().call().iterator();
			
			while (commitLogs.hasNext()) {
				RevCommit currentCommit = commitLogs.next();
				if (currentCommit.getParentCount() == 0) {
					break;
				}
				RevCommit parentCommit = currentCommit.getParent(0);
				
				// prepare the two iterators to compute the diff between
				ObjectReader reader = repository.newObjectReader();
				CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
				oldTreeIter.reset(reader, parentCommit.getTree().getId());
				CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
				newTreeIter.reset(reader, currentCommit.getTree().getId());

				long timestamp = (long) currentCommit.getCommitTime() * 1000;
				Date commitDate = new Date(timestamp);

				if (commitDate.after(until)) {
					continue;
				}
				
				if (commitDate.before(since)) {
					break;
				}
				
				CommitInfo commitInfo = new CommitInfo();
				commitInfo.setCommitID(currentCommit.getId().name());
				commitInfo.setCommitter(currentCommit.getCommitterIdent().getName());
				commitInfo.setMessage(currentCommit.getShortMessage());
				commitInfo.setCommitDate(commitDate);
				commitInfo.setProductName(productName);

				// debug code
//				System.out.printf("Committer: %s, Time: %s, Msg: %s\n",
//						commitInfo.getCommitter(),
//						commitInfo.getCommitDateString(), 
//						commitInfo.getMessage());
//				System.out.printf(">> Commit ID: %s\n", commitInfo.getCommitID());
				
				// finally get the list of changed files
				List<DiffEntry> diffs = new Git(repository).diff().setNewTree(newTreeIter).setOldTree(oldTreeIter).call();
				for (DiffEntry entry : diffs) {
					int commitType = convertCommitType(entry.getChangeType());
					String updatedFileName = entry.getPath(DiffEntry.Side.NEW);
				
					// ONLLY java files added to save computing time and space
					if (updatedFileName.contains(".java")) {
						commitInfo.addCommitFile(commitType, updatedFileName);
						
						// debug code
//						System.out.printf("ChagngeType: %d, Path: %s\n", commitType, updatedFileName);
					}
				}
				
				if (commitInfo.getAllCommitFilesWithoutCommitType().size() > 0) {
					commitDAO.insertCommitInfo(commitInfo);
				}
			}

			repository.close();
		}
	}
	
	private int convertCommitType(ChangeType changeType) {
		int commitType = -1;
		switch (changeType) {
		case ADD:
			commitType = CommitInfo.ADD_COMMIT;
			break;
		case MODIFY:
			commitType = CommitInfo.MODIFY_COMMIT;
			break;
		case DELETE:
			commitType = CommitInfo.DELETE_COMMIT;
			break;
		case RENAME:
			commitType = CommitInfo.RENAME_COMMIT;
			break;
		case COPY:
			commitType = CommitInfo.COPY_COMMIT;
			break;
		}
		
		return commitType;
	}

}
