#-*- coding: utf-8 -*-
from __future__ import print_function

import os
from xml.etree import ElementTree

from commons import Subjects
from commons import VersionUtil
from utils import PrettyStringBuilder
from utils import Progress


class Counting(object):
	'''
	Make bugs.txt, answers.txt, sources.txt each project
	The files will be stored in each project folder.
	 - bugs.txt : bug report count by each version
	 - answers.txt : This file has the number of answer files each bug report
	 - sources.txt : This file has the number of source files each version
	'''

	def __init__(self):
		self.S = Subjects()
		pass

	def getBugs(self, _repo):
		'''
		:param _repo:
		:return:
		'''
		bugs = []
		try:
			e = ElementTree.parse(_repo).getroot()

			bugtags = e.findall('bug')
			for tag in bugtags:
				bugs.append(int(tag.attrib['id']))

		except Exception as e:
			return None
		return bugs

	def getAnswers(self, _repo):
		'''
		get answer files from bug repository files
		:param _repo: repository file path
		:return:
		'''
		bugAnswers = {}
		try:
			e = ElementTree.parse(_repo).getroot()

			bugtags = e.findall('bug')
			for tag in bugtags:
				id = int(tag.attrib['id'])
				files = tag.findall('fixedFiles')[0]
				bugAnswers[id] = len(files.findall('file'))
		except Exception as e:
			return None
		return bugAnswers

	def getCodeCount(self, _path):
		if os.path.exists(_path) is False: return 0

		count = 0
		for root, dirs, files in os.walk(_path):
			for fileitem in files:
				if fileitem.endswith('.java') is False: continue
				count += 1
		return count

	def bug_counting(self, _group, _project):
		statistics = {}
		repo = os.path.join(self.S.getPath_bugrepo(_group, _project), u'repository.xml')
		statistics['all'] = self.getBugs(repo)

		for version in self.S.versions[_project].keys():
			vname = VersionUtil.get_versionName(version, _project)
			repo = os.path.join(self.S.getPath_bugrepo(_group, _project), u'repository', u'%s.xml'%vname)
			result = self.getBugs(repo)
			if result is None: continue
			statistics[vname] = result

		# Check missed items
		idset = set(statistics['all'])
		for key in statistics:
			if key == 'all': continue
			idset -= set(statistics[key])
		statistics['miss'] = list(idset)

		pretty = PrettyStringBuilder(_indent_depth=2)
		text = pretty.get_dicttext({_project: statistics})

		f = open(os.path.join(self.S.getPath_base(_group, _project),  u'bugs.txt'), 'w')
		f.write(text)
		f.close()
		pass

	def answers_counting(self, _group, _project):
		statistics = {}
		repo = os.path.join(self.S.getPath_bugrepo(_group, _project), u'repository.xml')
		statistics['all'] = self.getAnswers(repo)

		for version in self.S.versions[_project].keys():
			vname = VersionUtil.get_versionName(version, _project)
			repo = os.path.join(self.S.getPath_bugrepo(_group, _project), u'repository', u'%s.xml'%vname)
			result = self.getAnswers(repo)
			if result is None: continue
			statistics[vname] = result

		pretty = PrettyStringBuilder(_indent_depth=2)
		text = pretty.get_dicttext({_project: statistics})

		f = open(os.path.join(self.S.getPath_base(_group, _project), u'answers.txt'), 'w')
		f.write(text)
		f.close()
		pass

	def source_counting(self, _group, _project):
		statistics = {}

		progress = Progress('source counting', 2,10, True)
		progress.set_upperbound(len(self.S.versions[_project].keys()))
		progress.start()
		for version in self.S.versions[_project].keys():
			vname = VersionUtil.get_versionName(version, _project)
			repo = os.path.join(self.S.getPath_source(_group, _project, vname),)
			result = self.getCodeCount(repo)
			if result is None: continue
			statistics[vname] = result
			progress.check()
		progress.done()

		maxValue = 0
		for vname in statistics:
			if maxValue < statistics[vname]:
				maxValue = statistics[vname]
		statistics['max'] = maxValue

		pretty = PrettyStringBuilder(_indent_depth=2)
		text = pretty.get_dicttext({_project: statistics})

		f = open(os.path.join(self.S.getPath_base(_group, _project), u'sources.txt'), 'w')
		f.write(text)
		f.close()

	def run(self):
		for group in self.S.groups:
			for project in self.S.projects[group]:
				print(u'Counting for %s / %s' % (group, project))
				self.bug_counting(group, project)
				self.answers_counting(group, project)
				#self.source_counting(group, project)


###############################################################################################################
###############################################################################################################
if __name__ == "__main__":
	obj = Counting()
	obj.run()
	# r = obj.getCodeCount(u'/var/experiments/BugLocalization/dist/data/')
	# print(u'count::%d' % r)

	pass