#-*- coding: utf-8 -*-
'''
Created on 2016. 11. 19
Updated on 2016. 01. 09
'''
from __future__ import print_function


class ResultItem():
	id = 0
	version = u''
	filename = u''
	rank = 0
	score = 0.0
	top1 = 0
	top5 = 0
	top10 = 0
	AnsOrder = 0
	AP = 0.0
	TP = 0.0

	def __init__(self, _id, _version, _filename, _rank, _score,
	             _top1=0, _top5=0, _top10=0, _AnsOrder=0, _AP=0.0, _TP=0.0):
		self.id = _id
		self.version = _version
		self.filename = _filename
		self.rank = _rank
		self.score = _score
		self.top1       = _top1
		self.top5       = _top5
		self.top10      = _top10
		self.AnsOrder   = _AnsOrder
		self.AP         = _AP
		self.TP         = _TP

		pass

	def __repr__(self):
		return 'ID(%d) <%s,  %d (%.4f)\t%s \t Top1(%d), Top5(%d), Top10(%d), Order(%d), PR(%.2f), IR(%.2f)' % (
				self.id, self.version, self.rank, self.score, self.filename,
				self.top1, self.top5, self.top10, self.AnsOrder, self.AP, self.TP)
	
	def get_raw(self):
		return '%d\t%s\t%s\t%d\t%.4f\t%d\t%d\t%d\t%d\t%.2f\t%.2f' % (
				self.id, self.version, self.filename, self.rank, self.score,
				self.top1, self.top5, self.top10, self.AnsOrder, self.AP, self.TP)


class BugSummaryItem():
	id = 0
	version = u''
	top1 = 0
	top5 = 0
	top10 = 0
	AP = 0.0
	TP = 0.0

	def __init__(self, _id=None, _version=u'', _top1=0, _top5=0, _top10=0, _AP=0.0, _TP=0.0):
		self.id = _id
		self.version = _version
		self.top1   = _top1
		self.top5   = _top5
		self.top10  = _top10
		self.AP     = _AP
		self.TP     = _TP
		pass

	def __repr__(self):
		return u'ID(%d) <%s, Top1 :%d , Top5: %d, Top10: %d, PR: %.4f,  IR: %.4f>' % (
				self.id, self.version, self.top1, self.top5, self.top10, self.AP, self.TP)

	def get_raw(self):
		return '%d\t%s\t%d\t%d\t%d\t%.2f\t%.2f' % (
				self.id, self.version, self.top1, self.top5, self.top10, self.AP, self.TP)


class ProjectSummaryItem():
	project = u''
	top1 = 0
	top5 = 0
	top10 = 0
	top1P = 0
	top5P = 0
	top10P = 0
	MAP = 0.0
	MRR = 0.0

	def __init__(self, _project=u'', _top1=0, _top5=0, _top10=0,
	             _top1P = 0.0, _top5P = 0.0, _top10P=0.0,_MAP=0.0, _MRR=0.0):
		self.project = _project
		self.top1   = _top1
		self.top5   = _top5
		self.top10  = _top10
		self.top1P = _top1P
		self.top5P = _top5P
		self.top10P = _top10P
		self.MAP     = _MAP
		self.MRR     = _MRR
		pass

	def __repr__(self):
		return u'Top1: %d (%.2f%%), Top5: %d (%.2f%%), Top10: %d (%.2f%%), MAP: %.2f%%,  MRR: %.2f%%' % (
				self.top1, self.top1P, self.top5, self.top5P, self.top10, self.top10P, self.MAP, self.MRR)
