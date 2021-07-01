from data_handler import load_data, write_data, Data, calc_countries
from content_selection import get_timeframe, split_turns, group_wars, split_war, search_frame
from random import choice, randint
from formatter import TuringFormatter

fmt = TuringFormatter()

bad_practice = ""

TURNS=4

def neutral_report(data, turns=2):
	templates = load_data("templates.json")["neutral"]
	frame, index, _ = get_timeframe(data, turns)
	background = background_report(data, templates, index)
	write_data("background.md", background)
	report = "# Report \n"
	for event in frame:
		curr_player = event["player1"]
		if "player2" in event:
			other_player = event["player2"]
		else:
			other_player = curr_player
		report += templates[event["type"]].format(**event, curr_player=curr_player, other_player=other_player) + "\n \n"
	return report

def biased_report(data, player, other, turns=2):
	templates = load_data("templates.json")
	frame, low, high = get_timeframe(data, turns)
	turns = split_turns(frame)
	reports = []
	report = ""
	for i, turn in enumerate(turns, 1): # Want to write 1 report per turn.
		wars = []
		other_player = turn[0]["player1"] != player.name
		if not other_player:
			wars = group_wars(turn, player.name)
		else:
			wars = group_wars(turn, other.name)
		report += "# Report (" + player.name + " - Turn " + str(i) + ") \n"
		first = True
		prev_trend = "positive"
		for war in wars:
			war = list(map(lambda x: x['event'], war))
			split = split_war(war)
			misc = list(map(lambda x: x['event'], search_frame(war, [lambda x: x["type"] != "attack"])))
			for i, part in enumerate(split): # These are seperate parts of a continuing war centered on a single country
				if len(part) == 0:
					break
				trend, style = is_positive(part, other_player)
				tree = templates[trend]
				temp = part[0]
				if first:
					report += new_string(tree["introduction"], temp, player, other)
					first = False
				else:
					report += new_string(tree["continue"][prev_trend], temp, player, other)
				report += new_string(tree["fluff"]["opening"], temp, player, other)
				if style == "progress":
					report += new_string(tree["event"]["attack"]["progress"], temp, player, other)
				elif trend == "positive":
					if other_player:
						report += new_string(tree["event"]["attack"]["defending"][style], temp, player, other)
					else:
						report += new_string(tree["event"]["attack"]["won"][style], temp, player, other)
				else:
					if other_player:
						report += new_string(tree["event"]["attack"]["defending"][style], temp, player, other)
					else:
						report += new_string(tree["event"]["attack"]["loss"][style], temp, player, other)
				report += new_string(tree["fluff"]["intermittent"], temp, player, other)
				prev_trend = trend
				if i != len(split)-1:
					report += new_string(tree["fluff"]["continuing"], temp, player, other)
			if misc:
				trend = ""
				if prev_trend == "positive" and not other_player:
					report += new_string(templates["positive"]["fluff"]["reason"], {}, player, other)
					trend = "positive"
				elif other_player:
					report += new_string(templates["negative"]["fluff"]["reason"], {}, player, other)
					trend = "negative"
				if trend:
					for event in misc:
						report += new_string(templates[trend]["event"][event["type"]], event, player, other)
			report += "\n\n"
		report += new_string(templates["positive"]["fluff"]["conclusion"], {}, player, other)
		report += "\n\n"
	return report

def new_string(template, event, player, other):
	global bad_practice
	end = len(template)
	num = randint(0, end-1)
	string = fmt.format(template[num], **event, curr_player=player, other_player=other)
	if bad_practice == string:
		num = (num + 1) % end
		string = fmt.format(template[num], **event, curr_player=player, other_player=other)
	bad_practice = string
	if string[-1] == " ":
		return string
	else:
		return string + " "

def list_string(data):
	result = ""
	for index, elem in enumerate(data):
		if index == len(data) - 1:
			result = result + elem
		elif index == len(data) - 2:
			result = result + elem + " and "
		else:
			result = result + elem + ", "
	return result

def is_positive(frame, other):
	start = frame[0]
	style = start["type"]
	trend = "positive"
	if style == "attack":
		end = frame[-1]
		init_offend = start["init_offend"]
		init_defend = start["init_defend"]
		after_offend = end["after_offend"]
		after_defend = end["after_defend"]
		style = get_style(init_offend, init_defend, after_defend, after_offend, other)
		offend_percent = (100 * after_offend) / init_offend
		defend_percent = (100 * after_defend) / init_defend
		if after_defend != 0:
			if not other:
				if offend_percent <= 50 and defend_percent >= 50:
					if (init_defend - after_defend) < (2*init_offend):
						trend = "negative"
						if style == "struggle":
							style = "progress"
					else:
						style = "progress"
				elif style == "struggle":
					style = "progress"
		elif other:
			trend = "negative"
		# if after_defend != 0:
		# 	if not other and style != "struggle":
		# 		trend = "negative"
		# 	elif not other and style == "struggle":
		# 		style = "progress"
		# elif other:
		# 	trend = "negative"
	elif other:
		trend = "negative"
	return trend, style

def get_style(init_offend, init_defend, after_defend, after_offend, other):
	diff_percent = (init_defend * 100) / init_offend # init_defend is x percent of init_offend
	if diff_percent <= 50:
		if other:
			return "underdog"
		else:
			return "steamroll"
	elif diff_percent > 200:
		if other:
			return "steamroll"
		else:
			return "underdog"
	else:
		return "struggle"

def background_report(data, templates, index):
	background = "# Background \n"
	player1, player2 = data.players
	countries1, countries2 = calc_countries(data.events[:index], player1, player2)
	if player1.capital:
		background += fmt.format(templates["background"]["capital"], curr_player=player1.name, other_player=player2.name, capital1=player1.capital, capital2=player2.capital, countries1=list_string(countries1), countries2=list_string(countries2))
	elif player1.mission != player2.mission:
		background += fmt.format(templates["background"]["mission"], curr_player=player1.name, other_player=player2.name, objective1=player1.mission, objective2=player2.mission, countries1=list_string(countries1), countries2=list_string(countries2))
	else:
		background += fmt.format(templates["background"]["domination"], curr_player=player1.name, other_player=player2.name, countries1=list_string(countries1), countries2=list_string(countries2))
	background += "\n \n"
	background += "# World Map \n"
	background += "![WorldMap](./worldmap.png 'World Map')"
	return background

if __name__ == '__main__':
	data = Data("wardata.json")
	write_data("wardata_neutral.md", neutral_report(data, turns=TURNS))
	data = Data("wardata.json")
	write_data("wardata_bias_1.md", biased_report(data, data.players[0], data.players[1], turns=TURNS))
	data = Data("wardata.json")
	write_data("wardata_bias_2.md", biased_report(data, data.players[1], data.players[0], turns=TURNS))