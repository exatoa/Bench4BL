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
	Targets = ['HIVE-13725', 'HIVE-13401', 'HIVE-13388', 'HIVE-14618', 'HIVE-13476', 'HIVE-14322', 'HIVE-14029',
	           'HIVE-13016', 'HIVE-14197', 'HIVE-14267', 'HIVE-13463', 'HIVE-15395', 'HIVE-14386', 'HIVE-15369',
	           'HIVE-13144', 'HIVE-13465', 'HIVE-15144', 'HIVE-13592', 'HIVE-13302', 'HIVE-13945', 'HIVE-15227',
	           'HIVE-13243', 'HIVE-13491', 'HIVE-13751', 'HIVE-14330', 'HIVE-13749', 'HIVE-14060', 'HIVE-13372',
	           'HIVE-13294', 'HIVE-13906', 'HIVE-14245', 'HIVE-15099', 'HIVE-14357', 'HIVE-13000', 'HIVE-14000',
	           'HIVE-13857', 'HIVE-13096', 'HIVE-13263', 'HIVE-12879', 'HIVE-14784', 'HIVE-14113', 'HIVE-14195',
	           'HIVE-14100', 'HIVE-14098', 'HIVE-13396', 'HIVE-15104', 'HIVE-14726', 'HIVE-14694', 'HIVE-13585',
	           'HIVE-15309', 'HIVE-14697', 'HIVE-14715', 'HIVE-13514', 'HIVE-13911', 'HIVE-12780', 'HIVE-12966',
	           'HIVE-14349', 'HIVE-15137', 'HIVE-15397', 'HIVE-14294', 'HIVE-13487', 'HIVE-14214', 'HIVE-15345',
	           'HIVE-13729', 'HIVE-14282', 'HIVE-15196', 'HIVE-12969', 'HIVE-15311', 'HIVE-15329', 'HIVE-13700',
	           'HIVE-13502', 'HIVE-14608', 'HIVE-14355', 'HIVE-13407', 'HIVE-12996', 'HIVE-13590', 'HIVE-15437',
	           'HIVE-13488', 'HIVE-14326', 'HIVE-14707', 'HIVE-14072', 'HIVE-14296', 'HIVE-13710', 'HIVE-15471',
	           'HIVE-14600', 'HIVE-15233', 'HIVE-15030', 'HIVE-15381', 'HIVE-14139', 'HIVE-15504', 'HIVE-14619',
	           'HIVE-14820', 'HIVE-14436', 'HIVE-14743', 'HIVE-14873', 'HIVE-13090', 'HIVE-15488', 'HIVE-13953',
	           'HIVE-14027', 'HIVE-15476', 'HIVE-13821', 'HIVE-13909', 'HIVE-14298', 'HIVE-14218', 'HIVE-14400',
	           'HIVE-13020', 'HIVE-13325', 'HIVE-12941', 'HIVE-13417', 'HIVE-12813', 'HIVE-13645', 'HIVE-15483',
	           'HIVE-13659', 'HIVE-14115', 'HIVE-12905', 'HIVE-14038', 'HIVE-12893', 'HIVE-14311', 'HIVE-13178',
	           'HIVE-14205', 'HIVE-13572', 'HIVE-14295', 'HIVE-14006', 'HIVE-13320', 'HIVE-13621', 'HIVE-14380',
	           'HIVE-13056', 'HIVE-15199', 'HIVE-15275', 'HIVE-13227', 'HIVE-13527', 'HIVE-14399', 'HIVE-13986',
	           'HIVE-12964', 'HIVE-13201', 'HIVE-14839', 'HIVE-14457', 'HIVE-14674', 'HIVE-14187', 'HIVE-12992',
	           'HIVE-15231', 'HIVE-13269', 'HIVE-12795', 'HIVE-14563', 'HIVE-14942', 'HIVE-12963', 'HIVE-14146',
	           'HIVE-13045', 'HIVE-13841', 'HIVE-14192', 'HIVE-13932', 'HIVE-13588', 'HIVE-13047', 'HIVE-13008',
	           'HIVE-13691', 'HIVE-13539', 'HIVE-14278', 'HIVE-14346', 'HIVE-14177', 'HIVE-14865', 'HIVE-13530',
	           'HIVE-14447', 'HIVE-15446', 'HIVE-15258', 'HIVE-14614', 'HIVE-15355', 'HIVE-12945', 'HIVE-12920',
	           'HIVE-13864', 'HIVE-13833', 'HIVE-14432', 'HIVE-15295', 'HIVE-13551', 'HIVE-13753', 'HIVE-13885',
	           'HIVE-13286', 'HIVE-13079', 'HIVE-14244', 'HIVE-13948', 'HIVE-13369', 'HIVE-14089', 'HIVE-13346',
	           'HIVE-15376', 'HIVE-13094', 'HIVE-12824', 'HIVE-13959', 'HIVE-13853', 'HIVE-13599', 'HIVE-13856',
	           'HIVE-14453', 'HIVE-15308', 'HIVE-15359', 'HIVE-15487', 'HIVE-13036', 'HIVE-13947', 'HIVE-15096',
	           'HIVE-14779', 'HIVE-12799', 'HIVE-15341', 'HIVE-13338', 'HIVE-12933', 'HIVE-14479', 'HIVE-15386',
	           'HIVE-14403', 'HIVE-14402', 'HIVE-14348', 'HIVE-14588', 'HIVE-15327', 'HIVE-15122', 'HIVE-13186',
	           'HIVE-15463', 'HIVE-13298', 'HIVE-14012', 'HIVE-13213', 'HIVE-13135', 'HIVE-14062', 'HIVE-13262',
	           'HIVE-15334', 'HIVE-13064', 'HIVE-13237', 'HIVE-14262', 'HIVE-14799', 'HIVE-13705', 'HIVE-13840',
	           'HIVE-13467', 'HIVE-13657', 'HIVE-14200', 'HIVE-15031', 'HIVE-12951', 'HIVE-14998', 'HIVE-13618',
	           'HIVE-15237', 'HIVE-13415', 'HIVE-13480', 'HIVE-14230', 'HIVE-14172', 'HIVE-14418', 'HIVE-14764',
	           'HIVE-13240', 'HIVE-14310', 'HIVE-13077', 'HIVE-14727', 'HIVE-13199', 'HIVE-14446', 'HIVE-13809',
	           'HIVE-14301', 'HIVE-13216', 'HIVE-13111', 'HIVE-13713', 'HIVE-14335', 'HIVE-15291', 'HIVE-14003',
	           'HIVE-13380', 'HIVE-13866', 'HIVE-14513', 'HIVE-14336', 'HIVE-13929', 'HIVE-15252', 'HIVE-12875',
	           'HIVE-14424', 'HIVE-13961', 'HIVE-13957', 'HIVE-13882', 'HIVE-14293', 'HIVE-13112', 'HIVE-15139',
	           'HIVE-12993', 'HIVE-12808', 'HIVE-14659', 'HIVE-13383', 'HIVE-14052', 'HIVE-13874', 'HIVE-13128',
	           'HIVE-13669', 'HIVE-14175', 'HIVE-15494', 'HIVE-14774', 'HIVE-14408', 'HIVE-15323', 'HIVE-13283',
	           'HIVE-15312', 'HIVE-13410', 'HIVE-15361', 'HIVE-13598', 'HIVE-14702', 'HIVE-13767', 'HIVE-13093',
	           'HIVE-13105', 'HIVE-13818', 'HIVE-13246', 'HIVE-12915', 'HIVE-15273', 'HIVE-13255', 'HIVE-13844',
	           'HIVE-14766', 'HIVE-13108', 'HIVE-13342', 'HIVE-15485', 'HIVE-13437', 'HIVE-13324', 'HIVE-14433',
	           'HIVE-13498', 'HIVE-13500', 'HIVE-14114', 'HIVE-13787', 'HIVE-15276', 'HIVE-13596', 'HIVE-13878',
	           'HIVE-15090', 'HIVE-13303', 'HIVE-14236', 'HIVE-12947', 'HIVE-13743', 'HIVE-13445', 'HIVE-15447',
	           'HIVE-13686', 'HIVE-13660', 'HIVE-14141', 'HIVE-13403', 'HIVE-14008', 'HIVE-12885', 'HIVE-13146',
	           'HIVE-12926', 'HIVE-12990', 'HIVE-14037', 'HIVE-14448', 'HIVE-15236', 'HIVE-13002', 'HIVE-15162',
	           'HIVE-12927', 'HIVE-13991', 'HIVE-13837', 'HIVE-14132', 'HIVE-12789', 'HIVE-13533', 'HIVE-13719',
	           'HIVE-12937', 'HIVE-14241', 'HIVE-13310', 'HIVE-13553', 'HIVE-12812', 'HIVE-13285', 'HIVE-15521',
	           'HIVE-15503', 'HIVE-14007', 'HIVE-13494', 'HIVE-14173', 'HIVE-14074', 'HIVE-13291', 'HIVE-13381',
	           'HIVE-14898', 'HIVE-13518', 'HIVE-14822', 'HIVE-14053', 'HIVE-13082', 'HIVE-14234', 'HIVE-14090',
	           'HIVE-12931', 'HIVE-14136', 'HIVE-14895', 'HIVE-13287', 'HIVE-13561', 'HIVE-13931', 'HIVE-13153',
	           'HIVE-13831', 'HIVE-14658', 'HIVE-14693', 'HIVE-15442', 'HIVE-13570', 'HIVE-15148', 'HIVE-14480',
	           'HIVE-14054', 'HIVE-14111', 'HIVE-13924', 'HIVE-14678', 'HIVE-13340', 'HIVE-14338', 'HIVE-14773',
	           'HIVE-13458', 'HIVE-15474', 'HIVE-15029', 'HIVE-14286', 'HIVE-14680', 'HIVE-13232', 'HIVE-13311',
	           'HIVE-13043', 'HIVE-14313', 'HIVE-15421', 'HIVE-13115', 'HIVE-13872', 'HIVE-15293', 'HIVE-14268',
	           'HIVE-15113', 'HIVE-13985', 'HIVE-15280', 'HIVE-12981', 'HIVE-14991', 'HIVE-14176', 'HIVE-15178',
	           'HIVE-13251', 'HIVE-15060', 'HIVE-15367', 'HIVE-14024', 'HIVE-13513', 'HIVE-13169', 'HIVE-14091',
	           'HIVE-14814', 'HIVE-13676', 'HIVE-14574', 'HIVE-14483', 'HIVE-14147', 'HIVE-14435', 'HIVE-14308',
	           'HIVE-14959', 'HIVE-13936', 'HIVE-14242', 'HIVE-14737', 'HIVE-12809', 'HIVE-15234', 'HIVE-13608',
	           'HIVE-14265', 'HIVE-14858', 'HIVE-14210', 'HIVE-15077', 'HIVE-13448', 'HIVE-13997', 'HIVE-14397',
	           'HIVE-13233', 'HIVE-13141', 'HIVE-13861', 'HIVE-13941', 'HIVE-14126', 'HIVE-15065', 'HIVE-15403',
	           'HIVE-14439', 'HIVE-14345', 'HIVE-13447', 'HIVE-14411', 'HIVE-13046', 'HIVE-13859', 'HIVE-15239',
	           'HIVE-14560', 'HIVE-13339', 'HIVE-13449', 'HIVE-15331', 'HIVE-14805', 'HIVE-15002', 'HIVE-13017',
	           'HIVE-13960', 'HIVE-13510', 'HIVE-13720', 'HIVE-14426', 'HIVE-12800', 'HIVE-13217', 'HIVE-14591',
	           'HIVE-13648', 'HIVE-14055', 'HIVE-13211', 'HIVE-13540', 'HIVE-13446', 'HIVE-14226', 'HIVE-13973',
	           'HIVE-12792', 'HIVE-12867', 'HIVE-14714', 'HIVE-13423', 'HIVE-14122', 'HIVE-15054', 'HIVE-14520',
	           'HIVE-14144', 'HIVE-15061', 'HIVE-13523', 'HIVE-14258', 'HIVE-14538', 'HIVE-14378', 'HIVE-13832',
	           'HIVE-13209', 'HIVE-14207', 'HIVE-13439', 'HIVE-12954', 'HIVE-12904', 'HIVE-13968', 'HIVE-12785',
	           'HIVE-13428', 'HIVE-15124', 'HIVE-14624', 'HIVE-13646', 'HIVE-14263', 'HIVE-15417', 'HIVE-14634',
	           'HIVE-13014', 'HIVE-14924', 'HIVE-15247', 'HIVE-13434', 'HIVE-14324', 'HIVE-13876', 'HIVE-14621',
	           'HIVE-12911', 'HIVE-14566', 'HIVE-13704', 'HIVE-14135', 'HIVE-15282', 'HIVE-12772', 'HIVE-14350',
	           'HIVE-12788', 'HIVE-14519', 'HIVE-13568', 'HIVE-13332', 'HIVE-13462', 'HIVE-14034', 'HIVE-13813',
	           'HIVE-13278', 'HIVE-14045', 'HIVE-14564', 'HIVE-13972', 'HIVE-13236', 'HIVE-14751', 'HIVE-14570',
	           'HIVE-13185', 'HIVE-13210', 'HIVE-14686', 'HIVE-13343', 'HIVE-14876', 'HIVE-13440', 'HIVE-13810',
	           'HIVE-15257', 'HIVE-13052', 'HIVE-13699', 'HIVE-13493', 'HIVE-14819', 'HIVE-14076', 'HIVE-13756',
	           'HIVE-14071', 'HIVE-13617', 'HIVE-13322', 'HIVE-14390', 'HIVE-14215', 'HIVE-15274', 'HIVE-13858',
	           'HIVE-12999', 'HIVE-13728', 'HIVE-14589', 'HIVE-15351', 'HIVE-13126', 'HIVE-14361', 'HIVE-13065',
	           'HIVE-13184', 'HIVE-14004', 'HIVE-14083', 'HIVE-12797', 'HIVE-12865', 'HIVE-13361', 'HIVE-13652',
	           'HIVE-13218', 'HIVE-15344', 'HIVE-14530', 'HIVE-13160', 'HIVE-14092', 'HIVE-12794', 'HIVE-14889',
	           'HIVE-13625', 'HIVE-13912', 'HIVE-15095', 'HIVE-13822', 'HIVE-14930', 'HIVE-13084', 'HIVE-13422',
	           'HIVE-14414', 'HIVE-14606', 'HIVE-14254', 'HIVE-12998', 'HIVE-13930', 'HIVE-12784', 'HIVE-14022',
	           'HIVE-13191', 'HIVE-14367', 'HIVE-13730', 'HIVE-14011', 'HIVE-15519', 'HIVE-14801', 'HIVE-15251',
	           'HIVE-13174', 'HIVE-13622', 'HIVE-13051', 'HIVE-14988', 'HIVE-13013', 'HIVE-15190', 'HIVE-14573',
	           'HIVE-13927', 'HIVE-13883', 'HIVE-13597', 'HIVE-14928', 'HIVE-14710', 'HIVE-13512', 'HIVE-12837',
	           'HIVE-13525', 'HIVE-14984', 'HIVE-13900', 'HIVE-15167', 'HIVE-15140', 'HIVE-13563', 'HIVE-12864',
	           'HIVE-14015', 'HIVE-13326', 'HIVE-14073', 'HIVE-15296', 'HIVE-14013', 'HIVE-12995', 'HIVE-14648',
	           'HIVE-15493', 'HIVE-15202', 'HIVE-15279', 'HIVE-13083', 'HIVE-13904', 'HIVE-13632', 'HIVE-15048',
	           'HIVE-15517', 'HIVE-14964', 'HIVE-15160', 'HIVE-12965', 'HIVE-14297', 'HIVE-14292', 'HIVE-13867',
	           'HIVE-13242', 'HIVE-14259', 'HIVE-13934', 'HIVE-13110', 'HIVE-14178', 'HIVE-15181', 'HIVE-14229',
	           'HIVE-13855', 'HIVE-14690', 'HIVE-13826', 'HIVE-13405', 'HIVE-13267', 'HIVE-15335', 'HIVE-14557',
	           'HIVE-13966', 'HIVE-13330', 'HIVE-13862', 'HIVE-13602', 'HIVE-14516', 'HIVE-14813', 'HIVE-13089',
	           'HIVE-13836', 'HIVE-14422', 'HIVE-13693', 'HIVE-13313', 'HIVE-13378', 'HIVE-13870', 'HIVE-15406',
	           'HIVE-14032', 'HIVE-15390', 'HIVE-13552', 'HIVE-14366', 'HIVE-15038', 'HIVE-14359', 'HIVE-14778',
	           'HIVE-14163', 'HIVE-15062', 'HIVE-15428', 'HIVE-13392', 'HIVE-13057', 'HIVE-13610', 'HIVE-13099',
	           'HIVE-13754', 'HIVE-14960', 'HIVE-13379', 'HIVE-13390', 'HIVE-15347', 'HIVE-14137', 'HIVE-14463',
	           'HIVE-15278', 'HIVE-13200', 'HIVE-12976', 'HIVE-14864', 'HIVE-13086', 'HIVE-15120', 'HIVE-13701',
	           'HIVE-13131', 'HIVE-13299', 'HIVE-12894', 'HIVE-15338', 'HIVE-14251', 'HIVE-15445', 'HIVE-13258',
	           'HIVE-14333', 'HIVE-12815', 'HIVE-13327', 'HIVE-14652', 'HIVE-13159', 'HIVE-14363', 'HIVE-13628',
	           'HIVE-14617', 'HIVE-13619', 'HIVE-15299', 'HIVE-14188', 'HIVE-13485', 'HIVE-13542', 'HIVE-13834',
	           'HIVE-14031', 'HIVE-13895', 'HIVE-15177', 'HIVE-14109', 'HIVE-13823', 'HIVE-15343', 'HIVE-13849',
	           'HIVE-14059', 'HIVE-15385', 'HIVE-15081', 'HIVE-13042', 'HIVE-14381', 'HIVE-12887', 'HIVE-13887',
	           'HIVE-14607', 'HIVE-13373', 'HIVE-13260', 'HIVE-13175', 'HIVE-13653', 'HIVE-14900', 'HIVE-15482',
	           'HIVE-14360', 'HIVE-13300', 'HIVE-13394', 'HIVE-13987', 'HIVE-12790', 'HIVE-13712', 'HIVE-15267',
	           'HIVE-15211', 'HIVE-14966', 'HIVE-13744', 'HIVE-13039', 'HIVE-14222', 'HIVE-13293', 'HIVE-13609',
	           'HIVE-14140', 'HIVE-14155', 'HIVE-13163', 'HIVE-13333', 'HIVE-14332', 'HIVE-14142', 'HIVE-13264',
	           'HIVE-13261', 'HIVE-12891', 'HIVE-14377', 'HIVE-14706', 'HIVE-13452', 'HIVE-12820']

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
		bugitems = self.link_fixedFiles(bugitems, _removeTest)
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
				if f[:-4] not in self.Targets: continue
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
				dest = None
				for y in range(len(_bugitems)):
					if x == y:continue
					if _bugitems[y]['id'] != link['id']: continue
					dest = _bugitems[y]
					break
				if dest is None: continue

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
