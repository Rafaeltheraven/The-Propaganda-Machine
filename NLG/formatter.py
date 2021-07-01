from string import Formatter
import math

class TuringFormatter(Formatter):
	def __init__(self):
		Formatter.__init__(self)

	def get_value(self, key, args, kwds):
		if isinstance(key, str):
			if key not in kwds:
				return eval(key, globals(), kwds)
			else:
				return kwds[key]
		else:
			Formatter.get_value(key, args, kwds)

def ordinal(n):
	return "%d%s" % (n,"tsnrhtdd"[(math.floor(n/10)%10!=1)*(n%10<4)*n%10::4])