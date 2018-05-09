#-*- coding: utf-8 -*-
from __future__ import print_function
import math

###############################################################################################################
def combination(n, r):
	res = 1
	for y in range(n, n-r, -1):
		res *= y
	res /= math.factorial(r)
	return res