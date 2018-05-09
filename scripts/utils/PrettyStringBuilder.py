#-*- coding: utf-8 -*-
from __future__ import print_function


class PrettyStringBuilder(object):
	accuracy = 2
	indent_depth = 3
	point_level = 0

	def __init__(self, _indent_depth=3, _accuracy=2, _point_level=0):
		self.accuracy = _accuracy
		self.indent_depth = _indent_depth
		self.point_level = _point_level
		pass

	def toString(self, _item):
		return self.get_itemtext(_item)

	def get_itemtext(self, _item, _indent=0):
		if isinstance(_item, dict):
			return self.get_dicttext(_item, _indent)
		elif isinstance(_item, list):
			return self.get_listtext(_item, _indent)
		elif isinstance(_item, set):
			return self.get_listtext(_item, _indent)
		elif isinstance(_item, str):
			return u'"%s"' % self.escape(_item)
		elif isinstance(_item, unicode):
			return u'"%s"' % self.escape(_item)
		elif isinstance(_item, float):
			return str(_item) if self.accuracy <= 0 else ((u'%%.%df' % (self.accuracy)) % _item)
		elif isinstance(_item, int):
			return str(_item) if self.point_level <= 0 else self.get_integer(_item)
		elif isinstance(_item, long):
			return str(_item) if self.point_level <= 0 else self.get_integer(_item)
		else:
			return u'%s' % str(_item)

	def get_integer(self, _value):
		text = str(_value)
		count = 0
		position = self.point_level + count
		while True:
			if len(text) > position:
				text = text[:-position] + u',' + text[-position:]
				count+=1
				position += self.point_level + count
			else:
				break
		return text

	def get_listtext(self, _items, _indent=0):
		'''
		make list text
		:param _items:
		:param _indent:
		:return:
		'''
		if len(_items) == 0: return u'[]'

		# make list opener
		text = u'['

		# make dict items text
		for value in _items:
			text += u'%s%s, ' % (
				u'' if _indent >= self.indent_depth else (u'\n' + u'\t' * (_indent + 1)),
				self.get_itemtext(value, _indent + 1)
			)

		# make dict closer
		text = text.strip()
		if text.endswith(','): text = text[:-1]
		text += u'%s]' % (u'' if _indent >= self.indent_depth else (u'\n' + u'\t' * _indent))

		return text

	def get_dicttext(self, _item, _indent=0):
		'''
		make dictionary text
		:param _item:
		:param _indent:
		:return:
		'''
		if len(_item) == 0:	return u'{}'

		# make dict opener
		text = u'{'

		# make dict items text
		for key, value in _item.iteritems():
			text += u'%s%s:%s, '% (
				u'' if _indent >= self.indent_depth else (u'\n' + u'\t' * (_indent+1)),
				self.get_keytext(key),
				self.get_itemtext(value, _indent+1)
			)

		# make dict closer
		text = text.strip()
		if text.endswith(','): text = text[:-1]
		text += u'%s}' % (u'' if _indent >= self.indent_depth else (u'\n' + u'\t'*_indent))

		return text

	def get_keytext(self, _key):
		'''
		return text for dictionary key
		:param _key:
		:return:
		'''
		if isinstance(_key, unicode):
			return u'\"%s\"' % _key
		elif isinstance(_key, str):
			return u'\"%s\"' % _key
		elif isinstance(_key, int):
			return u'%d' % _key
		elif isinstance(_key, long):
			return u'%d' % _key
		elif isinstance(_key, float):
			return u'%.f' % (_key if self.accuracy is None else (u'%%.%df' % self.accuracy) % _key)
		else:
			return u'\'%s\'' % _key.__hash__()

	def escape(self, text):
		return text.replace('\\', '\\\\').replace('\n', '\\n').replace('\r', '\\r').replace('\"', '\\"')
