#-*- coding: utf-8 -*-
'''
Created on 2017. 02. 16
Updated on 2017. 02. 16
'''
from __future__ import print_function
import os
from Subjects import Subjects


class Previous(Subjects):
	'''
	Collecting Subjects information
	Sourcecode, Bug reports, Duplicate bug reports
	'''
	__name__ = u'Previous'
	groups = ['Previous']
	projects = {
		'Previous': [u'AspectJ', u'ZXing', u'PDE', u'JDT', u'SWT']
	}

	def __init__(self):
		super(Previous, self).__init__()
		pass

	def get_max_versions(self, _technique, _project):
		versions = {
			'AspectJ': u'1.6.0.M2',  # BLIA = 1.5.3.final
			'JDT': u'4.5',
			'PDE': u'4.4',
			'SWT': u'3.138',  # BLIA = 3.659
			'ZXing': u'1.6',
		}
		if _technique == "BLIA":
			if _project == "AspectJ": return u'1.5.3.final'
			if _project == "SWT": return u'3.659'
		#if _technique == "Locus":
		#if _project == "SWT": return u'3.138.GIT'

		return versions[_project]