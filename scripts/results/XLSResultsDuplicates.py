#-*- coding: utf-8 -*-
'''
Created on 2017. 04. 14
Updated on 2017. 04. 14

'''
from __future__ import print_function

from scipy.stats import mannwhitneyu

from commons import Subjects
from results import Evaluator
from results import XLSbasic


class XLSResultsDuplicates(XLSbasic):
	'''
	This class for duplicate reports
	'''
	__name__ = u'XLSResultsDuplicates'


	#######################################################################
	# Data Summary Part Process
	#######################################################################
	def create_OverallSheet(self, _startCol):
		sheet = self.workbook.add_worksheet(u'Overall')
		self.input_colspan(sheet, 0, 0, 3, [u'Master'], default_style=self.title_format)
		self.input_colspan(sheet, 0, 4, 3, [u'Duplicate'], default_style=self.title_format)
		texts = [u'Technique', u'MAP', u'MRR', u'', u'Technique', u'MAP', u'MRR']
		styles = [self.title_format]*3  + [self.blank_format] + [self.title_format]*3
		self.set_cols(sheet, col=_startCol, widths=[17, 15, 15, 10, 17, 15, 15])
		self.input_row(sheet, row=1, col=_startCol, values=texts, styles=styles)

		self.input_colspan(sheet, 10, 0, 3, [u'Master-Duplicate MWUTest'], default_style=self.title_format)
		texts = [u'Technique', u'MAP', u'MRR']
		styles = [self.title_format] * 3 + [self.blank_format] + [self.title_format] * 3
		self.input_row(sheet, row=11, col=_startCol, values=texts, styles=styles)
		return sheet

	def fill_OverallSheet(self, _sheet):
		styles = [self.subtitle_format] + [self.percent_format] *2 + [self.blank_format, self.subtitle_format] + [self.percent_format] *2
		row = 2

		masterCNT = 0
		dupCNT = 0
		for group in self.S.groups:
			for project in self.S.projects[group]:
				masterBugs, dupBugs = self.make_IDsets(self.S.duplicates[project])
				masterCNT += len(masterBugs)
				dupCNT += len(dupBugs)

		for tech in self.S.techniques:
			# masterCNT = float(len(self.OverallResult[tech]['master']['AP']))
			# dupCNT = float(len(self.OverallResult[tech]['duplicate']['AP']))

			values = [tech, sum(self.OverallResult[tech]['master']['AP'])/ float(masterCNT), sum(self.OverallResult[tech]['master']['TP'])/float(masterCNT), u'',
					  tech, sum(self.OverallResult[tech]['duplicate']['AP'])/float(dupCNT), sum(self.OverallResult[tech]['duplicate']['TP'])/float(dupCNT)]

			self.input_row(_sheet, row, 0, values, styles)
			row += 1

		row += 4
		styles = [self.subtitle_format] + [self.float_format] * 2
		for tech in self.S.techniques:
			s, APpvalue = mannwhitneyu(self.OverallResult[tech]['master']['AP'],
											 self.OverallResult[tech]['duplicate']['AP'],
											 use_continuity=True, alternative='two-sided')  # 'less', 'two-sided', 'greater'

			s, TPpvalue = mannwhitneyu(self.OverallResult[tech]['master']['TP'],
									   self.OverallResult[tech]['duplicate']['TP'],
									   use_continuity=True, alternative='two-sided')  # 'less', 'two-sided', 'greater'

			values = [tech, APpvalue, TPpvalue]
			self.input_row(_sheet, row, 0, values, styles)
			row += 1
		pass

	#######################################################################
	# Data Summary Part Process
	#######################################################################
	def create_SummarySheet(self, _shtName, _startCol):
		sheet = self.workbook.add_worksheet(_shtName)
		texts = [u'DupType', u'Technique',  u'Group', u'Project', u'Source Files', u'BugCount', u'Recommended BugCount', u'Top1 Count', u'Top5 Count', u'Top10 Count', u'Top1', u'Top5', u'Top10', u'MAP', u'MRR']
		self.set_cols(sheet, col=_startCol, widths=[10, 17, 17, 17, 14, 7, 7, 7, 7, 7, 7, 7, 7])
		self.input_row(sheet, row=0, col=_startCol, values=texts, default_style=self.title_format)

		sheet.freeze_panes(1, 0)  # Freeze the second row.
		row = 1
		return sheet, row

	def fill_SummarySheet(self, _sheet, _startRow, _program, _group, _project, _type, _item, _srcCnt, _bugCnt, _bugCntR):
		styles = [self.subtitle_format] * 4 + [self.number_format] * 6 + [self.percent_format] * 5
		values = [_type, _program, _group, _project, _srcCnt, _bugCnt, _bugCntR,
		          _item.top1, _item.top5, _item.top10,
		          _item.top1P, _item.top5P, _item.top10P,
		          _item.MAP, _item.MRR]
		self.input_row(_sheet, _startRow, 0, values, styles)
		#self.summary_row += 1
		_startRow += 1
		return _startRow

	#######################################################################
	# Raw Data Part Process
	#######################################################################
	def create_DataSheet(self, _shtName, _startCol):
		sheet = self.workbook.add_worksheet(_shtName)

		self.set_cols(sheet, 0, widths=[5, 12, 12, 12, 12, 8, 12, 12, 25, 6, 12, 12, 12, 7, 7, 7, 10, 8, 8, 8])

		texts = [u'key',u'DupType', u'Technique', u'Group', u'Project',  u'BugID', u'Version', u'AnsFileCount', u'File', u'Rank', u'Score',u'normalRank', u'normalScore', u'Top1', u'Top5', u'Top10', u'AnsOrder', u'P(rank)', u'AP', u'TP']
		self.input_row(sheet, row=0, col=_startCol, values=texts, default_style=self.title_format)

		sheet.freeze_panes(1, 0)  # Freeze the second row.
		_row = 1
		return sheet, _row

	def fill_DataSheet(self, _sheet, _row, _program, _group, _project, _type, _bugData, _rawData, _srcCounts, _ansCounts):
		#Write data and make basic statistics
		styles = [self.id_format]*7 + [self.number_format, self.base_format] + [self.number_format] + [self.float_format]*3 + [self.number_format]*4 + [self.float_format]*3

		data_keys = _rawData.keys()
		data_keys.sort()
		maxScore = 0.0
		for bugID in data_keys:
			for this in _rawData[bugID]:
				if maxScore < this.score: maxScore = this.score

		for bugID in data_keys:
			AP = _bugData[bugID].AP
			count = _ansCounts[bugID]

			for this in _rawData[bugID]:			# for each bug id's results
				vname = this.version
				if vname == 'all':
					vname = 'max'
				normRank = (this.rank+1) / float(_srcCounts[vname])
				normScore = (this.score / maxScore) if _program in ['AmaLgam', 'BLUiR', 'BLIA'] else this.score
				key = u'%s%d'%(_project.lower(),this.id)
				values = [key,
						  _type, _program, _group, _project, this.id, this.version, count, this.filename, this.rank, this.score, normRank, normScore,
				          this.top1, this.top5, this.top10, this.AnsOrder, this.AP, AP, this.TP]

				self.input_row(_sheet, _row, 0, values, styles)
				_row += 1
		return _row

	#######################################################################
	# Raw Data Part Process
	#######################################################################
	def create_bugDataSheet(self, _shtName, _startCol):
		sheet = self.workbook.add_worksheet(_shtName)

		self.set_cols(sheet, 0, widths=[5, 12, 12, 12, 12, 8, 12, 12, 6, 6, 6, 7, 7, 8, 8, 8])

		texts = [u'key', u'DupType', u'Technique', u'Group', u'Project',  u'BugID', u'Version', u'AnsFileCount', u'Top1', u'Top5', u'Top10', u'AP', u'TP',
		         u'Pin(1)', u'Pin(5)', u'Pin(10)']
		self.input_row(sheet, row=0, col=_startCol, values=texts, default_style=self.title_format)

		sheet.freeze_panes(1, 0)  # Freeze the second row.
		row = 1
		return sheet, row

	def fill_bugDataSheet(self, _sheet, _row, _program, _group, _project, _type, _bugData, _ansCounts):
		#Write data and make basic statistics
		data_keys = _bugData.keys()
		data_keys.sort()
		styles = [self.id_format]*7 + [self.number_format]*4+ [self.float_format]*5

		for bugID in data_keys:
			this = _bugData[bugID]			# for each bug id's results
			count = _ansCounts[this.id]
			key = u'%s%s%d'%(_program.lower(), _project.lower(),this.id)
			values = [key, _type, _program, _group, _project, this.id, this.version, count, this.top1, this.top5, this.top10, this.AP, this.TP]

			values.append(this.top1 / float(count if count <= 1 else 1))
			values.append(this.top5 / float(count if count <= 5 else 5))
			values.append(this.top10 / float(count if count <= 10 else 10))

			self.input_row(_sheet, _row, 0, values, styles)

			_row += 1
		return _row

	#######################################################################
	# Data Subject Part Process
	#######################################################################
	def load_results(self, _group, _project, _tech, _isUnion=False):
		resultFiles = []
		if _isUnion is False:
			for version in self.S.bugs[_project].keys():
				if version == 'all': continue
				versionName = u'%s' % version
				resultFiles.append(self.S.getPath_results(self.TYPE, _tech, _group, _project, versionName))
		else:
			resultFiles.append(self.S.getPath_results(self.TYPE, _tech, _group, _project, 'all'))

		evLoader = Evaluator(_tech, _project)
		rawData = evLoader.load(resultFiles)

		#filter non-duplicate reports
		keys = rawData.keys()
		for key in keys:
			if key in self.S.duplicate_sets[_project]: continue
			del rawData[key]

		# partition master and duplicate group
		masterData = {}
		duplicateData = {}
		for srcID, destID in self.S.duplicates[_project]:
			if srcID not in rawData or destID not in rawData: continue

			if (srcID in rawData) and (srcID not in masterData):
				masterData[srcID] = rawData[srcID]
			if (destID in rawData) and (destID not in duplicateData):
				duplicateData[destID] = rawData[destID]

		masterBugs, dupBugs = self.make_IDsets(self.S.duplicates[_project])

		evMaster = Evaluator(_tech, _project)
		evMaster.rawData = masterData
		evMaster.evaluate(self.S.answers[_project]['all'], len(masterBugs))

		evDuplicate = Evaluator(_tech, _project)
		evDuplicate.rawData = duplicateData
		evDuplicate.evaluate(self.S.answers[_project]['all'], len(dupBugs))

		return evMaster, evDuplicate

	def make_IDsets(self, _duplicates):
		masterBugs = set([])
		dupBugs = set([])
		for src, dest in _duplicates:
			masterBugs.add(src)
			dupBugs.add(dest)
		return masterBugs, dupBugs

	def run(self, _type, _isUnion=False):
		'''
		create result file
		'''
		self.TYPE = _type
		print(_type)

		self.S = Subjects()
		self.OverallResult = {}
		for tech in self.S.techniques:
			self.OverallResult[tech] = {'master': {'AP': [], 'TP': []}, 'duplicate': {'AP': [], 'TP': []}}

		# XLS preparing
		shtOverall = self.create_OverallSheet(0)
		shtMasterSummary, rowMasterSummary = self.create_SummarySheet(u'Summary (Master)', 0)
		shtMasterBugData, rowMasterBugData = self.create_bugDataSheet(u'bugData (Master)', 0)
		shtMasterData, rowMasterData = self.create_DataSheet(u'rawData (Master)', 0)

		shtDupSummary, rowDupSummary = self.create_SummarySheet(u'Summary (Dup)', 0)
		shtDupBugData, rowDupBugData = self.create_bugDataSheet(u'bugData (Dup)', 0)
		shtDupData, rowDupData = self.create_DataSheet(u'rawData (Dup)', 0)

		for group in self.S.groups:
			for project in self.S.projects[group]:
				print(u'working %s / %s ...' % (group, project), end=u'')

				for tech in self.S.techniques:
					print(tech + u' ', end=u'')
					evMaster, evDuplicate = self.load_results(group, project, tech, _isUnion)

					masterBugs, dupBugs = self.make_IDsets(self.S.duplicates[project])
					rowMasterSummary = self.fill_SummarySheet(shtMasterSummary, rowMasterSummary, tech, group, project, 'master', evMaster.projectSummary, self.S.sources[project]['max'], len(masterBugs), len(evMaster.bugSummaries))
					rowMasterBugData = self.fill_bugDataSheet(shtMasterBugData, rowMasterBugData, tech, group, project, 'master', evMaster.bugSummaries, self.S.answers[project]['all'])
					rowMasterData = self.fill_DataSheet(shtMasterData, rowMasterData, tech, group, project, 'master', evMaster.bugSummaries, evMaster.rawData, self.S.sources[project], self.S.answers[project]['all'])

					rowDupSummary= self.fill_SummarySheet(shtDupSummary, rowDupSummary, tech, group, project, 'duplicate', evDuplicate.projectSummary, self.S.sources[project]['max'], len(dupBugs), len(evDuplicate.bugSummaries))
					rowDupBugData = self.fill_bugDataSheet(shtDupBugData, rowDupBugData, tech, group, project, 'duplicate', evDuplicate.bugSummaries, self.S.answers[project]['all'])
					rowDupData = self.fill_DataSheet(shtDupData, rowDupData, tech, group, project, 'duplicate', evDuplicate.bugSummaries, evDuplicate.rawData, self.S.sources[project], self.S.answers[project]['all'])

					for bugID in evMaster.bugSummaries:
						self.OverallResult[tech]['master']['AP'].append(evMaster.bugSummaries[bugID].AP)
						self.OverallResult[tech]['master']['TP'].append(evMaster.bugSummaries[bugID].TP)

					for bugID in evDuplicate.bugSummaries:
						self.OverallResult[tech]['duplicate']['AP'].append(evDuplicate.bugSummaries[bugID].AP)
						self.OverallResult[tech]['duplicate']['TP'].append(evDuplicate.bugSummaries[bugID].TP)

				print(u' Done')

		self.fill_OverallSheet(shtOverall)
		self.finalize()
		pass


###############################################################################################################
###############################################################################################################
###############################################################################################################
if __name__ == "__main__":
	name = u'NewData_AWS'
	obj = XLSResultsDuplicates(u'/var/experiments/BugLocalization/dist/expresults/Result_Duplicates_%s.xlsx' % name)
	obj.run(name, _isUnion=False)

	# name = u'NewDataSingle'
	# obj = XLSResultsDuplicates(u'/var/experiments/BugLocalization/dist/expresults/Result_Duplicates_%s.xlsx' % name)
	# obj.run(name, _isUnion=True)
	pass


