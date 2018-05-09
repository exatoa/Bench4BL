#-*- coding: utf-8 -*-
'''
Created on 2017. 04. 14
Updated on 2017. 04. 14
'''
from __future__ import print_function

from commons import Subjects
from Evaluator import Evaluator
from XLSbasic import XLSbasic


class XLSResultAll(XLSbasic):
	__name__ = u'ResultXLSAll'

	#######################################################################
	# Data Summary Part Process
	#######################################################################
	def create_SummarySheet(self, _startCol):
		sheet = self.workbook.add_worksheet(u'Summary')
		texts = [u'Technique', u'Group', u'Project', u'Source Files', u'BugCount', u'Recommended BugCount', u'Top1 Count', u'Top5 Count', u'Top10 Count', u'Top1', u'Top5', u'Top10', u'MAP', u'MRR']
		self.set_cols(sheet, col=_startCol, widths=[17, 17, 17, 10, 14, 7, 7, 7, 7, 7, 7, 7, 7])
		self.input_row(sheet, row=0, col=_startCol, values=texts, default_style=self.title_format)

		sheet.freeze_panes(1, 0)  # Freeze the second row.
		self.summary_row = 1
		return sheet

	def fill_SummarySheet(self, _sheet, _group, _program, _project, _item, _srcCnt, _bugCnt, _bugCntR):
		styles = [self.subtitle_format] * 3 + [self.number_format] * 6 + [self.percent_format] * 5
		values = [_program, _group, _project, _srcCnt, _bugCnt, _bugCntR,
		          _item.top1, _item.top5, _item.top10,
		          _item.top1P, _item.top5P, _item.top10P,
		          _item.MAP, _item.MRR]
		self.input_row(_sheet, self.summary_row, 0, values, styles)
		self.summary_row += 1
		return self.summary_row

	#######################################################################
	# Raw Data Part Process
	#######################################################################
	def create_DataSheet(self, _startCol):
		sheet = self.workbook.add_worksheet(u'rawData')

		self.set_cols(sheet, 0, widths=[5, 12, 12, 12, 8, 12, 12, 25, 6, 12, 12, 12, 7, 7, 7, 10, 8, 8, 8,  9,7,6,6,6,6,19,15,9,9,9,9,9,9])

		texts = [u'key', u'Approach', u'Group', u'Project', u'BugID', u'Version', u'AnsFileCount', u'File', u'Rank', u'Score',u'normalRank', u'normalScore', u'Top1', u'Top5', u'Top10', u'AnsOrder', u'P(rank)', u'AP', u'TP',
		         u'DupType', u'DupID',
		         u'Talks', u'Enums', u'Code', u'Stack', u'CountSummaryHints', u'CountDescHints', u'Grade', u'Sentences', u'Words', u'Syllables', u'ASW', u'ALS']
		self.input_row(sheet, row=0, col=_startCol, values=texts, default_style=self.title_format)

		sheet.freeze_panes(1, 0)  # Freeze the second row.
		self.data_row = 1
		return sheet

	def fill_DataSheet(self, _sheet, _program, _group, _project, _bugData, _rawData, _srcCounts, _ansCounts):
		#Write data and make basic statistics
		styles = [self.id_format]*6 + [self.number_format, self.base_format] + [
		          self.number_format, self.float_format, self.float_format, self.float_format,
		          self.number_format, self.number_format, self.number_format,
		          self.number_format, self.float_format, self.float_format, self.float_format,
		          self.base_format,self.id_format] + [self.number_format]*6 + [self.float_format] + [self.number_format]*3 + [self.float_format]*2

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
				          _program, _group, _project, this.id, this.version, count, this.filename, this.rank, this.score, normRank, normScore,
				          this.top1, this.top5, this.top10, this.AnsOrder, this.AP, AP, this.TP]
				if this.id in self.dupData[_project]:
					values.append(self.dupData[_project][this.id]['type'])
					values.append(self.dupData[_project][this.id]['dupID'])
				else:
					values+= [u'none',u'']

				if key in self.features:
					values += self.features[key]


				self.input_row(_sheet, self.data_row, 0, values, styles)
				self.data_row += 1
		return self.data_row

	#######################################################################
	# Raw Data Part Process
	#######################################################################
	def create_bugDataSheet(self, _startCol):
		sheet = self.workbook.add_worksheet(u'bugData')

		self.set_cols(sheet, 0, widths=[5, 12, 12, 12, 8, 12, 12, 6, 6, 6, 7, 7, 8, 8, 8,
		                                9, 7, 6, 6, 6, 6, 19, 15, 9, 9, 9, 9, 9, 9])

		texts = [u'key', u'Approach', u'Group', u'Project', u'BugID', u'Version', u'AnsFileCount', u'Top1', u'Top5', u'Top10', u'AP', u'TP',
		         u'Pin(1)', u'Pin(5)', u'Pin(10)',
		         u'DupType', u'DupID',
		         u'Talks', u'Enums', u'Code', u'Stack', u'CountSummaryHints', u'CountDescHints', u'Grade', u'Sentences',
		         u'Words', u'Syllables', u'ASW', u'ALS']
		self.input_row(sheet, row=0, col=_startCol, values=texts, default_style=self.title_format)

		sheet.freeze_panes(1, 0)  # Freeze the second row.
		self.bug_row = 1
		return sheet

	def fill_bugDataSheet(self, _sheet, _program, _group, _project, _bugData, _ansCounts):
		#Write data and make basic statistics
		data_keys = _bugData.keys()
		data_keys.sort()
		styles = [self.id_format]*6 + [self.number_format]*4+[self.float_format]*5 + [
		          self.base_format, self.id_format] + [self.number_format] * 6 + [self.float_format] + [self.number_format] * 3 + [self.float_format] * 2

		for bugID in data_keys:
			this = _bugData[bugID]			# for each bug id's results
			count = _ansCounts[this.id]
			key = u'%s%d'%(_project.lower(),this.id)
			values = [key, _program, _group, _project, this.id, this.version, count, this.top1, this.top5, this.top10, this.AP, this.TP]

			values.append(this.top1 / float(count if count <= 1 else 1))
			values.append(this.top5 / float(count if count <= 5 else 5))
			values.append(this.top10 / float(count if count <= 10 else 10))

			if this.id in self.dupData[_project]:
				values.append(self.dupData[_project][this.id]['type'])
				values.append(self.dupData[_project][this.id]['dupID'])
			else:
				values += [u'none', u'']

			if key in self.features:
				values += self.features[key]

			self.input_row(_sheet, self.bug_row, 0, values, styles)

			self.bug_row += 1
		return self.bug_row

	#######################################################################
	# Dup Data Part Process
	#######################################################################
	def create_DupSheet(self, _startCol):
		sheet = self.workbook.add_worksheet(u'Dups')

		self.set_cols(sheet, 0, widths=[5, 12, 12, 12, 8, 12, 12])

		texts = [u'key', u'Group', u'Project', u'BugID', u'DupType', u'DupID']
		self.input_row(sheet, row=0, col=_startCol, values=texts, default_style=self.title_format)
		sheet.freeze_panes(1, 0)  # Freeze the second row.
		self.dup_row = 1
		return sheet

	def fill_DupSheet(self, _sheet, _group, _project, _dupGroups):
		#Write data and make basic statistics
		styles = [self.id_format,
		          self.id_format, self.id_format, self.id_format, self.id_format, self.id_format, self.number_format,
		          self.number_format, self.number_format, self.number_format, self.float_format, self.float_format]

		self.dupData[_project] = {}
		for masterID, dupID in _dupGroups:
			self.dupData[_project][masterID] = {'type':u'master', 'dupID':dupID}
			self.dupData[_project][dupID] = {'type': u'duplicated', 'dupID': masterID}

		for masterID, dupID in _dupGroups:
			values = [u'%s%d'%(_project.lower(), masterID), _group, _project, masterID, u'master', dupID]
			self.input_row(_sheet, self.dup_row, 0, values, styles)
			self.dup_row += 1

		for masterID, dupID in _dupGroups:
			values = [u'%s%d'%(_project.lower(), dupID), _group, _project, dupID, u'duplicated', masterID]
			self.input_row(_sheet, self.dup_row, 0, values, styles)
			self.dup_row += 1

		return self.dup_row


	#######################################################################
	# Data Subject Part Process
	#######################################################################
	def create_SubjectSheet(self, _startCol):
		sheet = self.workbook.add_worksheet('Subjects')

		self.set_cols(sheet, col=0, widths=[15, 17, 15, 15, 15, 5, 15, 17, 20, 15, 15])
		self.input_colspan(sheet, row=0, col=0, span=5, values=[u'Summary'], default_style=self.title_format)
		self.input_colspan(sheet, row=0, col=6, span=5, values=[u'Details'], default_style=self.title_format)

		self.set_rows(sheet, row=1, heights=[50])
		texts = [u'Group', u'Project', u'Bug Reports\n(Sum)', u'Duplicate\nBug Reports\n(Sum)', u'Source Files\n(Max)']
		self.input_row(sheet, row=1, col=0, values=texts, default_style=self.title_format)

		texts = [u'Group', u'Project', u'Version', u'Bug Reports', u'Source Files']
		self.input_row(sheet, row=1, col=6, values=texts, default_style=self.title_format)

		formulas = [u'=sum(C4:C5000)', u'=sum(D4:D5000)', u'=sum(E4:E5000)']
		self.input_row(sheet, row=2, col=2, values=formulas, default_style=self.subtitle_number_format)

		formulas = [u'=sum(J4:J50000)', u'=sum(K4:K50000)']
		self.input_row(sheet, row=2, col=9, values=formulas, default_style=self.subtitle_number_format)

		sheet.freeze_panes(3, 0)  # Freeze the second row.
		self.subj_summary_row = 3
		self.subj_data_row = 3
		return sheet

	def fill_SubjectSheet(self, _sheet, _group, _project, _srcCounts, _bugs, _dupCount):
		styles = [self.base_format, self.base_format, self.base_format, self.number_format, self.number_format]
		for version in _bugs.keys():
			if version == 'all': continue
			if version == 'max': continue
			values = [_group, _project, version.upper(), len(_bugs[version]), _srcCounts[version]]
			self.input_row(_sheet, self.subj_data_row, 6, values, styles)
			self.subj_data_row += 1

		#summary
		styles = [self.subtitle_format, self.subtitle_format, self.number_format, self.number_format, self.number_format]
		values = [_group, _project.upper(),  len(_bugs['all']), _dupCount, _srcCounts['max']]
		self.input_row(_sheet, self.subj_summary_row, 0, values, styles)
		self.subj_summary_row += 1
		pass

	#######################################################################
	# Data Subject Part Process
	#######################################################################
	def create_DescriptionSheet(self):
		'''

		:return:
		'''
		titles = [u'Sheet', u'Columns', u'Description', u'Reference']
		texts = [
			[u'Summary', u'Approach', u'The technique that one of AmaLgam, BLIA, BLUiR, BugLocator, BRTracer, Locus', u''],
			[u'Summary', u'Group', u'Project group name (Apache, Apache Commons, Jboss, Wildfly, Spring)', u''],
			[u'Summary', u'Project', u'Project Name', u''],
			[u'Summary', u'BugCount', u'The number of bug count', u''],
			[u'Summary', u'Top1 Count', u'The count of bugs that Top1 is true', u''],
			[u'Summary', u'Top5 Count', u'The count of bugs that Top5 is true', u''],
			[u'Summary', u'Top10 Count', u'The count of bugs that Top10 is true', u''],
			[u'Summary', u'Top1', u'Top1 Count / # of Bugs', u''],
			[u'Summary', u'Top5', u'Top5 Count / # of Bugs', u''],
			[u'Summary', u'Top10', u'Top10 Count / # of Bugs', u''],
			[u'Summary', u'MAP', u'', u'https://en.wikipedia.org/wiki/Information_retrieval#Mean_average_precision'],
			[u'Summary', u'MRR', u'', u''],
			[u'rawData', u'Approach', u'The technique that one of AmaLgam, BLIA, BLUiR, BugLocator, BRTracer, Locus', u''],
			[u'rawData', u'Group', u'Project group name (Apache, Apache Commons, Jboss, Wildfly, Spring)', u''],
			[u'rawData', u'Project', u'Project Name (Subject)', u''],
			[u'rawData', u'BugID', u'The ID of bug report', u''],
			[u'rawData', u'Version', u'The source code version related a bug report', u''],
			[u'rawData', u'AnsFileCount', u'The number of fixed files (this is for bug level value)', u''],
			[u'rawData', u'File', u'the fixed file name', u''],
			[u'rawData', u'Rank', u'the ranking of a fixed file (The rank starts from 0)', u''],
			[u'rawData', u'Score', u'The suspicious score from specific technique [0, 1]', u''],
			[u'rawData', u'normalRank', u'( Rank + 1) / the number of SourceCode Files ', u''],
			[u'rawData', u'normalScore', u'score / maximum value of score in specific technique', u''],
			[u'rawData', u'Top1', u'1 if the answer file is ranked in top 1 (0=False, 1=True)', u''],
			[u'rawData', u'Top5', u'1 if the answer file is ranked in top 5 (0=False, 1=True)', u''],
			[u'rawData', u'Top10', u'1 if the answer file is ranked in top 10 (0=False, 1=True)', u''],
			[u'rawData', u'AnsOrder', u'The ranking value of each answer file in a bug report (the rank start 0)', u''],
			[u'rawData', u'P(rank)',  u'the precision of a answer file (this used for calculating MAP',
			 u'https://en.wikipedia.org/wiki/Information_retrieval#Mean_average_precision'],
			[u'rawData', u'TP', u'Top Precision, the precision of file that is ranked on top (other ranking files are 0)', u'https://en.wikipedia.org/wiki/Mean_reciprocal_rank'],
			[u'rawData', u'DupType', u'Master:core bug report in duplicated set / Duplicate: duplicated bug report in duplicated set / None: this is not related with duplication)', u''],
			[u'rawData', u'DupID', u'duplicate bug report ID', u''],
			[u'rawData', u'Talks', u'Attribute of bug report : True if talks is exists in a bug report (extracted from Infozilla)', u''],
			[u'rawData', u'Enums', u'Attribute of bug report : True if talks is enumeration in a bug report (extracted from Infozilla)', u''],
			[u'rawData', u'Code', u'Attribute of bug report: True if talks is code region in a bug report (extracted from Infozilla)', u''],
			[u'rawData', u'Stack', u'Attribute of bug report : True if talks is stack trace in a bug report (extracted from Infozilla)', u''],
			[u'rawData', u'CountSummaryHints',
			 u'Attribute of bug report : the number of Hint keywords in summary in bug report (class, package, method name, and so on.)', u''],
			[u'rawData', u'CountDescHints',
			 u'Attribute of bug report : the number of Hint keywords in description in bug report (class, package, method name, and so on.)', u''],
			[u'bugData', u'-', u'Sheet Description : The summary information of each bug report', u''],
			[u'bugData', u'AP', u'The average of precision for the all answers in a bug report', u''],
			[u'bugData', u'TP', u'Top Precision, the precision of file that is ranked on top (this used for MRR)', u'']
		]

		sheet = self.workbook.add_worksheet(u'Description')
		self.set_cols(sheet, col=0, widths=[8, 25, 100, 30])
		self.input_row(sheet, row=0, col=0, values=titles, default_style=self.title_format)
		row = 1
		styles = [self.id_format, self.id_format, self.base_format, self.base_format]
		for text in texts:
			self.input_row(sheet, row=row, col=0, values=text, styles=styles, default_style=self.base_format)
			row +=1

		sheet.freeze_panes(1, 0)  # Freeze the second row.
		pass

	#######################################################################
	# Data Subject Part Process
	#######################################################################
	def create_FeaturesSheet(self, _filename):
		'''
		:return:
		'''
		self.features = {}
		sheet = self.workbook.add_worksheet(u'Features')
		styles = [self.id_format] * 4 + [self.number_format]*6 + [self.float_format] + [self.number_format]*3 + [self.float_format]*2
		f = open(_filename, 'r')
		titles = f.readline().split(u'\t')
		titles[-1] = titles[-1][:-1]   # cut-off '\n'
		self.set_cols(sheet, col=0, widths=[ 5,9,7,6, 6,6,6,6,19,15,8,9,6,8,7,7])
		self.input_row(sheet, row=0, col=0, values=titles, default_style=self.title_format)

		row_idx=0
		while True:
			line = f.readline()
			if line is None or len(line) == 0: break
			row_idx += 1
			values = line.split(u'\t')
			if len(values) < 16:
				items = values[:3] + [int(values[3])]
			else:
				items = values[:3] + [int(v) for v in values[3:10]] + [float(values[10]), int(values[11]), int(values[12]), int(values[13]), float(values[14]), float(values[15])]
				self.features[items[0]] = items[4:]
			self.input_row(sheet, row=row_idx, col=0, values=items, styles= styles)

		f.close()
		sheet.freeze_panes(1, 0)  # Freeze the second row.
		pass


	#######################################################################
	# Overall Process
	#######################################################################
	# def run_group(self, _group, _inputPath, _bugPath, _srcPath, _workingPath):
	#
	# 	# preparing
	# 	T = Targets()
	# 	T.load(_inputPath)
	# 	S = Subjects(_bugPath, _srcPath, _workingPath)
	# 	S.make_subjects(T.projects, T.versions)
	# 	D = Duplicates(_bugPath, _workingPath)
	# 	D.make_duplicates(T.projects)
	#
	# 	self.fill_DupSheet(self.dupSheet, _group, D.dupGroups)
	# 	self.fill_SubjectSheet(self.subjectSheet, _group, S.srcCounts, S.bugCounts, D.dupCounts)
	#
	# 	# create data sheet
	# 	progress = Progress(u'[%s] fill data from %s' % (self.__name__, _group), 2, 10, True)
	# 	progress.set_point(0).set_upperbound(len(T.programs)*len(T.projects))
	# 	progress.start()
	# 	for program in T.programs:
	# 		for project in T.projects:
	# 			ev = Evaluator(program, project)
	# 			ev.load(T.files[program][project])
	# 			ev.evaluate(S.ansCounts[project]['all'], S.bugCounts[project]['all'])
	#
	# 			self.fill_SummarySheet(self.summarySheet, _group, program, project, ev.projectSummary, S.bugCounts[project]['all'], len(ev.bugSummaries))
	# 			self.fill_bugDataSheet(self.bugSheet, program, _group, project, ev.bugSummaries, S.ansCounts[project]['all'])
	# 			self.fill_DataSheet(self.dataSheet, program, _group, project, ev.bugSummaries, ev.rawData, S.srcCounts[project], S.ansCounts[project]['all'])
	#
	# 			progress.check()
	# 	progress.done()
	# 	pass

	def append_project(self, _group, _project, _tech, _isUnion):
		resultFiles = []
		if _isUnion is False:
			for version in self.S.bugs[_project].keys():
				if version == 'all': continue
				versionName = u'%s' % version
				resultFiles.append(self.S.getPath_results(self.TYPE, _tech, _group, _project, versionName))
		else:
			resultFiles.append(self.S.getPath_results(self.TYPE, _tech, _group, _project, 'all'))

		ev = Evaluator(_tech, _project)
		ev.load(resultFiles)
		ev.evaluate(self.S.answers[_project]['all'], len(self.S.bugs[_project]['all']))

		self.fill_SummarySheet(self.summarySheet, _group, _tech, _project, ev.projectSummary, self.S.sources[_project]['max'], len(self.S.bugs[_project]['all']), len(ev.bugSummaries))
		self.fill_bugDataSheet(self.bugSheet, _tech, _group, _project, ev.bugSummaries, self.S.answers[_project]['all'])
		self.fill_DataSheet(self.dataSheet, _tech, _group, _project, ev.bugSummaries, ev.rawData, self.S.sources[_project], self.S.answers[_project]['all'])
		pass

	def run(self, _type, _isUnion=False, _featureFile=None):
		'''
		create result file
		'''
		self.TYPE = _type
		print(_type)
		# XLS preparing
		self.summarySheet = self.create_SummarySheet(0)
		self.create_DescriptionSheet()
		self.subjectSheet = self.create_SubjectSheet(0)
		self.dataSheet = self.create_DataSheet(0)
		self.bugSheet = self.create_bugDataSheet(0)
		if _featureFile is not None:
			self.create_FeaturesSheet(_featureFile)
		self.dupSheet = self.create_DupSheet(0)

		self.dupData = {}
		self.features = {}
		self.S = Subjects()
		for group in self.S.groups:  #['Commons', 'JBoss', 'Wildfly', 'Spring']
			for project in self.S.projects[group]:
				#if project not in ['HBASE','HIVE','ROO','SEC', 'SPR']:continue   #
				print(u'working %s / %s ...' % (group, project), end=u'')

				# fill Dup and subjects
				self.dupData[project] = {}
				self.fill_DupSheet(self.dupSheet, group, project, self.S.duplicates[project])
				self.fill_SubjectSheet(self.subjectSheet, group, project,
				                       self.S.sources[project],
				                       self.S.bugs[project],
				                       len(self.S.duplicates[project]))

				for tech in ['Locus']:#self.S.techniques: #['BLIA']:#  ['BugLocator', "BRTracer", 'BLUiR', 'BLIA']:#, 'Locus']:#
					print(tech + u' ', end=u'')
					self.append_project(group, project, tech, _isUnion)
				print(u' Done')
		self.finalize()
		pass


###############################################################################################################
###############################################################################################################
###############################################################################################################
if __name__ == "__main__":
	name = u'ISSTASingle'
	obj = XLSResultAll(u'/mnt/exp/Bug/expresults/Result_%s.xlsx' % name)
	obj.run(name, _isUnion=True)

	pass

