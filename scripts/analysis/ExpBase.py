#-*- coding: utf-8 -*-
'''
Created on 2017. 02. 13
Updated on 2017. 02. 13

'''
from __future__ import print_function
import os
import matplotlib
# Force matplotlib to not use any Xwindows backend.
matplotlib.use('Agg')

import numpy as np
import matplotlib.pyplot as plt


class ExpBase(object):
	OUTPUT = u''

	def __init__(self):
		pass

	def get_order(self, _dataX):
		order = []
		for group, projects in _dataX.iteritems():
			for project, data in projects.iteritems():
				order.append((group, project))
		return order

	def get_order_items(self, _dataX):
		order = []
		for group, projects in _dataX.iteritems():
			for project, items in projects.iteritems():
				for itemID in items:
					order.append((group, project, itemID))
		return order


	def get_array(self, _dataX, _colX, _orders=None):
		arrayX = []
		labels = list()

		if _orders is None:
			for group, projects in _dataX.iteritems():
				for project, item in projects.iteritems():
					arrayX.append(item[_colX])
					labels.append(project)
		else:
			for group, project in _orders:
				arrayX.append(_dataX[group][project][_colX])
				labels.append(project)
		return arrayX, labels

	def get_array_items(self, _dataX, _colX, _orders=None):
		arrayX = []
		labels = list()

		if _orders is None:
			for group, projects in _dataX.iteritems():
				for project, items in projects.iteritems():
					for itemID, item in items.iteritems():
						arrayX.append(item[_colX])
						labels.append(itemID)
		else:
			for group, project, itemID in _orders:
				if itemID not in _dataX[group][project]: continue
				arrayX.append(_dataX[group][project][itemID][_colX])
				labels.append(itemID)
		return arrayX, labels #list(labels)

	def load_results(self, _filename, _types):
		f = open(_filename, 'r')
		lines = f.readlines()
		f.close()

		titles = lines[0].strip().split(u'\t')[2:]
		if len(titles)> len(_types)-2:
			titles = titles[:len(_types)-2]

		for x in range(len(titles)): titles[x] = titles[x].strip()

		data = {}
		for line in lines[1:]:
			cols = line.strip().split(u'\t')
			group = cols[0]
			project = cols[1]
			if group not in data: data[group] = {}  # group
			data[group][project] = []    #project

			for x in range(2, len(cols) if len(_types) > len(cols) else len(_types)):
				data[group][project].append(self.get_value(cols[x], _types[x]))
				# if _types[x] == 'int': data[group][project].append(int(cols[x]))
				# elif _types[x] == 'float': data[group][project].append(float(cols[x]))
				# elif _types[x] == 'str': data[group][project].append(str(cols[x]))
				# else: data[group][project].append(str(cols[x]))

		return titles, data

	def get_value(self, _value, _type):
		if _value == '':
			return 0
		if _type == 'int':	    return int(_value)
		elif _type == 'float':  return float(_value)
		elif _type == 'str':    return str(_value)
		else:                   return str(_value)

	def load_results_items(self, _filename, _types):
		f = open(_filename, 'r')
		lines = f.readlines()
		f.close()

		titles = lines[0].strip().split(u'\t')[3:]
		if len(titles) > len(_types) - 3:
			titles = titles[:len(_types) - 3]

		for x in range(len(titles)): titles[x] = titles[x].strip()

		data = {}
		for line in lines[1:]:
			cols = line.strip().split(u'\t')
			group = cols[0]
			project = cols[1]
			itemID = cols[2]
			if group not in data: data[group] = {}  # group
			if project not in data[group]: data[group][project] = {}  # project
			data[group][project][itemID] = []    # itemID

			for x in range(3, 3+len(titles)):
				data[group][project][itemID].append(self.get_value(cols[x], _types[x]))

		return titles, data

	def load_dict_data(self, _filename):
		f = open(_filename, 'r')
		text = f.read()
		f.close()
		parts = eval(text)
		return parts

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

	def draw_scatter(self, _title, _Dx, _Dy, _xlabels, _ylabels, _datalabels, _filename):

		plt.figure(num=None, figsize=(16, 12), dpi=100, facecolor='w', edgecolor='k')
		plt.rc('font', **{'size':22})
		plt.rc('xtick', labelsize=20)
		plt.rc('ytick', labelsize=20)

		plt.plot(_Dx, _Dy, 'ro')
		plt.title(_title)
		plt.xlabel(_xlabels)
		plt.ylabel(_ylabels)

		for label, x, y in zip(_datalabels, _Dx, _Dy):
			#if x <= 0.5 or y >= 0.4: continue
			plt.annotate(label,
						xy=(x, y), xytext=(0, 0),
						textcoords='offset points', ha='left', va='bottom',
						#bbox=dict(boxstyle='round,pad=0.5', fc='yellow', alpha=0.5),
						#arrowprops=dict(arrowstyle='->', connectionstyle='arc3,rad=0'),
						fontsize = 15
			)

		filepath = os.path.join(self.OUTPUT, _filename)
		plt.savefig(filepath)
		plt.clf()  # Clear figure
		plt.close()
		#plt.show()

	def draw_scatter2(self, _title, _Dx, _Dy, _xlabel, _ylabel, _filepath):
		#N = 50
		x = np.asarray(_Dx)
		y = np.asarray(_Dy)

		plt.figure(num=None, figsize=(16, 12), dpi=100, facecolor='w', edgecolor='k')
		plt.rc('font', **{'size': 22})
		plt.rc('xtick', labelsize=20)
		plt.rc('ytick', labelsize=20)
		plt.title(_title)
		plt.xlabel(_xlabel)
		plt.ylabel(_ylabel)

		area = np.pi * (5 * np.random.rand(len(_Dx))) ** 2  # 0 to 15 point radii

		plt.scatter(x, y, s=area,  alpha=0.5) #c=colors,
		plt.savefig(_filepath)
		plt.clf()  # Clear figure
		plt.close()
		#plt.show()

###############################################################################################################
###############################################################################################################
if __name__ == "__main__":
	pass