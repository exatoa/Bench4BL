#-*- coding: utf-8 -*-
'''
Created on 2017. 02. 13
Updated on 2017. 02. 13
'''
from __future__ import print_function
import os
import matplotlib.pyplot as plt


class ExpBaseBug(object):
	def __init__(self):
		pass

	def get_order(self, _dataX):
		order = []
		for group, projects in _dataX.iteritems():
			for project, items in projects.iteritems():
				for itemID, data in items.iteritems():
					order.append((group, project, itemID))
		return order

	def get_array(self, _dataX, _colX, _orders):
		arrayX = []
		labels = list()
		for group, project, itemID in _orders:
			arrayX.append(_dataX[group][project][itemID][_colX])
			labels.append(u'%s_%s' % (project,itemID))
		return arrayX, labels #list(labels)

	def get_splitted_array(self, _dataX, _colX, _orders):
		arrayX = {}
		labels = {}
		for group, project, itemID in _orders:
			if group not in arrayX: arrayX[group] = {}
			if project not in arrayX[group]: arrayX[group][project] = []

			arrayX[group][project].append(_dataX[group][project][itemID][_colX])

			if group not in labels: labels[group] = {}
			if project not in labels[group]: labels[group][project] = []
			labels[group][project].append(u'%s' % (itemID))
		return arrayX, labels #list(labels)

	def load_results(self, _filename, _types):
		f = open(_filename, 'r')
		lines = f.readlines()
		f.close()

		titles = lines[0][:-1].split(u'\t')[3:]
		data = {}
		for line in lines[1:]:
			cols = line[:-1].split(u'\t')
			group = cols[0]
			project = cols[1]
			itemID = cols[2]
			if group not in data: data[group] = {}  # group
			if project not in data[group]: data[group][project] = {}  # project
			data[group][project][itemID] = []    # itemID

			for x in range(3, len(cols)):
				if _types[x] == 'int': data[group][project][itemID].append(int(cols[x]))
				elif _types[x] == 'float': data[group][project][itemID].append(float(cols[x]))
				elif _types[x] == 'str': data[group][project][itemID].append(str(cols[x]))
				else: data[group][project][itemID].append(str(cols[x]))

		return titles, data

	def draw_lineargress(self, _x, _y, _title, _xlabel, _ylabel,  _filename):
		plt.plot(_x, _y, 'ro')
		#plt.axis([0, 6, 0, 20])
		plt.title(_title)
		plt.xlabel(_xlabel)
		plt.ylabel(_ylabel)

		filepath = os.path.join(self.OUTPUT, _filename)
		plt.savefig(filepath)
		plt.clf()  # Clear figure
		#plt.show()

	def draw_sactter(self, _title, _Dx, _Dy, _xlabels, _ylabels, _datalabels, _filename):

		plt.figure(num=None, figsize=(16, 12), dpi=100, facecolor='w', edgecolor='k')
		plt.rc('font', **{'size':22})
		plt.rc('xtick', labelsize=20)
		plt.rc('ytick', labelsize=20)

		plt.plot(_Dx, _Dy, 'ro')
		plt.title(_title)
		plt.xlabel(_xlabels)
		plt.ylabel(_ylabels)

		if _datalabels is not None:
			for label, x, y in zip(_datalabels, _Dx, _Dy):
				#if x <= 0.5 or y >= 0.4: continue
				plt.annotate(label,
							xy=(x, y), xytext=(0, 0),
							textcoords='offset points', ha='right', va='bottom',
							#bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.5),
							#arrowprops=dict(arrowstyle='->', connectionstyle='arc3,rad=0'),
							fontsize = 15
				)

		filepath = os.path.join(self.OUTPUT, _filename)
		plt.savefig(filepath)
		plt.clf()  # Clear figure
		plt.close()
		#plt.show()

###############################################################################################################
###############################################################################################################
if __name__ == "__main__":
	pass