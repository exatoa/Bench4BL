import re

class VersionUtil(object):
	@staticmethod
	def get_latest_version(_versions):
		latest = _versions[0]
		for version in _versions:
			if VersionUtil.cmpVersion(version, latest) > 0:
				latest = version
		return latest

	@staticmethod
	def get_version_groups( _str):
		value = _str.strip()
		if len(value)==0: return []

		splits = re.split(r'\.| |_|-', value if value.find(u'(') < 0 else value[:value.find(u'(')].strip())

		items = []
		for split in splits:
			a = split.strip()
			b = re.sub(r'[^\d]+', '', a).strip()
			if len(a) == len(b):
				items.append(a)
		return items

	@staticmethod
	def hasVersion(_str):
		items = VersionUtil.get_version_groups(_str)
		if len(items) == 0: return False
		return True

	@staticmethod
	def cmpVersion(x, y):
		if x == y: return 0
		if len(x) == 0: return -1
		if len(y) == 0: return 1

		vX = VersionUtil.get_version_groups(x)
		vY = VersionUtil.get_version_groups(y)

		size = min(len(vX), len(vY))
		for idx in range(0, size):
			nX = int(vX[idx]) if len(vX[idx]) > 0 else 0
			nY = int(vY[idx]) if len(vY[idx]) > 0 else 0
			if nX < nY:
				return -1
			elif nX > nY:
				return 1
		if len(vX) < len(vY):
			return -1
		elif len(vX) > len(vY):
			return 1

		return 0


	@staticmethod
	def get_versionName(_version, _projectName=None):
		if _projectName is not None:
			return _projectName + u'_' + _version.replace(u'.', u'_')
		return _version.replace(u'.', u'_')
