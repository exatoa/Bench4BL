# -*- coding: utf-8 -*-
from __future__ import print_function

import shutil
import os, stat
import subprocess
import time
from commons import VersionUtil


class GitInflator():
	workDir = u''       #u'/home/user/bug/Apache/CAMEL/'
	projectName = u''   #u'CAMEL'
	sourcesPath = u''    #u'/home/user/bug/Apache/CAMEL/sources/'
	gitURL = u''        #u'https://github.com/apache/camel.git'
	gitRepo = u'gitrepo'
	sourcesRepo = u'sources'
	projectPath = u''

	def __init__(self, _project, _giturl, _basePATH):
		self.projectName = _project
		self.gitURL = _giturl
		self.workDir = _basePATH
		self.sourcesPath = os.path.join(_basePATH, self.sourcesRepo)
		pass

	def inflate(self, _versions):
		if _versions is None:
			return None

		self.projectPath = self.clone(True)
		print(u'Git Repo: %s'%self.projectPath)
		time.sleep(2)

		#check output path
		if os.path.exists(self.sourcesPath) is False:
			os.makedirs(self.sourcesPath)

		#get tags
		# tags = self.get_tags()
		# if tags is None: return False

		#print(self.projectName + u':: the number of tags are ' + str(len(tags)))
		size = len(_versions)
		count = 0
		for version, tag in _versions.items():
			vname = VersionUtil.get_versionName(version, self.projectName)
			count += 1
			print (u'%s(%d/%d) :: [%s]'%(self.projectName, count, size, vname), end=u'')

			dest = os.path.join(self.sourcesPath, vname)
			if os.path.exists(dest) is True:
				print(u'  already exists!')
				continue

			tag = tag.strip()
			if tag == u'':
				print (u'invalidate tag name: "%s"'%tag)
				continue

			print(u' checkout %s... ' %tag, end=u'')
			if self.checkout(tag) is False:
				print(u'Failed')
				continue
			time.sleep(2)

			#copy
			dest = os.path.join(self.sourcesPath, vname)
			print(u'  copy...', end=u'')
			if self.makecopy(dest) is False:
				print (u'Failed!')
				continue
			print(u'Done')
			time.sleep(2)
		print(u'All checkout works done!!')
		pass

	#Delect alternative function when an error occured
	def del_rw(self, action, name, exc):
		os.chmod(name, stat.S_IWRITE)
		os.remove(name)

	def clone(self, passExists):
		basepath = os.path.join(self.workDir, u'gitrepo')
		if os.path.exists(basepath) is True:
			if passExists is True:
				return basepath
			shutil.rmtree(basepath, onerror=self.del_rw)

		try:
			subprocess.check_output(['git', 'clone', self.gitURL, u'gitrepo'],  cwd=self.workDir)
		except Exception as e:
			print(e)
			return None
		return basepath

	def get_tags(self):
		result = subprocess.check_output(['git', 'tag'], cwd=self.projectPath)
		if result is None:
			return None
		tags = result.split('\n')
		return tags

	def checkout(self, _tag):
		flag = False

		# checkout tag
		while True:
			try:
				result = subprocess.check_output(['git', 'checkout', _tag], stderr=subprocess.STDOUT, cwd=self.projectPath)
			except Exception as e:
				print(e)
				self.clone(False)
				continue
			break

		# checkout validation
		result = subprocess.check_output(['git', 'branch'], cwd=self.projectPath)
		first = result.split('\n')[0]
		if len(first) > 19:
			version = first[19:-1]
			if version.strip() == _tag:
				flag = True
		return flag

	def makecopy(self, _dest):
		# create target directory
		if not (_dest[-1:]==u'\\' or _dest[-1:]==u'/'):
			_dest += os.path.sep

		# remove previous info
		if os.path.exists(_dest):
			shutil.rmtree(_dest, onerror=self.del_rw)
		os.makedirs(_dest)

		#copy
		for filename in os.listdir(self.projectPath):
			if filename == '.git': continue
			fname = os.path.join(self.projectPath, filename)
			if os.path.isdir(fname) is True:
				shutil.copytree(fname, os.path.join(_dest,filename), symlinks=False, ignore=None)
			else:
				shutil.copy(fname, os.path.join(_dest,filename))
		return True


def getargs():
	import argparse
	parser = argparse.ArgumentParser(description='')
	parser.add_argument('-p', dest='project', help='project name')
	parser.add_argument('-g', dest='giturl', help='github url')
	parser.add_argument('-w', dest='working', default=None, help='working path')
	parser.add_argument('-o', dest='output', default=None, help='output path')

	args = parser.parse_args()

	if args.project is None or args.giturl is None:
		parser.print_help()
		return None
	return args


if __name__ == "__main__":
	args = getargs()
	if args is None:
		exit(1)

	git = GitInflator(args.project, args.giturl, args.working)
	git.inflate([])

