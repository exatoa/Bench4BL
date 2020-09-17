#*- coding: utf-8 -*-
'''
Created on 2017. 02. 16
Updated on 2017. 02. 16
'''
from __future__ import print_function
import os


class Subjects(object):
	'''
	Collecting Subjects information
	Sourcecode, Bug reports, Duplicate bug reports
	'''
	__name__ = u'Subjects'
	root = u'/home/ubuntu/Bench4BL/data/'
	root_result = u'/home/ubuntu/Bench4BL/expresults/'
	techniques = ['BugLocator', 'BRTracer', 'BLUiR', 'AmaLgam', 'BLIA', 'Locus']
	groups = ['Apache']#, 'Commons', 'JBoss', 'Wildfly', 'Spring']
	projects = {
		'Apache':[u'HIVE'],
		# 'Apache':[u'CAMEL', u'HBASE', u'HIVE'],
		# 'Commons':[u'CODEC', u'COLLECTIONS', u'COMPRESS', u'CONFIGURATION', u'CRYPTO', u'IO', u'LANG', u'MATH', u'WEAVER',u'CSV'],
		# 'JBoss':[u'ENTESB', u'JBMETA'],
		# 'Wildfly':[u'ELY', u'WFARQ', u'WFCORE', u'WFLY', u'WFMP',u'SWARM'],
		# 'Spring':[U'AMQP', U'ANDROID', U'BATCH', U'BATCHADM', U'DATACMNS', U'DATAGRAPH', U'DATAJPA', U'DATAMONGO', U'DATAREDIS', U'DATAREST', U'LDAP', U'MOBILE', U'ROO', U'SEC', U'SECOAUTH', U'SGF', U'SHDP', U'SHL', U'SOCIAL', U'SOCIALFB', U'SOCIALLI', U'SOCIALTW', U'SPR', U'SWF', U'SWS']
	}

	urls = {
		'HBASE': u'https://github.com/apache/hbase.git',
		'HIVE': u'https://github.com/apache/hive.git',
		'CAMEL': u'https://github.com/apache/camel.git',

		'MATH': u'https://github.com/apache/commons-math.git',
		'LANG': u'https://github.com/apache/commons-lang.git',
		'IO': u'https://github.com/apache/commons-io.git',
		'COLLECTIONS': u'https://github.com/apache/commons-collections.git',
		'CODEC': u'https://github.com/apache/commons-codec.git',
		'WEAVER': u'https://github.com/apache/commons-weaver.git',
		'CONFIGURATION': u'https://github.com/apache/commons-configuration.git',
		'CSV': u'https://github.com/apache/commons-csv.git',
		'COMPRESS': u'https://github.com/apache/commons-compress.git',
		'CRYPTO': u'https://github.com/apache/commons-crypto.git',

		'ENTESB': u'https://github.com/jboss-fuse/fuse.git',
		'JBMETA': u'https://github.com/jboss/metadata.git',

		'ELY': u'https://github.com/wildfly-security/wildfly-elytron.git',
		'SWARM': u'https://github.com/wildfly-swarm/wildfly-swarm.git',
		'WFARQ': u'https://github.com/wildfly/wildfly-arquillian.git',
		'WFCORE': u'https://github.com/wildfly/wildfly-core.git',
		'WFLY': u'https://github.com/wildfly/wildfly.git',
		'WFMP': u'https://github.com/wildfly/wildfly-maven-plugin.git',

		'ANDROID': u'https://github.com/spring-projects/spring-android',
		'AMQP': u'https://github.com/spring-projects/spring-amqp',
		'BATCH': u'https://github.com/spring-projects/spring-batch',
		'BATCHADM': u'https://github.com/spring-projects/spring-batch-admin',
		'DATACMNS': u'https://github.com/spring-projects/spring-data-commons',
		'DATAJPA': u'https://github.com/spring-projects/spring-data-jpa',
		'DATAMONGO': u'https://github.com/spring-projects/spring-data-mongodb',
		'DATAGRAPH': u'https://github.com/spring-projects/spring-data-neo4j',
		'DATAREDIS': u'https://github.com/spring-projects/spring-data-redis',
		'DATAREST': u'https://github.com/spring-projects/spring-data-rest',
		'LDAP': u'https://github.com/spring-projects/spring-ldap',
		'MOBILE': u'https://github.com/spring-projects/spring-mobile',
		'ROO': u'https://github.com/spring-projects/spring-roo',
		'SECOAUTH': u'https://github.com/spring-projects/spring-security-oauth',
		'SHL': u'https://github.com/spring-projects/spring-shell',
		'SOCIAL': u'https://github.com/spring-projects/spring-social',
		'SOCIALFB': u'https://github.com/spring-projects/spring-social-facebook',
		'SOCIALLI': u'https://github.com/spring-projects/spring-social-linkedin',
		'SOCIALTW': u'https://github.com/spring-projects/spring-social-twitter',
		'SPR': u'https://github.com/spring-projects/spring-framework',
		'SWF': u'https://github.com/spring-projects/spring-webflow',
		'SWS': u'https://github.com/spring-projects/spring-ws',
		'SGF': u'https://github.com/spring-projects/spring-data-gemfire',
		'SHDP': u'https://github.com/spring-projects/spring-hadoop',
		'SEC': u'https://github.com/spring-projects/spring-security',
		# 'SOCIALGH':u'https://github.com/spring-projects/spring-social-github',
	}

	###
	# versions has all available source code version
	versions = {}

	# the distribution of bug reports' ID in each version
	# if there are no bug reports in a version, the bugs not include the key according to version.
	bugs = {}
	answers = {}
	sources = {}
	duplicates = {}
	duplicate_sets = {}
	answers_merge = {}

	def __init__(self):
		# make version informations
		self.versions = {}
		self.bugs = {}
		for group in self.groups:
			for project in self.projects[group]:
				self.versions[project] = self.load_versions(group, project)
				self.bugs[project] = self.load_bugs(group, project)
				self.sources[project] = self.load_sources(group, project)
				self.answers[project] = self.load_answers(group, project)
				self.duplicates[project] = self.load_duplicates(group, project)
				self.duplicate_sets[project] = set([])
				for dup in self.duplicates[project]:
					self.duplicate_sets[project].update(dup)
				self.answers_merge[project] = self.load_answers(group, project, 'answers_merge.txt')

				# sumBugs = 0
				# for version in self.bugs[project]:
				# 	if version == 'all': continue
				# 	sumBugs += len(self.bugs[project][version])
				# print(u'%s\t%s\t%d\t%d' % (group, project, len(self.bugs[project]['all']), sumBugs))
		self.complement_duplicates()
		pass

	def complement_duplicates(self):
		if len(self.duplicates) <= 0: return False

		def make(_duplicates):
			flagWorked = False
			newDups = list()
			for srcA, destA in _duplicates:
				newSrc = None
				for srcB, destB in _duplicates:
					if srcA == destB:
						newSrc = srcB
						break
				if newSrc is None:
					newDups.append([srcA, destA])
				else:
					newDups.append([newSrc, destA])
					flagWorked = True
			return newDups, flagWorked

		for project in self.duplicates:
			while True:
				newDups, flag = make(self.duplicates[project])
				if flag is False: break
				self.duplicates[project] = newDups
		return True

	def load_sources(self, _group, _project):
		filename =os.path.join(self.getPath_base(_group, _project), 'sources.txt')
		if os.path.exists(filename) is False:
			return {}
		f = open(filename, 'r')
		text = f.read()
		f.close()
		data = eval(text)
		if 'miss' in data[_project]: del data[_project]['miss']

		return data[_project]

	def load_answers(self, _group, _project, _name=None):
		if _name is not None:
			filename = os.path.join(self.getPath_base(_group, _project), _name)
		else:
			filename = os.path.join(self.getPath_base(_group, _project), 'answers.txt')

		if os.path.exists(filename) is False:
			return {}
		f = open(filename, 'r')
		text = f.read()
		f.close()
		data = eval(text)
		if 'miss' in data[_project]: del data[_project]['miss']

		return data[_project]

	def load_bugs(self, _group, _project):
		filename =os.path.join(self.getPath_base(_group, _project), 'bugs.txt')
		if os.path.exists(filename) is False:
			return {}
		f = open(filename, 'r')
		text = f.read()
		f.close()
		data = eval(text)
		if 'miss' in data[_project]: del data[_project]['miss']

		return data[_project]

	def load_duplicates(self, _group, _project):
		filename = os.path.join(self.getPath_bugrepo(_group, _project), 'duplicates.json')
		if os.path.exists(filename) is False:
			return {}
		f = open(filename, 'r')
		text = f.read()
		f.close()
		data = eval(text)
		nd = {}
		for key in data:
			nd[key.upper()] = data[key]
		return nd[_project.upper()]

	def load_versions(self, _group, _project):
		f = open(os.path.join(self.getPath_base(_group, _project), 'versions.txt'), 'r')
		text = f.read()
		f.close()
		data = eval(text)

		return data[_project]

	####################################################
	# path functions
	####################################################
	def getPath_bugrepo(self, _group, _project):
		return os.path.join(self.root, _group, _project, 'bugrepo')

	def getPath_source(self, _group, _project, _version=None):
		if _version is None:
			return os.path.join(self.root, _group, _project, 'sources')
		return os.path.join(self.root, _group, _project, 'sources', _version)

	def getPath_gitrepo(self, _group, _project):
		return os.path.join(self.root, _group, _project, 'gitrepo')

	def getPath_base(self, _group, _project):
		return os.path.join(self.root, _group, _project)

	def getPath_results(self, _type, _tech, _group, _project, _version):
		return os.path.join(self.root_result, _type, _group, _project, u'%s_%s_%s_output.txt'%(_tech, _project, _version))

	def getPath_result_folder(self, _type, _group, _project):
		return os.path.join(self.root_result, _type, _group, _project)

