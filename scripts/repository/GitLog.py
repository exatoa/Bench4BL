#-*- coding: utf-8 -*-
'''
Created on 2016. 11. 28
Updated on 2016. 11. 28
'''
from __future__ import print_function
import codecs
import sys
import re
import os
import subprocess
from utils import Progress

class GitLog:
	'''
	Load git commit log information
	'''
	ProjectName = u''
	GitRepoPath = u''
	GitLogPath = u''
	parentPath = u''

	logs = {}

	def __init__(self, _projectName,  _gitPath, _logpath):
		self.__name__ = _projectName
		self.ProjectName = _projectName
		self.GitRepoPath = _gitPath
		self.GitLogPath = _logpath
		pass

	#######################################################################
	# Git log Load
	#######################################################################
	def make(self):
		'''
		Save git commit information to GitLogPath (with --pretty option)
		:return:
		'''
		print(u'[%s] making Git log file....'%self.ProjectName, end=u'')

		# check this branch
		command = [u'git', u'branch']
		result = subprocess.check_output(command, stderr=sys.stderr, cwd=self.GitRepoPath)
		if result is None:
			print(u'Failed')
			return False

		# if the head is not up-to-date, checkout up-to-date
		result = result.strip()
		if result[-1] == '\n': result = result[:-1]
		items = result.split('\n')
		if len(items)> 1:
			command = [u'git', u'checkout', items[-1].strip()]
			result = subprocess.check_output(command, stderr=sys.stderr, cwd=self.GitRepoPath)
			if result is None:
				print(u'Failed')
				return False

		command = [u'git', u'log', u'--reflog', u'--name-status', u'--pretty=format:---------------------%nhash:%H%nauthor:%an%ncommit_date:%ci%nmessage:%s%n']
		output = file(self.GitLogPath, 'w')
		result = subprocess.call(command, stdout=output, stderr=sys.stderr, cwd=self.GitRepoPath)
		output.close()
		if result is None:
			print(u'Failed')
			return False

		print(u'Done')
		return True

	def file_loader(self, _f, _with_filter=True):
		log = {'hash':u'', 'author':u'', 'commit_date':u'', 'message':u'', 'fixedFiles':{}}

		for line in _f:
			if line == u'---------------------\n':
				#if len(log['fixedFiles']) > 0:
				yield log
				log = {'hash': u'', 'author': u'', 'commit_date': u'', 'message': u'', 'fixedFiles': {}}
				continue
			if line.startswith(u'hash:'):
				log['hash'] = line[5:-1].strip()
				continue
			if line.startswith(u'author:'):
				log['author'] = line[7:-1].strip()
				continue
			if line.startswith(u'commit_date:'):
				log['commit_date'] = line[12:-1].strip()[:-5]
				continue
			if line.startswith(u'message:'):
				log['message'] = line[8:-1].strip()
				continue
			if line.startswith(u'A\t') or line.startswith(u'M\t') or line.startswith(u'D\t'):
				fname = line[2:-1].strip()
				ftype = line[:1]
				if _with_filter is True and self.filetype_fileter(fname) is False: continue
				log['fixedFiles'][fname] = ftype
				continue
			# We don't use this type of files. (maybe it has no context in the code)
			# if line.startswith(u'A\t'):
			# 	log['fixedFiles'][line[2:-1].strip()] = line[:1]
			#	continue
		pass

	def filetype_fileter(self, filename):
		return filename.endswith(u'.java')  # not this type of files returns False

	def load(self, _force=False):
		'''
		Load commit info from GitLogPath
		:return:  {bugID:[{'hash':u'', 'author':u'', 'commit_date':u'', 'message':u'', 'fixedFiles':{}}, {}, ...], ...}
		'''
		if os.path.exists(self.GitLogPath) is False or _force is True:
			self.make()

		logfile = codecs.open(self.GitLogPath, 'r', 'utf-8')
		progress = Progress(u'[%s] loading git log data' % self.__name__, 1000, 20000, False)
		progress.set_point(0)
		progress.start()
		for logitem in self.file_loader(logfile, _with_filter=False):
			# filter unuseful logs
			if len(logitem['fixedFiles'])==0: continue

			# We only use bug report id in log message
			# mapping bug report ID
			logitem['linked_bug'] = re.findall(r'%s-[0-9]+'%self.ProjectName.upper(),logitem['message'])
			logitem['linked_bug'] = set(logitem['linked_bug'])
			for linked_id in logitem['linked_bug']:
				if linked_id not in self.logs:
					self.logs[linked_id] = [logitem]
				else:
					self.logs[linked_id].append(logitem)
			progress.check()
		progress.done()
		logfile.close()
		return self.logs

	def load_raw(self, _force=False):
		'''
		Load commit info from GitLogPath
		:return:  {bugID:[{'hash':u'', 'author':u'', 'commit_date':u'', 'message':u'', 'fixedFiles':{}}, {}, ...], ...}
		'''
		if os.path.exists(self.GitLogPath) is False or _force is True:
			self.make()

		self.logs = []
		logfile = codecs.open(self.GitLogPath, 'r', 'utf-8')
		progress = Progress(u'[%s] loading git log data' % self.__name__, 1000, 20000, False)
		progress.set_point(0)
		progress.start()
		for logitem in self.file_loader(logfile):
			# filter unuseful logs
			#if len(logitem['fixedFiles'])==0: continue
			if logitem['hash'] == '': continue
			self.logs.insert(0, logitem)
			progress.check()
		progress.done()
		logfile.close()
		return self.logs