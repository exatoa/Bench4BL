#-*- coding: utf-8 -*-
'''
Created on 2016. 11. 19
Updated on 2016. 01. 09
'''
from __future__ import print_function
import os
import sys
sys.path.append(os.getcwd())    # add the executed path to system PATH

import codecs
from commons import VersionUtil
from GitLog import GitLog
from BugFilter import BugFilter
from GitVersion import GitVersion
import shutil


class BugRepositoryMaker:
	'''
	'''
	__name__ = u'BugRepositoryMaker'
	ProjectName = u''
	duplicatePath = u''
	repositoryPath = u''
	git = None
	gitVersion = None
	bugFilter = None

	def __init__(self, _projectName, _srcbugPath, _gitPath, _output):
		self.__name__ = _projectName
		if os.path.exists(_output) is False:
			os.makedirs(_output)

		self.ProjectName = _projectName
		self.duplicatePath = os.path.join(_output, u'duplicates.json')
		self.repositoryPath = os.path.join(_output, u'repository')
		self.git = GitLog( _projectName, _gitPath, os.path.join(_output, u'.git.log'))
		self.gitVersion = GitVersion(_projectName, _gitPath, os.path.join(_output, u'.git_version.txt'))
		self.bugFilter = BugFilter(_projectName, os.path.join(_srcbugPath, u'bugs'))
		pass

	#######################################################################
	# Conert XML
	#######################################################################
	def convertText(self, _bug):
		'''
		Convert bug object to XML
		:param _bug:
		:return:
		'''
		format =  u'\t<bug id="%s" opendate="%s" fixdate="%s" resolution="%s">\n'
		format += u'\t\t<buginformation>\n'
		format += u'\t\t\t<summary>%s</summary>\n'
		format += u'\t\t\t<description>%s</description>\n'
		format += u'\t\t\t<version>%s</version>\n'
		format += u'\t\t\t<fixedVersion>%s</fixedVersion>\n'
		format += u'\t\t\t<type>%s</type>\n'
		format += u'\t\t</buginformation>\n'
		format += u'\t\t<fixedFiles>\n%s\n\t\t</fixedFiles>\n'
		format += u'%s'  #this section for links
		format += u'\t</bug>\n'

		fixedfiles = u'\n'.join(
			u'\t\t\t<file type="'+ f['type'] +u'">' + f['name'] + u'</file>'
			for f in _bug['fixedFiles']
		)

		links = u'\n'.join(
			u'\t\t\t<link type="'+ link['type']
			+ u'" description="'+ link['description'] +u'">'
			+ link['id'][link['id'].rfind('-')+1:] + u'</link>'
			for link in _bug['links']
		)
		if links !=u'':
			links = u'\t\t<links>\n%s\n\t\t</links>\n'%links

		text = format% (_bug['id'][_bug['id'].rfind('-')+1:],
						_bug['opendate'].strftime(u'%Y-%m-%d %H:%M:%S'),
						_bug['fixdate'].strftime(u'%Y-%m-%d %H:%M:%S'),
						_bug['resolution'],
						_bug['summary'],
						_bug['description'],
						_bug['version'],
						_bug['fixVersion'],
						_bug['type'],
						fixedfiles,
		                links)
		return text

	def convertTextSimple(self, _bug):
		'''
		Convert bug object to XML (simply)
		:param _bug:
		:return:
		'''
		format =  u'\t<bug id="%s" opendate="%s" fixdate="%s" resolution="%s">\n'
		format += u'\t\t<buginformation>\n'
		format += u'\t\t\t<summary>%s</summary>\n'
		format += u'\t\t\t<version>%s</version>\n'
		format += u'\t\t\t<fixedVersion>%s</fixedVersion>\n'
		format += u'\t\t\t<type>%s</type>\n'
		format += u'\t\t</buginformation>\n'
		format += u'\t\t<fixedFiles>%s</fixedFiles>\n'
		format += u'%s'  #this section for links
		format += u'\t</bug>\n'

		fixedfiles = unicode(len(_bug['fixedFiles']))

		links = u'\n'.join(
			u'\t\t\t<link type="'+ link['type']
			+ u'" description="'+ link['description'] +u'">'
			+ link['id'][link['id'].rfind('-')+1:] + u'</link>'
			for link in _bug['links']
		)
		if links !=u'':
			links = u'\t\t<links>\n%s\n\t\t</links>\n'%links

		text = format% (_bug['id'][_bug['id'].rfind('-')+1:],
						_bug['opendate'].strftime(u'%Y-%m-%d %H:%M:%S'),
						_bug['fixdate'].strftime(u'%Y-%m-%d %H:%M:%S'),
						_bug['resolution'],
						_bug['summary'],
						_bug['version'],
						_bug['fixVersion'],
						_bug['type'],
						fixedfiles,
		                links)
		return text

	def outputXML(self, _items, _targetPath):
		#write XML File
		output = codecs.open(_targetPath, 'w', 'utf-8')
		output.write(u'<?xml version = "1.0" encoding = "UTF-8" ?>\n<bugrepository name="%s">\n'%self.ProjectName)
		for item in _items:
			output.write(self.convertText(item))
		output.write(u'</bugrepository>')
		output.flush()
		output.close()
		pass

	def printSample(self, _items):

		types = {}
		for item in _items:
			if item['type'] not in types:
				types[item['type']] = 1

		output = codecs.open(self.repositoryPath + u'.type', 'w', 'utf-8')
		for t in types.keys():
			for item in _items:
				if item['type'] != t : continue
				output.write(self.convertText(item))
			output.write(u'\n\n<!-- -------------------------------------------------------------- -->\n\n')
		output.close()

		versions = {}
		for item in _items:
			if item['version'] not in versions:
				versions[item['version']] = 1

		output = codecs.open(self.repositoryPath + u'.version', 'w', 'utf-8')
		for ver in versions.keys():
			for item in _items:
				if item['version'] != ver: continue
				output.write(self.convertTextSimple(item))
			output.write(u'\n\n<!-- -------------------------------------------------------------- -->\n\n')
		output.close()

	def getVersionString(self, _version):
		vname = self.ProjectName + u'_' + _version.replace(u'.', u'_')
		if vname.endswith(u'_') is True:
			vname = vname[:-1]
		return vname

	def getItemsByVersion(self, _items, _versions):
		# write XML File

		_versions.sort(cmp=VersionUtil.cmpVersion)
		version_bugs = dict((ver, list()) for ver in _versions)

		size = len(_versions)
		for idx in range(0, size):
			version = _versions[idx]
			nextVersion = _versions[idx + 1] if idx != size - 1 else u'10000.0'  # assign big. version number

			for bugitem in _items:
				if VersionUtil.cmpVersion(version, bugitem['version']) > 0 and idx!=0: continue
				if VersionUtil.cmpVersion(bugitem['version'], nextVersion) >= 0: continue
				version_bugs[version].append(bugitem)

		return version_bugs

	def outputDuplicates(self, _dupgroups):
		output = codecs.open(self.duplicatePath, 'w', 'utf-8')
		output.write(u'{"%s":[\n' % self.ProjectName.lower())
		groupcnt  = 0
		for group in _dupgroups:
			groupcnt += 1
			srcID = int(group['src'][group['src'].find('-')+1:].strip())
			destID = int(group['dest'][group['dest'].find('-')+1:].strip())
			output.write(u'\t[%d, %d]'%(srcID, destID))
			if groupcnt != len(_dupgroups):
				output.write(u',')
			if group['fixedboth'] is True:
				output.write(u' # fixed both')
			output.write(u'\n')

		output.write(u']}\n')
		output.close()
		return len(_dupgroups)

	def getItemsOnlyVersion(self, _items):
		newItems = []
		for item in _items:
			if VersionUtil.hasVersion(item['version']) is False: continue
			newItems.append(item)
		return newItems

	def filter_dupgroups(self, _dups, _bugs):
		bugIDset = set([item['id'] for item in _bugs])

		newDups = []
		for dup in _dups:
			if dup['src'] not in bugIDset or dup['dest'] not in bugIDset: continue
			newDups.append(dup)

		#change chaning bug::   A-> B -> C   ==>    A->C, B->C
		count = 1
		while count !=0:
			count = 0
			for dup in newDups:
				src = dup['src']
				for subdup in newDups:
					if subdup['dest'] != src: continue
					dup['src'] = subdup['src']
					count += 1
					break
		return newDups

	#######################################################################
	# Overall Process
	#######################################################################
	def run(self, _versions):

		print(u'[%s] start making bug repositories for %s' %(self.__name__, self.ProjectName))
		logs = self.git.load()
		tagmaps = self.gitVersion.load()
		items, dupgroups = self.bugFilter.run(logs, tagmaps)
		print(u'[%s] %d Bug reports has been merged!' % (self.ProjectName, len(items)))

		# refine more
		FilteredItems = self.getItemsOnlyVersion(items)
		versionItems = self.getItemsByVersion(FilteredItems, _versions)
		print(u'[%s] Making bug repository for each version...' % self.ProjectName)

		# revise dup bug reports which are not include removed items.
		# and remove missed bugs from FilteredItems when the version making
		dupgroups = self.filter_dupgroups(dupgroups, FilteredItems)
		print(u'[%s] Filtered bug reports.' % self.ProjectName)

		#self.printSample(items)
		self.outputXML(items, self.repositoryPath + u'_full.xml')
		self.outputXML(FilteredItems, self.repositoryPath + u'.xml')

		if os.path.exists(self.repositoryPath) is True:
			shutil.rmtree(self.repositoryPath)
		if os.path.exists(self.repositoryPath) is False:
			os.makedirs(self.repositoryPath)

		exists = set([])
		for ver in versionItems.keys():
			if len(versionItems[ver])==0: continue
			exists.add(ver)
			outputPath = os.path.join(self.repositoryPath, self.getVersionString(ver) + u'.xml')
			self.outputXML(versionItems[ver], outputPath)
		print(u'Done')

		exists = list(exists)
		exists.sort(cmp=VersionUtil.cmpVersion)
		print(u'[%s] %d version repositories has been created! %s' % (self.ProjectName, len(exists), exists))

		dupcnt = self.outputDuplicates(dupgroups)
		print(u'[%s] %d duplicate bug-set has been created!' % (self.ProjectName, dupcnt))
		pass

###############################################################################################################
###############################################################################################################
###############################################################################################################
def getargs():
	import argparse
	parser = argparse.ArgumentParser(description='')
	parser.add_argument('-p', dest='project', help='project name')
	parser.add_argument('-g', dest='gitPath', help='git cloned path; we will extract log message from this.')
	parser.add_argument('-s', dest='bugPath', default=None, help='the directory contains bug reports')
	parser.add_argument('-v', dest='versions', default=None, help='versions')

	args = parser.parse_args()

	if args.project is None or args.gitPath is None or args.bugPath is None:
		parser.print_help()
		return None
	return args


if __name__ == "__main__":
	args = getargs()
	if args is None:
		# from collections import namedtuple
		# Args = namedtuple('Args', 'project gitPath bugPath versions')
		#args = Args(u'PDE', u'/var/experiments/BugLocalization/dist/data/Previous/PDE/gitrepo/', u'/var/experiments/BugLocalization/dist/data/Previous/PDE/bugrepo/PDE/', ['4.5'])

		exit(0)

	obj = BugRepositoryMaker(args.project, args.bugPath, args.gitPath, args.bugPath)
	obj.run(args.versions)
	pass