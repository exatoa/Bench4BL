#-*- coding: utf-8 -*-
from __future__ import print_function
import urllib2
import hashlib
import os

###############################################################################################################
class HashPath(object):
	@staticmethod
	def sequence(_name, _name_size, _level):
		'''
		:param _path:
		:param _name_size:
		:param _level:
		:return:
		'''
		# check _name_size
		if _name_size <= 0:
			return _name

		bucket = u''
		for i in range(0, _level):
			sub = _name[(i*_name_size):((i+1)*_name_size)]
			if sub == u'':
				sub = u'0'*_name_size
			bucket += sub +u'\\'

		return bucket

	@staticmethod
	def encode(_path, _name_size, _level):
		'''
		create HASH path using last filename in give path
		ex)   /home/user/2d9e014f.txt
				==> /home/user/2d/9e/01/2d92014f.txt   (when level==3, namesize==2)
				==> /home/user/2d9/e01/2d92014f.txt   (when level==2, namesize==3)
		:param _path:
		:param _name_size:
		:param _level:
		:return:
		'''
		# check _name_size
		if _name_size <= 0:
			return _path

		idx = _path.rfind(u'\\')
		idx2 = _path.rfind(u'/')
		if idx < idx2: idx = idx2

		if idx == -1:
			parent = ''
			filename = _path[:]
		else:
			parent = _path[0:(idx+1)]
			filename = _path[(idx+1):]

		bucket = u''
		for i in range(0, _level):
			sub = filename[(i*_name_size):((i+1)*_name_size)]
			if sub == u'':
				sub = u'0' * _name_size
			bucket += sub + os.path.sep
			
		return parent + bucket + filename

	@staticmethod
	def split(_txt, _split, _count):
		'''
		text split spicified count, if _txt has no delimiter, it will be None.
		:param _txt:
		:param _split:
		:param _count:
		:return:
		'''
		result = ()
		while _count > 0:
			idx = _txt.index(_split)
			if idx > 0:
				part = _txt[(idx+1):]
				_txt = _txt[0:idx]
			else:
				part = None

			result += (part,)
			_count -= 1

		return result

	@staticmethod
	def url_to_path(_url, _only_host=False):
		'''
		make a relative path using url (domain\\path\\hashinfo_from_url)
		:param _url:
		:return:
		'''
		if _url is None:
			return None

		#make Hash Filename
		m = hashlib.md5()
		m.update(_url.encode('utf-8'))
		filename = m.hexdigest()

		#seperate url and make a path (\\domain\\path\\)
		req = urllib2.Request(_url)
		host = req.get_host()  # domain:port
		host = host.replace(u':', u'#')  # change port delimiter

		if _only_host is False:
			path = req.get_selector()	   # choose selector after domain.
			path = path.split(u'?')[0]	  # remove parameter

			path = path.replace(os.path.altsep, os.path.sep)   # altsep --> sep
			if path.startswith(os.path.sep) is True:
				path = path[1:]
			host = os.path.join(host, path)

		filepath = os.path.join(host ,filename)
		return filepath

	@staticmethod
	def expend_hashpath(_path, _level, _name_size):
		'''
		make a relative path (parent\\hash_path\\filename)
		:param _url:
		:return:
		'''
		if _path is None:
			return None

		#seperate parent and filename
		idx = _path.rfind(u'\\')
		idx2 = _path.rfind(u'/')
		if idx<idx2 : idx = idx2
		if idx < 0: return None
		#parent = os.path.abs(os.path.join(_path, os.pardir))
		parent = _path[:(idx+1)]
		filename = _path[(idx+1):]

		#make Hash Filename
		m = hashlib.md5()
		m.update(filename)
		hexfilename = m.hexdigest()

		bucket = u''
		for i in range(0, _level):
			sub = hexfilename[(i*_name_size):((i+1)*_name_size)]
			if sub == u'':
				sub = u'0'*_name_size
			bucket += sub + os.path.sep

		return os.path.join(parent, bucket, filename)

	@staticmethod
	def reduce_hashpath(_path, _level, _name_size):
		'''
		remove a hash path (parent\\[hash_path\\]filename)
		:param _url:
		:return:
		'''
		if _path is None:
			return None

		#seperate parent and filename
		idx = _path.rfind(u'\\')
		idx2 = _path.rfind(u'/')
		if idx<idx2 : idx = idx2
		filename = _path[(idx+1):]
		parent = _path[:idx]

		#reduce
		while _level>0:
			idx = parent.rfind(u'\\')
			idx2 = parent.rfind(u'/')
			if idx<idx2 : idx = idx2
			parent = parent [:idx]
			_level -= 1
		return os.path.join(parent, filename)

###############################################################################################################
###############################################################################################################
###############################################################################################################
if __name__ == '__main__':
	path = HashPath.expend_hashpath(u'/home/user/bug/temp/file.txt', 2, 2)
	print(HashPath.reduce_hashpath(path, 2,2,))

