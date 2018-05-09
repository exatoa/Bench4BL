#-*- coding: utf-8 -*-
'''
Created on 2017. 02. 16
Updated on 2017. 02. 16
To run this program, you need to do following setps.
   - preparing each techniques
   - download Git reporitory :: use launcher_GitInflator.py
   - make bug repository :: use launcher_repoMaker.py
   - make summary information files :: use ExecuteTools/executor/Counting.py
'''

from __future__ import print_function
# Path Appended :: to execute in shell
import os
# import sys
# sys.path.append(os.getcwd())    # add the executed path to system PATH
# sys.path.append(u'/var/experiments/BugLocalization/dist/scripts/')

from commons import Subjects
from commons import Previous
from commons import VersionUtil
import subprocess
from datetime import datetime


class Launcher(object):
	'''
	IRBL Techniques Launcher class
	'''

	ProgramNames = [u'BugLocator',  u'BRTracer', u'BLUiR', u'AmaLgam',  u'BLIA', u'Locus']
	ProgramPATH = u'/mnt/exp/Bug/techniques/releases/'
	OutputPATH = u'/mnt/exp/Bug/expresults/'
	JavaOptions = u'-Xms512m -Xmx4000m'
	JavaOptions_Locus = u'-Xms512m -Xmx8000m'
	TYPE = u'Test'

	def __init__(self):
		self.S = Subjects()
		if os.path.exists(os.path.join(self.ProgramPATH, 'logs')) is False:
			os.makedirs(os.path.join(self.ProgramPATH, 'logs'))


		t = datetime.now()
		timestr = t.strftime(u'%y%m%d_%H%M')
		self.logFileName = u'logs_%s'%timestr + u'_%s.txt'

	def finalize(self):
		self.log.close()
		pass

	def createArguments(self, _params):
		if isinstance(_params, str) is True:
			return _params
		if isinstance(_params, unicode) is True:
			return _params

		paramsText = u''
		for key, value in _params.iteritems():
			if key == 'v':
				if value is True: paramsText += u' -v'
			else:
				if value is None or value == '': continue
				paramsText += u' -%s %s' % (key, value)
		return paramsText

	def executeJava(self, _program, _params, _cwd=None,  _project=None, _vname=None):
		if _cwd is None: _cwd = self.ProgramPATH
		options = self.JavaOptions if _program != 'Locus' else self.JavaOptions_Locus
		command = u'java %s -jar %s%s.jar %s' % (options, self.ProgramPATH, _program, self.createArguments(_params))
		commands = command.split(u' ')

		t = datetime.now()
		timestr = t.strftime(u'Strat:%Y/%m/%d %H:%M')
		print(u'\n\n[%s]\nCMD:: %s' % (timestr, command))
		#sys.stdout.write(u'\n\nCMD:: %s\n' % command)
		self.log.write('\n\n[%s]\nCMD:: %s\n' % (timestr, command))
		if _program == 'BLIA':
			self.log.write('working with %s / %s\n' % ( _project, _vname))
		try:
			#subprocess.call(commands, cwd=_cwd, shell=False) #, stdout=self.log, stderr=self.log)
			p = subprocess.Popen(commands, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, shell=False, cwd=_cwd)
			while True:
				line = p.stdout.readline()
				if line != '':
					# the real code does filtering here
					print (line.rstrip())
					self.log.write(line.rstrip()+u'\n')
					self.log.flush()
				else:
					break

		except Exception as e:
			print(e)
			return None
		return 'Success'

	def get_params(self, _program, _group, _project, _alpha, _version=None, _isUnion=False, _useMerge=False):
		bugrepo = self.S.getPath_bugrepo(_group, _project)
		params = {
			'n': u'%s_%s' % (_project, _version if _isUnion is False else u'all'),
			's': self.S.getPath_source(_group, _project, _version),  # source path
			'b': os.path.join(bugrepo,u'repository%s.xml' % (u'' if _isUnion is True else u'/%s' % _version)),  # base bug path
			'w': os.path.join(self.OutputPATH, self.TYPE, _group, _project)+u'/',  # result path
			'a': _alpha,  # alpha parameter
		}

		if _useMerge is True:
			params['b'] = os.path.join(bugrepo,u'repository_merge%s.xml' % (u'' if _isUnion is True else u'/%s' % _version))

		if _program in ['AmaLgam', 'BLIA', 'Locus']:
			params['g'] = self.S.getPath_gitrepo(_group, _project)  # git repo.

		if _program == 'BLIA':
			params = self.save_BLIA_config(_project, params, _version if _isUnion is False else u'all')

		if _program == 'Locus':
			params = self.save_Locus_config(_project, params, _version if _isUnion is False else u'all')

		return params

	def get_paramsDist(self, _program, _group, _project, _alpha, _version, _codeVersion):
		params = {
			'n': u'%s_%s' % (_project, u'all'),
			's': self.S.getPath_source(_group, _project, _codeVersion),  # source path
			'b': os.path.join(self.S.getPath_bugrepo(_group, _project),u'repository/%s.xml' % _version),  # base bug path
			'w': os.path.join(self.OutputPATH, self.TYPE, _group, _project)+u'/',  # result path
			'a': _alpha,  # alpha parameter
		}
		if _program in ['AmaLgam', 'BLIA', 'Locus']:
			params['g'] = self.S.getPath_gitrepo(_group, _project)  # git repo.

		path = os.path.join(params['w'], u'%s_%s'%(_program, params['n']))
		if os.path.exists(path) is True:
			for file in os.listdir(path):
				if file =='revisions': continue
				if file == 'bugText': continue
				if file == 'hunkCode.txt': continue
				if file == 'hunkLog.txt': continue
				if file == 'hunkIndex.txt': continue
				if file == 'hunkCLTIndex.txt': continue
				if file == 'commitCLTINdex.txt': continue
				if file == 'sourceHunkLink.txt': continue
				if file == 'logOneline.txt': continue
				if file == 'concernedCommits.txt': continue
				os.remove(os.path.join(path, file))

		if _program == 'BLIA':
			params = self.save_BLIA_config(_project, params, _version)

		if _program == 'Locus':
			params = self.save_Locus_config(_project, params, 'all', _isAppend=True)

		return params

	def get_paramsOLD(self, _program, _group, _project, _alpha, _version):
		params = {
			'n': u'%s_all' % _project,
			's': self.S.getPath_source(_group, _project, _version),  # source path
			'b': os.path.join(self.S.getPath_bugrepo(_group, _project), u'repository.xml'),  # base bug path
			'w': os.path.join(self.OutputPATH, self.TYPE, _group, _project)+u'/',  # result path
			'a': _alpha,  # alpha parameter
		}
		if _program in ['AmaLgam', 'BLIA', 'Locus']:
			params['g'] = self.S.getPath_gitrepo(_group, _project)  # git repo.

		if ((_program == 'BLIA' and _project not in ['PDE', 'JDT']) or
			    (_program == 'Locus' and _project == 'AspectJ')):
			params['b'] = os.path.join(self.S.getPath_bugrepo(_group, _project), u'%s_repository.xml' % _program)
		else:
			params['b'] = os.path.join(self.S.getPath_bugrepo(_group, _project), u'repository.xml')

		if _program == 'BLIA':
			params = self.save_BLIA_config(_project, params, u'all')

		if _program == 'Locus':
			params = self.save_Locus_config(_project, params, u'all')

		return params

	def save_BLIA_config(self, _project, _params, _versionName):
		alpha = 0.2
		beta = 0.2
		pastDays = 15

		filename = os.path.join(self.ProgramPATH, u'blia_properties', u'%s.properties' % _project)
		f = open(filename, 'w')
		f.write('#Target product to run BLIA\n')
		f.write('TARGET_PRODUCT=' + _project + '\n')
		f.write('\n')
		f.write('# Execution configurations\n')
		f.write('WORK_DIR=' + _params['w'] + '\n')
		f.write('THREAD_COUNT=10' + '\n')
		f.write('\n')
		f.write('# For ' + _project + '\n')
		f.write('PRODUCT=' + _project + '\n')
		f.write('VERSION=' + _versionName + '\n')
		f.write('SOURCE_DIR=' + _params['s'] + '\n')
		f.write('ALPHA=' + str(alpha) + '\n')
		f.write('BETA=' + str(beta) + '\n')
		f.write('PAST_DAYS=' + str(pastDays) + '\n')
		f.write('REPO_DIR=' + _params['g'] + '/.git' + '\n')
		f.write('BUG_REPO_FILE=' + _params['b'] + '\n')
		f.write('COMMIT_SINCE=1990-04-01\n')
		f.write('COMMIT_UNTIL=2016-11-30\n')
		f.write('CANDIDATE_LIMIT_RATE=0.1\n')
		f.close()

		return filename

	def save_Locus_config(self, _project, _params, _versionName, _isAppend=False):
		filename = os.path.join(self.ProgramPATH, u'locus_properties', u'%s_config.txt' % _versionName)
		f = open(filename, 'w')
		f.write('task=fileall\n')
		f.write('Project=' + _project +'\n')
		f.write('Version=' + _versionName +'\n')
		if _isAppend is True:
			f.write('MODE=append\n')
		f.write('repoDir=' + _params['g'] +'\n')
		f.write('sourceDir=' + _params['s'] + '\n')
		f.write('workingLoc='+ _params['w'] +'\n')
		f.write('bugReport=' + _params['b'] + '\n')
		f.write('changeOracle='+_params['w'] + '\n')
		f.close()

		return filename

	def run(self, _type, _sGroup=None, _sProject=None,  _sProgram=None, _sVersion=None, _isUnion=False, _isDist=False, _useMerge=False):
		self.TYPE = _type
		nameTag = self.TYPE + (u'_%s'%_sGroup if _sGroup is not None else u'') + (u'_%s'%_sProject if _sProject is not None else u'') + (u'_%s'%_sVersion if _sVersion is not None else u'') + (u'_%s'%_sProgram if _sProgram is not None else u'')
		self.log = open(os.path.join(self.ProgramPATH, 'logs', self.logFileName%(nameTag)), 'w')

		# select target subjects or select all.
		for program in (self.ProgramNames if _sProgram is None else [_sProgram]):
			for group in (self.S.groups if _sGroup is None else [_sGroup]):
				for project in (self.S.projects[group] if _sProject is None else [_sProject]):
					#working selected subject and program.
					versions = self.S.bugs[project].keys()
					if _isDist is True:
						codeVersion = VersionUtil.get_latest_version(self.S.bugs[project].keys())
						for verName in (versions if _sVersion is None else [_sVersion]):
							if verName == 'all': continue
							params = self.get_paramsDist(program, group, project, 0.2, verName, codeVersion)
							self.executeJava(program+u'_dist', params, _project=project, _vname =verName)

					elif _isUnion is True:
						# if the self.S.version[project] uses, the error occurs because there are versions with no bug report
						verName = VersionUtil.get_latest_version(self.S.bugs[project].keys())
						params = self.get_params(program, group, project, 0.2, verName, _isUnion)
						self.executeJava(program, params, _project=project, _vname =verName)
					else:
						# In the version is not single case,
						for verName in (versions if _sVersion is None else [_sVersion]):
							if verName == 'all': continue
							if _useMerge is True:
								if verName not in self.S.answers_merge[project]: continue
							# if the self.S.version[project] uses, the error occurs because there are versions with no bug report
							outputFile = os.path.join(self.OutputPATH, _type, group, project, u'%s_%s_%s_output.txt'%(program, project, verName))
							# if os.path.exists(outputFile) is True:
							# 	print(u'Already exists :: %s '% outputFile)
							# 	continue
							params = self.get_params(program, group, project, 0.2, verName, _isUnion, _useMerge)
							self.executeJava(program, params, _project=project, _vname =verName)
						# for version
				# for program
			# for project
		#for group
		t = datetime.now()
		timestr = t.strftime(u'Done:%Y/%m/%d %H:%M')
		print(u'\n\n[%s]' % timestr)
		self.log.write('\n\n[%s]' % timestr)

	def runOLD(self, _type, _sGroup=None, _sProject=None, _sProgram=None):
		self.TYPE = _type
		nameTag = self.TYPE + (u'_%s' % _sGroup if _sGroup is not None else u'') + (u'_%s' % _sProject if _sProject is not None else u'')
		self.log = open(os.path.join(self.ProgramPATH, 'logs', self.logFileName%nameTag), 'w')

		self.S = Previous()
		for program in self.ProgramNames:
			if _sProgram is not None and program != _sProgram: continue

			for group in self.S.groups:
				if _sGroup is not None and group != _sGroup: continue

				for project in self.S.projects[group]:
					if _sProject is not None and project != _sProject: continue

					maxVersion = self.S.get_max_versions(program, project)
					versionName = u'%s_%s' % (project, VersionUtil.get_versionName(maxVersion))
					alpha = 0.2 if not (program == 'BugLocator' and project == 'AspectJ') else 0.3
					params = self.get_paramsOLD(program, group, project, alpha, versionName)

					#print(u'java %s -jar %s%s.jar ' % (self.JavaOptions, self.ProgramPATH, program) + self.createArguments(params))
					self.executeJava(program, params, _project=project, _vname =versionName)

		t = datetime.now()
		timestr = t.strftime(u'Done:%Y/%m/%d %H:%M')
		print(u'\n\n[%s]' % timestr)
		self.log.write('\n\n[%s]' % timestr)
		pass

##################################################################################################
##################################################################################################

def getargs():
	import argparse
	parser = argparse.ArgumentParser(description='')
	parser.add_argument('-p', dest='project', default=None, help='A specific project name what you want to work.')
	parser.add_argument('-g', dest='group', default=None, help='A specific group name what you want to work.')
	parser.add_argument('-t', dest='technique', default=None, help='A specific technique name what you want to work.')
	parser.add_argument('-v', dest='version', default=None, help='A specific version name what you want to work.')
	parser.add_argument('-w', dest='workType', default=None, help='workType : PreviousData or not. other case is NewData')
	parser.add_argument('-s', dest='isSingle', default=False, type=bool, help='use latest source code for all bug report')
	parser.add_argument('-d', dest='isDist', default=False, type=bool, help='use the multiple bug repository and the single source code')
	parser.add_argument('-m', dest='useMerge', default=False, type=bool, help='use merged bug reporitory')

	args = parser.parse_args()

	if args.workType is None:
		parser.print_help()
		return None
	return args

if __name__ == '__main__':
	import sys

	args = getargs()
	if args is None:
		exit(1)

	obj = Launcher()
	if args.workType.startswith('PreviousData'):
		obj.runOLD(args.workType, _sGroup=args.group, _sProject=args.project, _sProgram=args.technique)

	else:
		obj.run(args.workType, _sGroup=args.group, _sProject=args.project, _sVersion=args.version,
				_sProgram=args.technique, _isUnion=args.isSingle, _isDist=args.isDist, _useMerge = args.useMerge)

	obj.finalize()
