#-*- coding: utf-8 -*-
'''
Created on 2016. 11. 19
Updated on 2016. 01. 09
'''
from __future__ import print_function

import os

from results.Items import ResultItem, BugSummaryItem, ProjectSummaryItem


class Evaluator():
	'''
	Evaluate the result of IRBL Techniques
	input : program, project, versions
	output : program-project sheet data, summary_bugs, summary_project
	'''
	__name__ = u'Evaluator'
	program = u''
	project = u''
	projectSummary = None

	rawData = None
	bugSummaries = None

	def __init__(self, _program, _project):
		self.program = _program
		self.project = _project
		self.projectSummary = ProjectSummaryItem(_project)
		pass

	def cmp(self, _x, _y):
		if _x.rank < _y.rank:
			return -1
		elif _x.rank > _y.rank:
			return 1
		return 0

	def evaluate(self, _ansCnts, _bugcnt):
		'''
		evaluate merged data
		:param _files:
		:param _bugs:
		:return:
		'''
		data_keys = self.rawData.keys()
		data_keys.sort()
		for bugID in data_keys:
			self.rawData[bugID].sort(self.cmp)
			for order in range(len(self.rawData[bugID])):			# for each bug id's results
				this = self.rawData[bugID][order]
				# calculation
				this.top1 = 1 if this.rank < 1 else 0
				this.top5 = 1 if this.rank < 5 else 0
				this.top10 = 1 if this.rank < 10 else 0
				this.AnsOrder = order
				this.AP = float(this.AnsOrder+1) / (this.rank+1)
				this.TP = (1.0/(this.rank+1)) if this.AnsOrder == 0 else 0

		# make summary
		self.bugSummaries = {}
		for bugID in data_keys:
			item = BugSummaryItem(bugID, self.rawData[bugID][0].version)
			for this in self.rawData[bugID]:
				item.top1 += this.top1
				item.top5 += this.top5
				item.top10 += this.top10
				item.AP += this.AP
				item.TP += this.TP

			# item.top1 = 1 if item.top1 >= 1 else 0
			# item.top5 = 1 if item.top5 >= 1 else 0
			# item.top10 = 1 if item.top10 >= 1 else 0
			item.AP = item.AP / _ansCnts[bugID]   #len(self.rawData[bugID])
			self.bugSummaries[bugID] = item

		# evaluate
		for bugID in data_keys:
			item = self.bugSummaries[bugID]
			self.projectSummary.top1 += 1 if item.top1 >= 1 else 0
			self.projectSummary.top5 += 1 if item.top5 >= 1 else 0
			self.projectSummary.top10 += 1 if item.top10 >= 1 else 0
			self.projectSummary.MAP += item.AP
			self.projectSummary.MRR += item.TP

		self.projectSummary.top1P  = (self.projectSummary.top1 / float(_bugcnt)) if _bugcnt > 0 else 0
		self.projectSummary.top5P  = (self.projectSummary.top5 / float(_bugcnt)) if _bugcnt > 0 else 0
		self.projectSummary.top10P = (self.projectSummary.top10 / float(_bugcnt)) if _bugcnt > 0 else 0
		self.projectSummary.MAP    = (self.projectSummary.MAP / float(_bugcnt)) if _bugcnt > 0 else 0
		self.projectSummary.MRR    = (self.projectSummary.MRR / float(_bugcnt)) if _bugcnt > 0 else 0
		pass

	def load(self, _files):
		'''
		load specific project's data set
		:param _files:
		:return: data = {id:[ResultItem(), ...], ...}
		'''
		def line_iterator(_filename):
			data = open(_filename, 'r')
			while True:
				line = data.readline()
				if line is None or len(line) == 0: break
				line = line[:-1]

				columns = line.split('\t')
				if len(columns) != 4: continue

				yield columns
			data.close()

		self.rawData = {}
		for filename in _files:
			if os.path.exists(filename) is False:
				print('There are no file : %s' % filename)
				continue

			version = self.getVersion(filename)
			for columns in line_iterator(filename):
				bid = int(columns[0])
				if bid not in self.rawData:
					self.rawData[bid] = []
				if columns[3]=='NaN':
					columns[3] = '0'
				self.rawData[bid].append(ResultItem(bid, version, columns[1], int(columns[2]), float(columns[3])))

		return self.rawData

	def getVersion(self, _filepath):
		idx1 = _filepath.rfind(u'/')
		idx2 = _filepath.rfind(u'\\')
		if idx1 < idx2:
			idx1 = idx2
		filename = _filepath[idx1+1:]
		idx1 = filename.find(u'_')
		idx2 = filename.find(u'_', idx1 + 1)
		version = filename[idx2+1:]
		return version[:version.rfind(u'_')]

	def output(self, _filepath):
		f = open(_filepath, 'w')
		f.write('Evaluation:\n\t%s' % self.projectSummary)
		f.write('\nSummary:\n')
		for bugID in self.bugSummaries.keys():
			item = self.bugSummaries[bugID]
			f.write('\t%s\n' % item.get_rwa())

		f.write('\n\n----------------------------------\nData:\n')
		for bugID in self.rawData.keys():
			items = self.rawData[bugID]
			for item in items:
				f.write('\t%s\n' % item.get_raw())
		pass

if __name__ == "__main__":
	pass