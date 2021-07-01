def get_timeframe(data, max_turns=2):
	events = data.events
	mid = len(events) // 2
	curr_player = events[mid]["player1"]
	low = mid
	done = False
	while not done:
		low = low - 1
		temp = events[low]["player1"]
		if temp != curr_player:
			done = True
			low = low+1
	high = mid
	turns = 0
	while turns < max_turns:
		high = high+1
		temp = events[high]["player1"]
		if temp != curr_player:
			turns = turns + 1
			curr_player = temp
	return events[low:high], low, high

def split_turns(frame):
	result = []
	curr = []
	player = frame[0]["player1"]
	for event in frame:
		if event["player1"] != player:
			result.append(curr)
			curr = [event]
			player = event["player1"]
		else:
			curr.append(event)
	result.append(curr)
	return result

def group_wars(frame, player=None):
	if not player:
		player = frame[0]["player1"]
	war_events = search_frame(frame, [match_funcs({"type": "attack", "player1": player})]) # A list of attack events
	result = []
	for block in war_events:
		event, index = block["event"], block["index"]
		if check_visited(event):
			rel = find_related(frame, event["country1"], event["player1"], index) # A list of related events
			result.append(rel)
			for temp in rel:
				temp["event"]["visited"] = True
	return result

def find_related(frame, country, player, index):
	prev_rel = search_frame(frame[0:index], [match_funcs({"player1": player, "country1": country}), check_visited]) # A list of every previous event in this country
	succ_rel = search_frame(frame[index:], [match_funcs({"player1": player, "country1": country}), check_visited]) # A list of every following event in this country
	last_attack = search_frame(frame[index:], [match_funcs({"player1": player, "country1": country, "type": "attack"}), check_visited]) # A list of all attack events in this country
	if last_attack: # If there was an attack in this country
		last = last_attack[-1]
		abs_index = last["index"] + index
		if abs_index < len(frame) and has_won(last["event"]):
			succ_rel.extend(find_related(frame, last["event"]["country2"], player, abs_index))
	prev_rel.extend(succ_rel)
	return prev_rel

'''
Searches a given list ("frame") of events and returns all events that fit the requirements
matches has a set of key, value pairs that MUST be matched. If one of these is not matched, skip
'''
def search_frame(frame, funcs):
	result = []
	for i, event in enumerate(frame):
		match = True
		for func in funcs:
			if not func(event):
				match = False
				break
		if match:
			# event["visited"] = True
			result.append({"event": event, "index": i})
	return result

def match_funcs(matches):
	return lambda x: match_func(x, matches)

def match_func(event, matches):
	match = True
	for key, value in matches.items():
		if key not in event or event[key] != value:
			match = False
			break
	return match

def check_visited(event):
	if "visited" not in event or event["visited"] == False:
		return True
	else:
		return False

def has_won(event):
	return event["after_defend"] == 0

def find_war(frame):
	first = 0
	for index, event in enumerate(frame):
		if event["type"] == "attack":
			return index
	return -1

def split_war(frame):
	index = find_war(frame)
	country = frame[index]["country2"]
	result = []
	curr = []
	for event in frame:
		if event["type"] == "attack":
			temp = event["country2"]
			if temp == country:
				curr.append(event)
			else:
				country = temp
				result.append(curr)
				curr = []
	result.append(curr)
	return result