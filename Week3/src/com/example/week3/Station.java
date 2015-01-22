package com.example.week3;

public class Station {
	String line;
	String name;
	int tag;
	
	Edge[] edge;
	int numEdge;
	
	int map_x;
	int map_y;
	
	float latitude;
	float longitude;
	
	int prev_min;
	Station prev;
	
	float rating;
	
	public Station(String _line, String _name, int _tag, int _x, int _y, float _lat, float _long, float _rating) {
		line = _line;
		name = _name;
		tag = _tag;

		edge = new Edge[4];
		numEdge = 0;
		
		map_x = _x;
		map_y = _y;
		
		latitude = _lat;
		longitude = _long;
		
		prev_min = 0;
		prev = null;
		
		rating = _rating;
	}
	
	public void addEdge(Edge _edge){
		edge[numEdge++] = _edge;
	}

}
