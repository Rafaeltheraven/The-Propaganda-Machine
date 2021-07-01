import json
from collections import OrderedDict
from content_selection import match_funcs, search_frame

class Data(object):
	"""docstring for Data"""
	def __init__(self, file):
		super(Data, self).__init__()
		self.data = load_data(file)
		self.players = self.get_players()
		self.countries = self.get_countries()
		self.continents = self.get_continents()
		self.events = self.get_events()
		self.low = 0
		self.high = len(self.events)

	def get_players(self):
		result = []
		for key, value in self.data['players'].items():
			result.append(Player(key, value))
		return result

	def get_events(self):
		return self.data['events']

	def get_countries(self):
		return self.data['countries']

	def get_continents(self):
		return self.data['continents']

class Player(object):
	def __init__(self, name, data):
		super(Player, self).__init__()
		self.name = name
		self.capital = data["capital"]
		self.mission = data["mission"]
		self.countries = data["intial_countries"]

	def __str__(self):
		return self.name

def load_data(file):
	data = ""
	with open(file) as json_file:
		data = json.load(json_file, object_pairs_hook=OrderedDict)
	return data

def write_data(file, data):
	with open(file, "w") as f:
		f.write(data)

def calc_countries(frame, player1, player2):
	countries1 = player1.countries
	countries2 = player2.countries
	for event in frame:
		if event["type"] == "attack" and event["after_defend"] == 0:
			country = event["country2"]
			if event["player1"] == player1.name:
				countries1.append(country)
				countries2.remove(country)
			else:
				countries1.remove(country)
				countries2.append(country)
	return countries1, countries2

