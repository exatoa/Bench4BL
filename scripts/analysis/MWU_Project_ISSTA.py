#-*- coding: utf-8 -*-
'''
Created on 2017. 02. 12
Updated on 2017. 02. 12

'''
from __future__ import print_function
import os
import matplotlib
# Force matplotlib to not use any Xwindows backend.
matplotlib.use('Agg')

from scipy.stats import mannwhitneyu
from ExpBase import ExpBase
from commons import Subjects


class MWUTest(ExpBase):
	techniques = ['BugLocator', 'BRTracer', 'BLUiR', 'AmaLgam', 'BLIA', 'Locus']

	def MWUtest(self, _dataA, _dataB, _bugsA=None, _bugsB=None):
		'''
		Mann-Whitney U Test between IRBL technique results
		:param _nameA: The results of Type A
		:param _nameB: The results of Type B
		:param _bugsA: the count of bugs for each techniques
		:param _bugsB: the count of bugs for each techniques
		:return: {technique : pvalue, techinique: pvalue, ...}
		'''

		results = {}

		for idx in range(len(self.techniques)):
			filteredDataA = []
			filteredDataB = []
			for group, projValues in _dataA.iteritems():
				for project, bugValues in projValues.iteritems():
					filteredDataA += [items[idx] for items in bugValues.values()]
			for group, projValues in _dataB.iteritems():
				for project, bugValues in projValues.iteritems():
					filteredDataB += [items[idx] for items in bugValues.values()]

			# filteredDataA += [items[idx] for items in _dataA.values()]
			# filteredDataB = [items[idx] for items in _dataB.values()]
			#filteredDataA, labels = self.get_array_items(_dataA, idx)
			#filteredDataB, labels = self.get_array_items(_dataB, idx)

			if _bugsA is not None:
				if isinstance(_bugsA, dict) is True:
					filteredDataA += ([0] * (_bugsA[self.techniques[idx]] - len(filteredDataA)))
				else:
					filteredDataA += ([0] * (_bugsA - len(filteredDataA)))
			if _bugsB is not None:
				if isinstance(_bugsB, dict) is True:
					filteredDataB += ([0] * (_bugsB[self.techniques[idx]] - len(filteredDataB)))
				else:
					filteredDataB += ([0] * (_bugsB - len(filteredDataB)))


			#slope, intercept, r_value, p_value, stderr = stats.linregress(dataMAP, dataFeature)
			t_statistic, t_pvalue = mannwhitneyu(filteredDataA, filteredDataB, use_continuity=True, alternative='two-sided')
			l_statistic, l_pvalue = mannwhitneyu(filteredDataA, filteredDataB, use_continuity=True, alternative='less')
			g_statistic, g_pvalue = mannwhitneyu(filteredDataA, filteredDataB, use_continuity=True, alternative='greater')

			pvalue = min(t_pvalue , l_pvalue, g_pvalue)
			#statistic, pvalue = mannwhitneyu(filteredDataA, filteredDataB, use_continuity=True, alternative='two-sided')  # 'less', 'two-sided', 'greater'

			results[self.techniques[idx]] = pvalue

		return results

	def get_technique_averages(self, _source, _counts):
		'''

		:param _source: project's bug results dict
		:param _count: original bug counts for each technique
		:return:
		'''
		results = {}
		for idx in range(len(self.techniques)):
			sumValue = 0
			for itemID, item in _source.iteritems():
				sumValue += item[idx]
			results[self.techniques[idx]] = sumValue / float(_counts[self.techniques[idx]])
		return results

	def make_max_text(self, _value, _isMAX, _color):
		if _isMAX is True:
			return u' &  \\cellcolor{%s}\\textbf{%.4f}' % (_color, _value)
		else:
			return u' &  %.4f' % _value

	def get_max_technique(self, _values, _techniques):
		maxTech = _techniques[0]
		for tech in _techniques:
			if _values[maxTech] < _values[tech]:
				maxTech = tech
		return maxTech

	def compare_single_results(self, _basepath):
		'''
		for Table 1 : single results
		:param _basepath:
		:return:
		'''

		def get_averages(_itemType):
			results = {}
			for tData in ['Old', 'New_Single']:
				filepath = os.path.join(_basepath, u'%s_%s.txt' % (tData, _itemType))
				titles, data = self.load_results_items(filepath, ['str'] * 3 + ['float'] * 6)
				for group in data:
					if group not in results: results[group] = {}
					for project in data[group]:
						CNTs = dict(zip(titles, CNTdata[group][project]))
						results[group][project] = self.get_technique_averages(data[group][project], CNTs)
			return results

		techinques, CNTdata = self.load_results(os.path.join(_basepath, u'BugCNT.txt'), ['str'] * 2 + ['int'] * 6)

		APresults = get_averages('AP')
		TPresults = get_averages('TP')

		S = Subjects()

		# make MAP values for New Subjects
		print(u'\n\n')
		print(u'Technique Mann-Whitney U Test p-values')
		print(u'\t' + u'\t\t'.join(self.techniques))
		print(u'Subject\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR')
		print(u"\\hline")
		print(u"\\multicolumn{13}{c}{\\bf New subjects} \\\\")
		print(u"\\hline")
		for group in S.groups:
			for project in S.projects[group]:
				text = u'%s' % project
				APmax = self.get_max_technique(APresults[group][project], self.techniques)
				TPmax = self.get_max_technique(TPresults[group][project], self.techniques)

				for tech in self.techniques:
					text += self.make_max_text(APresults[group][project][tech], APmax==tech, u'blue!25')
					text += self.make_max_text(TPresults[group][project][tech], TPmax==tech, u'green!25')
				text += u' \\\\'
				print(text)

		# make average information
		avgAPs = dict(zip(self.techniques, [0] * len(self.techniques)))
		avgTPs = dict(zip(self.techniques, [0] * len(self.techniques)))
		for tech in self.techniques:
			count = 0
			for group in S.groups:
				count += len(S.projects[group])
				avgAPs[tech] += sum(APresults[group][project][tech] for project in S.projects[group])
				avgTPs[tech] += sum(TPresults[group][project][tech] for project in S.projects[group])
			avgAPs[tech] /= count
			avgTPs[tech] /= count

		text = u'Average'
		APmax = self.get_max_technique(avgAPs, self.techniques)
		TPmax = self.get_max_technique(avgTPs, self.techniques)
		for tech in self.techniques:
			text += self.make_max_text(avgAPs[tech], APmax == tech, u'blue!25')
			text += self.make_max_text(avgTPs[tech], TPmax == tech, u'green!25')
		text += u' \\\\'
		print(u'\\hline')
		print(text)

		# make MAP values for OLD Subjects
		print(u"\\hline")
		print(u"\\multicolumn{13}{c}{\\bf Old subjects} \\\\")
		print(u"\\hline")
		group = u'Previous'
		projects = [u'AspectJ', u'ZXing', u'PDE', u'JDT', u'SWT']
		for project in projects:
			text = u'%s' % project
			APmax = self.get_max_technique(APresults[group][project], self.techniques)
			TPmax = self.get_max_technique(TPresults[group][project], self.techniques)

			for tech in self.techniques:
				text += self.make_max_text(APresults[group][project][tech], APmax==tech, u'blue!25')
				text += self.make_max_text(TPresults[group][project][tech], TPmax==tech, u'green!25')
			text += u' \\\\'
			print(text)

		# make average information
		avgAPs = {}
		avgTPs = {}
		for tech in self.techniques:
			avgAPs[tech] = sum(APresults[group][project][tech] for project in projects) / len(projects)
			avgTPs[tech] = sum(TPresults[group][project][tech] for project in projects) / len(projects)

		text = u'Average'
		APmax = self.get_max_technique(avgAPs, self.techniques)
		TPmax = self.get_max_technique(avgTPs, self.techniques)
		for tech in self.techniques:
			text += self.make_max_text(avgAPs[tech], APmax == tech, u'blue!25')
			text += self.make_max_text(avgTPs[tech], TPmax == tech, u'green!25')
		text += u' \\\\'
		print(u'\\hline')
		print(text)

		pass

	def compare_multi_results(self, _basepath, _withoutTest=False):
		'''
		:param _basepath:
		:return:
		'''
		def get_average_mwu(_itemType):
			results = {}
			multi = os.path.join(_basepath, u'New_Multiple%s_%s.txt' % ('_noTest' if _withoutTest is True else '', _itemType))
			titles, dataM = self.load_results_items(multi, ['str'] * 3 + ['float'] * 6)
			# MWUresults = {}
			# single = os.path.join(_basepath, u'New_Single_%s.txt' % _itemType)
			# titles, dataS = self.load_results_items(single, ['str'] * 3 + ['float'] * 6)
			for group in dataM:
				if group not in results: results[group] = {}
				#if group not in MWUresults: MWUresults[group] = {}
				for project in dataM[group]:
					CNTs = dict(zip(titles, CNTdata[group][project]))
					results[group][project] = self.get_technique_averages(dataM[group][project], CNTs)
					#MWUresults[group][project] = self.MWUtest(dataS[group][project], dataM[group][project], CNTs, CNTs)

			return results #, MWUresults

		techinques, CNTdata = self.load_results(os.path.join(_basepath, u'BugCNT.txt'), ['str'] * 2 + ['int'] * 6)

		APresults = get_average_mwu('AP')
		TPresults = get_average_mwu('TP')

		print(u'')
		print(u'\t' + u'\t\t'.join(self.techniques))
		print(u'Subject\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR\tMAP\tMRR')
		S = Subjects()
		for group in S.groups:
			for project in S.projects[group]:
				text = u'%s' % project
				APmax = self.get_max_technique(APresults[group][project], self.techniques)
				TPmax = self.get_max_technique(TPresults[group][project], self.techniques)

				for tech in self.techniques:
					text += self.make_max_text(APresults[group][project][tech], APmax==tech, u'blue!25')
					text += self.make_max_text(TPresults[group][project][tech], TPmax==tech, u'green!25')
				text += u' \\\\'
				print(text)

		# make average information
		avgAPs = dict(zip(self.techniques, [0]*len(self.techniques)))
		avgTPs = dict(zip(self.techniques, [0]*len(self.techniques)))
		for tech in self.techniques:
			count = 0
			for group in S.groups:
				count += len(S.projects[group])
				avgAPs[tech] += sum(APresults[group][project][tech] for project in S.projects[group])
				avgTPs[tech] += sum(TPresults[group][project][tech] for project in S.projects[group])
			avgAPs[tech] /= count
			avgTPs[tech] /= count

		text = u'Average'
		APmax = self.get_max_technique(avgAPs, self.techniques)
		TPmax = self.get_max_technique(avgTPs, self.techniques)
		for tech in self.techniques:
			text += self.make_max_text(avgAPs[tech], APmax == tech, u'blue!25')
			text += self.make_max_text(avgAPs[tech], TPmax == tech, u'green!25')
		text += u' \\\\'
		print(u'\\hline')
		print(text)


		# print(u'\\hline')
		# text = u'Average'
		# for tech in self.techniques:
		# 	avgAP = avgTP = 0.0
		# 	count = 0
		# 	for group in S.groups:
		# 		count += len(S.projects[group])
		# 		avgAP += sum(APresults[group][project][tech] for project in S.projects[group])
		# 		avgTP += sum(TPresults[group][project][tech] for project in S.projects[group])
		# 	text += u' & {:.4f} & {:.4f}'.format(avgAP / count, avgTP/count)
		# text += u" \\\\"
		# print(text)
		pass

	def extract_features(self, _basepath):
		titles, data = self.load_results(os.path.join(_basepath, u'02_PW_Bug_Features.txt'), ['str'] * 2 + ['int'] + ['float'] * 3 + ['int', 'float'] )

		for group in data:
			for project in data[group]:
				item = data[group][project]
				data[group][project] = dict(zip([u'RatioEnum', u'RatioSTrace', u'RatioCode', u'RepAvgTk'], [item[1], item[2], item[3], item[5]]))
		return data

	def make_average(self, _source, _bugCount=None):
		titles, data = self.load_results_items(_source, ['str'] * 3 + ['float'] * 6)

		results = {}
		for idx in range(len(self.techniques)):
			filteredData, labels = self.get_array_items(data, idx)

			if _bugCount is not None:
				if isinstance(_bugCount, dict) is True:
					average = sum(filteredData) / float(_bugCount[self.techniques[idx]])
				else:
					average = sum(filteredData) / float(_bugCount)
			else:
				average = sum(filteredData) / float(len(filteredData))

			results[self.techniques[idx]] = average
		return results

	def load_counts(self, _filepath, _types):
		'''
		CNT_total파일로부터 버그리포트 숫자를 로드함
		파일은 데이터 종류별로 각 테크닉에 사용된 버그리포트 수를 담고있음
		ex) Type	BugLocator	BRTracer	BLUiR	AmaLgam	BLIA	Locus
			New	9504	9504	9504	9504	9504	9600
			OLD	558	558	558	558	558	516
		:param _filepath:
		:param _types:
		:return:
		'''
		f = open(_filepath, 'r')
		lines = f.readlines()
		f.close()

		titles = lines[0].strip().split(u'\t')[1:]
		if len(titles) > len(_types) - 1:
			titles = titles[:len(_types) - 1]

		for x in range(len(titles)): titles[x] = titles[x].strip()

		data = {}
		for line in lines[1:]:
			cols = line.strip().split(u'\t')
			Dtype = cols[0]

			data[Dtype] = {}  # data type
			for x in range(1, len(cols) if len(_types) > len(cols) else len(_types)):
				data[Dtype][titles[x-1]] = self.get_value(cols[x], _types[x])

		return titles, data

	def compare_results(self, _basepath):
		'''
		for Table 8
		Compare Old project results, Single project results, Multi-version project results using Mann-Whitney U test.
		:param _basepath:
		:return:
		'''
		techniques, CNTdata = self.load_counts(os.path.join(_basepath, u'BugCNT_Total.txt'), ['str'] + ['int'] * 6)

		OldSingle_Pvalues = {}
		SingleMulti_Pvluaes = {}
		MultiTest_Pvluaes = {}
		avgs = {'OLD':{}, 'Single':{}, 'Multi':{}, 'MultiTest':{}}
		for item in ['AP', 'TP']:
			oldfile = os.path.join(_basepath, u'Old_%s.txt' % item)
			singlefile = os.path.join(_basepath, u'New_Single_%s.txt' % item)
			multifile = os.path.join(_basepath, u'New_Multiple_%s.txt' % item)
			multitestfile = os.path.join(_basepath, u'New_Multiple_noTest_%s.txt' % item)
			avgs['OLD'][item] = self.make_average(oldfile, CNTdata['OLD'])
			avgs['Single'][item] = self.make_average(singlefile, CNTdata['NEW'])
			avgs['Multi'][item] = self.make_average(multifile, CNTdata['NEW'])
			avgs['MultiTest'][item] = self.make_average(multitestfile, CNTdata['NEW'])

			titlesO, dataO = self.load_results_items(oldfile, ['str'] * 3 + ['float'] * 6)
			titlesS, dataS = self.load_results_items(singlefile, ['str'] * 3 + ['float'] * 6)
			titlesM, dataM = self.load_results_items(multifile, ['str'] * 3 + ['float'] * 6)
			titlesT, dataT = self.load_results_items(multitestfile, ['str'] * 3 + ['float'] * 6)

			OldSingle_Pvalues[item] = self.MWUtest(dataO, dataM, CNTdata['OLD'], CNTdata['NEW'])
			SingleMulti_Pvluaes[item] = self.MWUtest(dataS, dataM, CNTdata['NEW'], CNTdata['NEW'])
			MultiTest_Pvluaes[item] = self.MWUtest(dataM, dataT, CNTdata['NEW'], CNTdata['NEW'])

		def make_text(_valA, _valB, _valStar):
			m = u'{:.4f}'.format(_valB)
			arraw = u'$\\nearrow$~' if _valB > _valA else (u'$\\searrow$~' if _valB < _valA else u'')
			return (u' & \\textbf{%s%s%s}' % (arraw, m, _valStar) if _valStar != u'' else u' & %s%s' % (arraw, m))

		print(u'\n\n\nTechnique Mann-Whitney U Test p-values')
		print(u'\tOld\t\tSingle')
		print(u'Technique\tMAP\tMRR\tMAP\tMRR')
		for tech in self.techniques:
			APstar = u'$^{\\ast\\ast}$' if OldSingle_Pvalues['AP'][tech] < 0.01 else (u'$^{\\ast}$' if OldSingle_Pvalues['AP'][tech] < 0.05 else u'')
			TPstar = u'$^{\\ast\\ast}$' if OldSingle_Pvalues['TP'][tech] < 0.01 else (u'$^{\\ast}$' if OldSingle_Pvalues['TP'][tech] < 0.05 else u'')

			text = u'{} & {:.4f} & {:.4f}'.format(tech, avgs['OLD']['AP'][tech], avgs['OLD']['TP'][tech])
			text += make_text(avgs['OLD']['AP'][tech], avgs['Single']['AP'][tech], APstar)
			text += make_text(avgs['OLD']['TP'][tech], avgs['Single']['TP'][tech], TPstar)
			text += u'\\\\'
			print(text)

		print(u'\n\n')
		print(u'Technique Mann-Whitney U Test p-values')
		print(u'\tSingle\t\tMulti')
		print(u'Technique\tMAP\tMRR\tMAP\tMRR')
		for tech in self.techniques:
			APstar = u'$^{\\ast\\ast}$' if SingleMulti_Pvluaes['AP'][tech] < 0.01 else (
			u'$^{\\ast}$' if SingleMulti_Pvluaes['AP'][tech] < 0.05 else u'')
			TPstar = u'$^{\\ast\\ast}$' if SingleMulti_Pvluaes['TP'][tech] < 0.01 else (
			u'$^{\\ast}$' if SingleMulti_Pvluaes['TP'][tech] < 0.05 else u'')

			text = u'{} & {:.4f} & {:.4f}'.format(tech, avgs['Single']['AP'][tech], avgs['Single']['TP'][tech])
			text += make_text(avgs['Single']['AP'][tech], avgs['Multi']['AP'][tech], APstar)
			text += make_text(avgs['Single']['TP'][tech], avgs['Multi']['TP'][tech], TPstar)
			text += u'\\\\'
			print(text)

		print(u'\n\n')
		print(u'Technique Mann-Whitney U Test p-values')
		print(u'\tMulti\t\tWithoutTest')
		print(u'Technique\tMAP\tMRR\tMAP\tMRR')
		for tech in self.techniques:
			APstar = u'$^{\\ast\\ast}$' if MultiTest_Pvluaes['AP'][tech] < 0.01 else (u'$^{\\ast}$' if MultiTest_Pvluaes['AP'][tech] < 0.05 else u'')
			TPstar = u'$^{\\ast\\ast}$' if MultiTest_Pvluaes['TP'][tech] < 0.01 else (u'$^{\\ast}$' if MultiTest_Pvluaes['TP'][tech] < 0.05 else u'')

			text = u'{} & {:.4f} & {:.4f}'.format(tech, avgs['Multi']['AP'][tech], avgs['Multi']['TP'][tech])
			text += make_text(avgs['Multi']['AP'][tech], avgs['MultiTest']['AP'][tech], APstar)
			text += make_text(avgs['Multi']['TP'][tech], avgs['MultiTest']['TP'][tech], TPstar)
			text += u'\\\\'
			print(text)

###############################################################################################################
###############################################################################################################
if __name__ == "__main__":
	basepath = u'/mnt/exp/Bug/analysis/'
	obj = MWUTest()
	obj.compare_single_results(basepath)
	obj.compare_multi_results(basepath, _withoutTest=False)			# compare version matching results including Test Files
	obj.compare_multi_results(basepath, _withoutTest=True)			# compare version matching results without Test Files
	# obj.compare_results(basepath)
	# obj.compare_single_results(basepath)
	# obj.compare_test(basepath)
	#obj.calc_pearson(basepath)
	#obj.compare_dup_results(basepath)

