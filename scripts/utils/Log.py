#-*- coding: utf-8 -*-
from __future__ import print_function
import logging
import os

class Log(object):
	logger = None
	formatter = None

	DEBUG = logging.DEBUG
	INFO = logging.INFO
	WARNING = logging.WARNING
	CRITICAL = logging.CRITICAL

	@staticmethod
	def init(_name, _level=logging.DEBUG, _format=None):
		# create formatter and add it to the handlers
		if _format is None:
			Log.formatter = logging.Formatter('%(asctime)s [%(levelname)s] %(name)s : %(message)s')
		else:
			Log.formatter = logging.Formatter(_format)

		# create logger object
		Log.logger = logging.getLogger(_name)
		Log.logger.setLevel(_level)
		return Log

	@staticmethod
	def set_formatter(_format):
		Log.formatter = logging.Formatter(_format)
		return Log


	@staticmethod
	def add_ConsoleHandler(_level=logging.INFO):
		# create console handler with a higher log level
		ch = logging.StreamHandler()
		ch.setLevel(_level)
		ch.setFormatter(Log.formatter)
		Log.logger.addHandler(ch)   # add the handlers to logger
		return Log

	@staticmethod
	def add_FileHandler(_filepath, _level=logging.INFO):

		try:
			# check path and file name.
			idx = _filepath.rfind(u'\\')
			idx2 = _filepath.rfind(u'/')
			if idx < idx2 :
				idx = idx2
			path = _filepath[:idx+1]
			if os.path.exists(path) is False:
				os.makedirs(path)

			# create file handler
			fh = logging.FileHandler(_filepath)
			fh.setLevel(_level)
			fh.setFormatter(Log.formatter)
			Log.logger.addHandler(fh)
		except:
			print('Error occured to create log file. Only use command logging')

		return Log

	@staticmethod
	def out(_level, _msg):
		if _level==logging.DEBUG:
			Log.logger.debug(_msg)
		elif _level==logging.WARNING:
			Log.logger.warn(_msg)
		elif _level==logging.CRITICAL:
			Log.logger.critical(_msg)
		else: 
			Log.logger.info(_msg)
		pass

