#-*- coding: utf-8 -*-
'''
Created on 2016. 11. 28
Updated on 2016. 11. 28
'''
from __future__ import print_function
import re
import os
from git import Repo
from Queue import Queue
from utils import Progress


class GitVersion:
	__name__ = 'GitVersion'
	GitLogPath = u''
	workingPath = u''
	tags = None
	tagtimes = None
	git = None
	regxVersion = None

	tagmap = {}
	childmap = {}
	ancestors = set([])

	def __init__(self, _projectName, _gitPath, _storePath):
		self.__name__ = _projectName
		self.GitRepoPath = _gitPath
		self.storePath = _storePath

		# initialize related finding version
		self.regxVersion = re.compile(r'([0-9]+[_\-\.]*)+')
		self.git = Repo(self.GitRepoPath)
		self.tagmap = {}
		self.tags = {}
		self.tagtimes = {}

		# making tag info
		self.tagtimes[None] = 0
		for tagref in self.git.tags:
			if tagref.name.startswith(u'%') is True: continue
			short_hash = str(tagref.commit)[:7]
			self.tags[short_hash] = tagref.name
			self.tagtimes[tagref.name] =  tagref.commit.committed_date
		pass

	def make_childmap(self):
		visited = set([])

		q = Queue()
		q.put((self.git.head.commit, None))		# (commit, child_hash)

		progress = Progress(u'[%s] making git childmap' % self.__name__, 500, 10000, False)
		progress.set_point(0)
		progress.start()
		while q.empty() is False:
			progress.check()
			commit, child_hash = q.get()
			commit_hash = str(commit)[:7]

			# create child map
			if commit_hash not in self.childmap:
				self.childmap[commit_hash] = set([])
			if child_hash is not None:
				self.childmap[commit_hash].add(child_hash)

			if commit_hash in visited: continue
			visited.add(commit_hash)

			# pass itself to parent
			for parent in commit.parents:
				q.put((parent, commit_hash))

			# add ancestors if this commit has no parents
			if len(commit.parents)==0:
				self.ancestors.add(commit_hash)
		progress.done()

		pass

	def make_tagmap(self, ):
		q = Queue()
		visited = set([])

		# root node find (queue init)
		for item in list(self.ancestors):
			q.put((item, None))  # (commit_hash, tagname)

		# For each item in queue
		progress = Progress(u'[%s] making git tagmaps' % self.__name__, 500, 10000, False)
		progress.set_point(0)
		progress.start()
		while q.empty() is False:
			commit_hash, parent_tag = q.get()

			# If this commit in tags, map with commit_hash and tag
			if commit_hash in self.tags:
				commit_tag = self.tags[commit_hash]
				self.tagmap[commit_hash] = commit_tag

			# if this commit not in tags, map with child commit_hash and tag
			else:
				if commit_hash not in self.tagmap:
					self.tagmap[commit_hash] = parent_tag
				else:
					# compare time previous_tag and parent_tag
					previous_tag = self.tagmap[commit_hash]
					pre_time = self.tagtimes[previous_tag]
					par_time = self.tagtimes[parent_tag]
					if par_time > pre_time:
						self.tagmap[commit_hash] = parent_tag
				commit_tag = parent_tag

			if commit_hash not in visited:
				visited.add(commit_hash)
				for child_hash in self.childmap[commit_hash]:
					q.put((child_hash, commit_tag))

			progress.check()
		progress.done()
		pass

	def get_tagname(self, _hash):
		if _hash in self.tagmap:
			tag = self.tagmap[_hash]
			return self.get_versionname(tag)
		return None

	def get_versionname(self, _tag):
		'''
		make version name from tag name
		:param _tag:
		:return:
		'''
		result = self.regxVersion.search(_tag)
		name = result.group(0)
		name = re.sub(r'[_\-\.]$', '', name)
		name = re.sub(r'[_\-]', '.', name)
		return name

	def save_cache(self):
		writer = open(self.storePath, 'w')
		writer.write('{\n')
		hashcnt = 0
		for hash in self.tagmap:
			hashcnt += 1
			writer.write('\t"%s":%s' % (hash, ('"%s"'%self.tagmap[hash]) if self.tagmap[hash] is not None else 'None'))
			if hashcnt != len(self.tagmap):
				writer.write(',\n')
		writer.write('\n}')
		writer.close()
		pass

	def load_cache(self):
		if os.path.exists(self.storePath) is False:
			return None

		print(u'[%s] loading git versions cache....' % self.__name__, end=u'')
		loader = open(self.storePath, 'r')
		text = loader.read()
		loader.close()

		if text.strip() == u'': return None
		self.tagmap = eval(text)
		print(u'Done.')
		return self.tagmap

	def find_tagname(self, _hash):
		'''
`       Find Tag name (not used)
		:param _hash:
		:return:
		'''
		tag = None
		hashset = set([])

		q = Queue()
		q.put(_hash)

		while q.empty() is False:
			hash = q.get()

			if hash in self.tags:
				tag = self.tags[hash]
				break

			commit = self.git.commit(hash)
			for parent in commit.parents:
				parent_hash = str(parent)[:7]
				if parent_hash in hashset: continue
				hashset.add(parent_hash)
				q.put(parent_hash)
			# print(q.queue)

		return tag, len(hashset)

	def load(self):
		map = self.load_cache()     # changed tagmap in this function
		if map is None:
			self.make_childmap()
			self.make_tagmap()      # changed tagmap in this function
			self.save_cache()

		return self.tagmap
