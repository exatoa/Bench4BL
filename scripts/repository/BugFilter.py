#-*- coding: utf-8 -*-
'''
Created on 2016. 11. 19
Updated on 2016. 01. 09
'''
from __future__ import print_function
import os
import shutil
import codecs
import cgi
import re
from dateutil import parser as dateparser
from commons import VersionUtil
from pytz import timezone
from utils import Progress
from bs4 import BeautifulSoup


class BugFilter:
	'''
	Extract bug reports to satisfy our criterions. bugitems = []
	bugItem = {
		'description':'',
		'id':'',
		'summary':'',
		'resolution':'',
		'opendate':'',
		'fixdate':'',
		'version':'',
		'fixVersion':'',
		'type':'',
		'links':[
			{'type':'', 'description':'', 'id':number}, ...
		]
	}
	'''
	__name__ = u'BugFilter'
	ProjectName = u''
	SourceBugPath = u''
	gitlogs = None
	gitversions = None

	def __init__(self, _projectName, _srcbugPath):
		self.__name__ = _projectName
		self.ProjectName = _projectName
		self.SourceBugPath = _srcbugPath

		pass

	@staticmethod
	def unhash_folder(_src, _dest):
		'''
		hashed folder ==> unshed folder
		example) path/aa/00/filename  ==> path/filename
		:param _src:
		:param _dest:
		:return:
		'''
		if os.path.exists(_dest) is False:
			os.makedirs(_dest)
		progress = Progress(u'Bug reports is merging', 20, 1000, False)
		progress.start()
		for root, dirs, files in os.walk(_src):
			for f in files:
				shutil.copy(os.path.join(root, f), os.path.join(_dest, f))
				progress.check()
		progress.done()

	# def show_versions(self, _bugitems):
	# 	for bug in _bugitems:
	# 		#if bug['id'] not in ['DATAREST-216', 'DATAREST-199']: continue
	# 		print(bug['id'] + u':' + bug['version'])
	#
	# 	print(u'\n\n\n\n\n\n')

	def run(self, _gitlogs, _gitversions, _removeTest=True, _onlyJava=True):
		self.gitlogs = _gitlogs
		self.gitversions = _gitversions
		bugitems = self.loads()
		bugitems = self.link_fixedFiles(bugitems)
		bugitems, dupgroups = self.make_dupgroups(bugitems)
		bugitems = self.filter(bugitems)
		bugitems.sort(self.cmp)  #fixed time order ASC
		self.make_minimumVersion(bugitems)
		return bugitems, dupgroups

	def loads(self):
		'''
		loads a raw file of bug report
		:return:
		'''
		fileConnt = self.getFileCounts(self.SourceBugPath)

		bugitems = []

		# show progress
		progress = Progress(u'[%s] Loading bug reports'%self.__name__, 2, 10, True)
		progress.set_upperbound(fileConnt)
		progress.start()
		for root, dirs, files in os.walk(self.SourceBugPath):
			for f in files:
				if f[:f.find(u'-')].strip().lower()  != self.ProjectName.lower(): continue
				#shutil.copy(os.path.join(root, f), os.path.join(_dest, f))
				bugitem = self.get_bugitem(os.path.join(root, f))
				if bugitem is not None:
					bugitems.append(bugitem)
				progress.check()
		progress.done()
		return bugitems

	def getFileCounts(self, _src):
		'''
		get the count of files
		:param _src:
		:return:
		'''
		count = 0
		files = os.listdir(_src)
		for aname in files:
			path = os.path.join(_src, aname)
			#stat_info = os.lstat(path)
			#if stat.S_ISDIR(stat_info.st_mode):
			if os.path.isdir(path):
				count += self.getFileCounts(path)
			else:
				count += 1
		return count

	def get_bugitem(self, _filepath):

		#read xml data
		fobj = codecs.open(_filepath, 'r', 'utf-8')
		xmltext = fobj.read()
		fobj.close()

		try:
			#extract information
			doc = BeautifulSoup(xmltext, 'html.parser')
			keys = ['description', 'key', 'summary', 'resolution', 'created', 'resolved', 'version', 'fixVersion', 'type']
			keymaps = ['description', 'id', 'summary', 'resolution', 'opendate', 'fixdate', 'version', 'fixVersion', 'type']
			bug = {}
			for idx in range(0, len(keymaps)):
				bug[keymaps[idx]] = u''

			for idx in range(0, len(keys)):
				findkey = 'item > ' + keys[idx].lower()
				items = doc.select(findkey )
				if len(items)==0: continue
				for item in items:
					bug[keymaps[idx]] += (u', ' if len(bug[keymaps[idx]]) > 0 else u'') + item.get_text()
			bug['fixedFiles'] = []

			#duplicate bug report
			bug['links'] = self.get_links(doc)

			# Convert some formats (date and text...)
			#re.sub = remove compound character except english caracter and numbers and some special characters
			bug['summary'] = cgi.escape(re.sub(r'[^\x00-\x80]+', '', bug['summary']))  #re.sub(r'[^\w\s&\^\|/()\[\]\{\}<>+\-=*/`~!@#$%^,.:;\\\'"?]', '', bug['summary']))
			bug['description'] = BeautifulSoup(bug['description'], "html.parser").get_text()
			bug['description'] = cgi.escape(re.sub(r'[^\x00-\x80]+', '', bug['description']))
			bug['description'] = cgi.escape(re.sub(chr(27), '', bug['description']))

			t = dateparser.parse(bug['opendate'])
			bug['opendate'] = t.astimezone(timezone('UTC'))
			#bug['opendate'] = dobj.strftime(u'%Y-%m-%d %H:%M:%S')

			if bug['fixdate'] != u'':
				t = dateparser.parse(bug['fixdate'])
				bug['fixdate'] = t.astimezone(timezone('UTC'))
			else:
				bug['fixdate'] = None
			#bug['fixdate'] = dobj.strftime(u'%Y-%m-%d %H:%M:%S')
		except Exception as e:
			print(e)
			return None
		return bug

	def get_links(self, _doc):
		'''
		extract links in bug report file.
		:param _doc:
		:return:
		'''
		links = []
		issuetypes = _doc.select('item > issuelinks > issuelinktype')
		for issuetype in issuetypes:
			name = issuetype.select('name')
			if len(name)<=0: continue
			typename = name[0].get_text()
			subtypes = issuetype.select('outwardlinks')
			for subtype in subtypes:
				keyvalues = subtype.select('issuekey')
				for keyvalue in keyvalues:
					key_id = keyvalue.get_text()
					key_id = key_id
					links.append({'type':typename, 'description':subtype['description'], 'id':key_id})

			subtypes = issuetype.select('inwardlinks')
			for subtype in subtypes:
				keyvalues = subtype.select('issuekey')
				for keyvalue in keyvalues:
					key_id = keyvalue.get_text()
					key_id = key_id[key_id.rfind(u'-')+1:]
					links.append({'type':typename, 'description':subtype['description'], 'id':key_id})
		return links

	def link_fixedFiles(self, _bugitems, _removeTest=True, _onlyJava=True):
		'''
		Mapping answer files with Bug reports and git Log
		The all related commit's files will be fixed files
		:param _bugitems:
		:return:
		'''
		for bug in _bugitems:
			if bug['id'] not in self.gitlogs:
				bug['fixedFiles'] = []
				bug['commits'] = []
				continue

			logs = self.gitlogs[bug['id']]	# get logs corresponding bug ID
			bug['commits'] = [] #[commit['hash'] for commit in logs]

			for log in logs:
				# log = [{'hash':u'', 'author':u'', 'commit_date':u'', 'message':u'', 'fixedFiles':{}}, ...]
				files = []
				for filename in log['fixedFiles']:
					if _onlyJava is True and filename.endswith('.java') is False: continue
					if _removeTest is True and filename.find('test') >= 0: continue
					if _removeTest is True and filename.find('Test') >= 0: continue

					changeType = log['fixedFiles'][filename]
					clsName = self.get_classname(filename)
					# check duplicate file
					existIDX = -1
					for idx in range(len(files)):
						if files[idx]['name'] == clsName:
							existIDX = idx
							break
					if existIDX == -1:
						files.append({'type':changeType, 'name':clsName})
					else:
						# override the value if old is M and new is D.
						if changeType == 'D':
							files[existIDX]['type'] = changeType
				if len(files) > 0:
					bug['commits'].append(log['hash'])
					bug['fixedFiles'] += files

		return _bugitems

	def get_classname(self, _filename):
		'''
		get class name from filepath
		:param _filename:
		:return:
		'''
		classname = _filename.replace(u'/', u'.')
		classname = classname.replace(u'\\', u'.')

		idx = classname.find(u'.org.')
		if idx > 0:
			classname = classname[idx+1:]

		return classname

	def make_dupgroups(self, _bugitems):
		'''
		identify duplicate bug reports,
		we return groups of dup-set like below:
			dupgroups = [{'src':ID, 'dest':ID, 'fixedboth:True /False}, ...]
		if a bug report have fixedFiles, the report will be the master report.
		if the two duplicate reports have fixedFiles both, the low id report will be a master report and
		the fixedboth field will be set True.
		:param _bugitems:
		:return:
		'''
		dupgroups = []
		visited = set([])

		for x in range(len(_bugitems)):
			src = _bugitems[x]
			worklist = []
			# find duplicate from all links, and add dup-groups
			for link in src['links']:
				# filter unrelated items
				if link['type'].lower() != 'duplicate': continue
				project = link['id'][:link['id'].find('-')].strip()
				if project != self.ProjectName:continue
				if src['id'] in visited and link['id'] in visited: continue

				# find dest data
				for y in range(len(_bugitems)):
					if x == y:continue
					if _bugitems[y]['id'] != link['id']: continue
					dest = _bugitems[y]
					break

				# add worklist
				if len(src['fixedFiles'])>0 and len(dest['fixedFiles'])>0:
					if (src['id'][src['id'].find('-')+1:] <= dest['id'][dest['id'].find('-')+1:]):
						worklist.append((src, dest, True))
					else:
						worklist.append((dest, src, True))
				elif len(src['fixedFiles'])>0 and len(dest['fixedFiles'])==0:
					worklist.append((src, dest, False))
				elif len(src['fixedFiles'])==0 and len(dest['fixedFiles'])>0:
					worklist.append((dest, src, False))

			# append dupgroups and auxiliary works
			for src, dest, both in worklist:
				visited.add(src['id'])
				visited.add(dest['id'])
				self.complement_reports(src, dest, both)
				dupgroups.append({'src':src['id'], 'dest':dest['id'], 'fixedboth':both})
		return _bugitems, dupgroups

	def complement_reports(self, _src, _dest, _both):
		'''
		complement information from the duplicate bug report
		:param _bugitems:
		:param _dupgroups:
		:param _gitversions:
		:return:
		'''
		# sync fixedfile
		if _both is False:
			if len(_src['fixedFiles']) == 0:
				_src['fixedFiles'] = _dest['fixedFiles']
				_src['commits'] = _dest['commits']
			else:
				_dest['fixedFiles'] = _src['fixedFiles']
				_dest['commits'] = _src['commits']

		# sync version
		if _dest['version'] != u''and _src['version'] == u'':
			_src['version'] = _dest['version']
		elif _src['version'] != u'' and _dest['version'] == u'':
			_dest['version'] = _src['version']
		elif _src['version'] == u'' and _dest['version'] == u'':
			#if both report has no version, get version information from git repository
			v1 = self.get_gitversion(_src['id'])
			v2 = self.get_gitversion(_dest['id'])
			if v1!=u'' and v2 != u'':
				if v1==u'': _src['version'] = v2
				_src['version'] = v1 if VersionUtil.cmpVersion(v1, v2) <0 else v2
				_dest['version'] = _src['version']

		# sync fixdate
		if _dest['fixdate'] != u''and _src['fixdate'] == u'':
			_src['fixdate'] = _dest['fixdate']
		if _src['fixdate'] != u'' and _dest['fixdate'] == u'':
			_src['fixdate'] = _src['fixdate']

		pass

	def get_gitversion(self, _id):
		'''
		get bug version information from git repository
		:param _id:
		:return:
		'''
		if _id not in self.gitlogs: return u''

		min_version = u''
		commits = self.gitlogs[_id]
		for commit in commits:
			if commit['hash'] not in self.gitversions: continue
			version = self.gitversions[commit['hash']]
			if version is None: continue
			if min_version == u'': min_version = version
			if VersionUtil.cmpVersion(version, min_version) < 0:
				min_version = version

		return min_version

	def filter(self, _bugitems):
		'''
		Remove bug reports that is not satisfied the criteria from the _bugitems
		:param _bugitems: list of bug reports
		:return:
		'''
		noFileCount = 0
		noDateCount = 0
		noVersionCount = 0
		onlyVersionCount = 0
		removedCount = 0
		newlist = []

		for bug in _bugitems:
			flagVersion=True
			flagFiles=True
			flagDate=True

			if bug['version'].strip()  ==u'':
				noVersionCount += 1
				flagVersion = False
			if len(bug['fixedFiles']) == 0:
				noFileCount += 1
				flagFiles=False
			if bug['fixdate'] is None:
				noDateCount += 1
				flagDate=False

			if flagDate is False or flagFiles is False or flagVersion is False:
				removedCount += 1
				if flagVersion is False and flagDate is True and flagFiles is True:
					onlyVersionCount += 1
				continue

			# we already filtered this types of bug
			# if not (
			# 	(bug['type'].lower() == 'bug' and bug['resolution'].lower() == 'fixed')
			# 	or bug['resolution'].lower() =='duplicate'
			# ): continue

			newlist.append(bug)
		print(u'[%s] Filter : %d fixedFiles, %d version, %d fixdate. :: %d/%d only no versions'% (	self.__name__,
																								noFileCount,
																								noVersionCount,
																								noDateCount,
																								onlyVersionCount,
																								removedCount))
		print(u'[%s] Filter : %d remained list.'% (self.__name__, len(newlist)))
		return newlist

	def make_minimumVersion(self, _bugs):
		for bug in _bugs:
			min_version = u'10000.0' # assign big version
			for version in bug['version'].split(u', '):
				if VersionUtil.cmpVersion(version, min_version) < 0:

					min_version = version
			bug['version'] = min_version
		pass

	def cmp(self, x, y):
		if x['fixdate'] < y['fixdate'] :
			return -1
		elif x['fixdate'] > y['fixdate']:
			return 1
		return 0
