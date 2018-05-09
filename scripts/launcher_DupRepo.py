#-*- coding: utf-8 -*-
from __future__ import print_function
import os
import shutil
from commons import Subjects
from repository import DupMergeRepositoryMaker

#clean
def clean():
	S = Subjects()
	for group in S.groups:
		for project in S.projects[group]:
			print(u'cleanning %s / %s ' % (group, project))
			dirpath = os.path.join(S.getPath_bugrepo(group, project), u'repository_merge')
			fullrepo = os.path.join(S.getPath_bugrepo(group, project), u'repository_merge.xml')
			try:
				shutil.rmtree(dirpath)
			except Exception as e:
				print(u'Failed to remove repository folder')
			try:
				os.remove(fullrepo)
			except Exception as e:
				print(u'Failed to remove full repository file')
	pass

def work():
	obj = DupMergeRepositoryMaker()
	S = Subjects()
	for group in S.groups:
		for project in S.projects[group]:
			obj.make(group, project)

#clean()
work()