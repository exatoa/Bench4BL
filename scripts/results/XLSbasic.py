#-*- coding: utf-8 -*-
'''
Created on 2016. 11. 19
Updated on 2016. 01. 09

'''
from __future__ import print_function
import xlsxwriter


class XLSbasic(object):
	__name__ = u'XLSbasic'
	workbook = None

	def __init__(self, _output):
		self.workbook = xlsxwriter.Workbook(_output)
		self.define_formats()

	def define_formats(self):
		# Style Definition
		self.title_base_format = self.workbook.add_format({'bold': True, 'align': 'center', 'border': True, 'valign':'vcenter'})
		self.title_format = self.workbook.add_format({'text_wrap':True, 'valign':'vcenter', 'bold': True, 'align': 'center', 'bg_color': self.RGBA(197, 217, 241), 'border': True})

		self.subtitle_format = self.workbook.add_format({'text_wrap':True, 'bold': True, 'align': 'center', 'valign':'vcenter', 'bg_color': self.RGBA(255, 242, 204), 'border': True})
		self.subtitle_number_format = self.workbook.add_format({'bold': True, 'align': 'right', 'bg_color': self.RGBA(255, 242, 204), 'border': True, 'num_format':'#,##0'})
		self.subtitle_float_format = self.workbook.add_format({'bold': True, 'align': 'right', 'bg_color': self.RGBA(255, 242, 204), 'border': True, 'num_format': '#,##0.00'})
		self.subtitle_percent_format = self.workbook.add_format({'bold': True, 'align': 'right', 'bg_color': self.RGBA(255, 242, 204), 'border': True, 'num_format':'#,##0.00%'})

		self.blank_format = self.workbook.add_format({'align':'left'})
		self.base_format = self.workbook.add_format({'align': 'left', 'border': True})
		self.id_format = self.workbook.add_format({'align': 'center', 'border': True})
		self.number_format = self.workbook.add_format({'align': 'right', 'border': True, 'num_format':'#,##0'})
		self.float_format = self.workbook.add_format({'align': 'right', 'border': True, 'num_format':'#,##0.0000'})
		self.percent_format = self.workbook.add_format({'align': 'right', 'border': True, 'num_format': '#,##0.0%'})

		self.blue_id_format = self.workbook.add_format({'bg_color': self.RGBA(197, 217, 241),'align': 'center', 'border': True})
		self.blue_number_format = self.workbook.add_format({'bg_color': self.RGBA(197, 217, 241),'align': 'right', 'border': True, 'num_format': '#,##0'})
		self.blue_float_format = self.workbook.add_format({'bg_color': self.RGBA(197, 217, 241),'align': 'right', 'border': True, 'num_format': '#,##0.0000'})
		self.blue_percent_format = self.workbook.add_format({'bg_color': self.RGBA(197, 217, 241),'align': 'right', 'border': True, 'num_format': '#,##0.0%'})

		self.yellow_id_format = self.workbook.add_format({'bg_color': self.RGBA(255, 242, 204), 'align': 'center', 'border': True})
		self.yellow_number_format = self.workbook.add_format({'bg_color': self.RGBA(255, 242, 204), 'align': 'right', 'border': True, 'num_format':'#,##0'})
		self.yellow_float_format = self.workbook.add_format({'bg_color': self.RGBA(255, 242, 204), 'align': 'right', 'border': True, 'num_format':'#,##0.0000'})
		self.yellow_percent_format = self.workbook.add_format({'bg_color': self.RGBA(255, 242, 204), 'align': 'right', 'border': True, 'num_format': '#,##0.0%'})

		self.inactive_base_format = self.workbook.add_format({'align': 'left', 'border': True, 'bg_color': self.RGBA(191, 191, 191)})
		self.inactive_id_format = self.workbook.add_format({'align': 'center', 'border': True, 'bg_color': self.RGBA(191, 191, 191)})
		self.inactive_number_format = self.workbook.add_format({'align': 'right', 'border': True, 'num_format':'#,##0', 'bg_color': self.RGBA(191, 191, 191)})
		self.inactive_float_format = self.workbook.add_format({'align': 'right', 'border': True, 'num_format':'#,##0.0000', 'bg_color': self.RGBA(191, 191, 191)})
		self.inactive_percent_format = self.workbook.add_format({'align': 'right', 'border': True, 'num_format': '#,##0.0%', 'bg_color': self.RGBA(191, 191, 191)})
		pass

	def finalize(self):
		'''
		close and save
		:return:
		'''
		if self.workbook is not None:
			print(u'[%s] saving.....' % self.__name__, end=u'')
			self.workbook.close()
			print(u'Done')
		pass

	def RGBA(self, red, green, blue, alpha=0):
		return u'#%X%X%X' % (red, green, blue)

	def charCol(self, _idx):
		div = _idx + 1
		string = u''
		while div > 0:
			module = (div - 1) % 26
			string = chr(65 + module) + string
			div = int((div - module) / 26)
		return string

	def indexCol(self, _chars):
		index = 0
		for char in _chars:
			index = index * 26 + (ord(char.upper()) - 64)
		return index-1




	#################################################################
	# Util functions
	#################################################################
	def set_cols(self, sheet, col, widths):
		# setting column size
		for x in range(0, len(widths)):
			sheet.set_column(col + x, col + x, width=widths[x])  # ProjectName

	def set_rows(self, sheet, row, heights):
		for y in range(0, len(heights)):
			sheet.set_row(row + y, height=heights[y])  # ProjectName

	def input_colspan(self, sheet,  row, col, span, values, styles = None, default_style=None):
		if default_style is None:	default_style = self.base_format
		span -= 1

		for y in range(0, len(values)):
			if styles is None or styles[y] is None:cell_style = default_style
			else:									cell_style = styles[y]
			sheet.merge_range(row + y, col, row + y, col + span, values[y], cell_style)

	def input_rowspan(self, sheet,  row, col, span, values, styles = None, default_style=None):
		if default_style is None:	default_style = self.base_format
		span -= 1

		for x in range(0, len(values)):
			if styles is None or styles[x] is None:cell_style = default_style
			else:									cell_style = styles[x]
			sheet.merge_range(row, col + x, row + span, col + x, values[x], cell_style)


	def input_col(self, sheet, row, col, values, styles=None, default_style=None):
		if default_style is None:	default_style = self.base_format

		for x in range(0, len(values)):
			if styles is None or styles[x] is None:	cell_style = default_style
			else:										cell_style = styles[x]

			sheet.write(row, col + x, values[x], cell_style)
		pass

	def input_row(self, sheet, row, col, values, styles = None, default_style=None):
		if default_style is None:	default_style = self.base_format

		for x in range(0, len(values)):
			if styles is None or styles[x] is None :cell_style = default_style
			else:									cell_style = styles[x]

			sheet.write(row, col + x, values[x], cell_style)
		pass