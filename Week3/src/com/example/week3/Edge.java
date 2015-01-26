package com.example.week3;

public class Edge {
	String line;
	Station from;
	Station to;
	int min;
	
	public Edge(String _line, String _from, String _to, int _min) {
		line = _line;
		from = OpenActivity.roadMap.get(_from);
		to = OpenActivity.roadMap.get(_to);
		min = _min;
	
		from.addEdge(this);
		to.addEdge(this);
	}
}
