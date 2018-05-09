#-*- coding: utf-8 -*-
'''
Created on 2017. 03. 15
Updated on 2017. 03. 15
'''
from __future__ import print_function
import re


class DataLoader(object):
	__name__ = u'DataLoader'

	def load_item_wordfrequency(self, _filename):
		'''
		load Term-Frequency File
		:param _filename:
		:return: {itemID:{term1:count2, term2:count2, ....}, ...}
		'''
		data = {}
		f = open(_filename, 'r')
		titles = f.readline()
		while True:
			line = f.readline()
			if line is None or len(line) == 0: break

			group, project, type, item, term, count = line[:-1].split('\t')

			if item not in data: data[item] = {}
			data[item][term] = int(count) if isinstance(count, int) else float(count)
		f.close()
		return data

	def load_wordfrequency(self, _filename):
		'''
		Load a type of files: idf, ptf, tfidf
		:param _filename:
		:return: {term1:count2, term2:count2, ....}
		'''
		data = {}
		f = open(_filename, 'r')
		title = f.readline()
		while True:
			line = f.readline()
			if line is None or len(line) == 0: break
			group, project, type, term, count = line[:-1].split('\t')
			data[term] = int(count) if isinstance(count, int) else float(count)
		f.close()
		return data

	def load_words_in_frequency(self, _filename):
		'''
		Load only word list from the type of files: idf, ptf, tfidf
		:param _filename:
		:return: [term1, term2, ...]
		'''
		data = []
		f = open(_filename, 'r')
		title = f.readline()
		while True:
			line = f.readline()
			if line is None or len(line) == 0: break
			group, project, type, term, count = line[:-1].split('\t')
			data.append(term)
		f.close()
		return data

	def load_itemwords(self, _filename):
		'''
		Load only term list from TF file.
		:param _filename:
		:return:
		'''
		data = {}
		f = open(_filename, 'r')
		title = f.readline()
		while True:
			line = f.readline()
			if line is None or len(line) == 0: break
			group, project, type, item, term, count = line[:-1].split('\t')

			if item not in data: data[item] = []
			data[item].append(term)
		f.close()
		return data

	def load_words(self, _filepath):
		'''
		Load terms from term list file.
		:param _filepath:
		:return:
		'''
		f = open(_filepath, 'r')
		words = f.readlines()
		f.close()

		for x in range(len(words)):
			words[x] = words[x][:-1]
		return words

	def load_bug_feateures(self, _filepath):
		f = open(_filepath, 'r')
		text = f.read()
		f.close()

		data = eval(text)
		values = {'Enumeration': False, 'StackTrace': False, 'CodeEntity': False, 'CodeEntities': set([]), 'SourceCodeRegion': False}

		if 'enums' in data and len(data['enums']) != 0: values['Enumeration'] = True
		if 'traces' in data and len(data['traces']) != 0: values['StackTrace'] = True
		if 'source' in data and len(data['source']) != 0: values['SourceCodeRegion'] = True
		if 'DescHints' in data and 'SummaryHints' in data:
			entities = set(data['DescHints']) | set(data['SummaryHints'])
			if len(entities) != 0:
				values['CodeEntity'] = True
				values['CodeEntities'] = entities
		else:
			print('Error')
		return values
