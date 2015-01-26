package com.example.week3;

public class Station {
	// String line;
	String name;
	
	Edge[] edge;
	int numEdge;
	
	int map_x;
	int map_y;
	
	double latitude;
	double longitude;
	
	int prev_min;
	Station prev;
	// Edge prevEdge;
	
	float rating;
	boolean transfer;
	
	public Station(String _name, int _x, int _y, double _lat, double _long, float _rating) {
		// line = _line;
		name = _name;

		edge = new Edge[4];
		numEdge = 0;
		
		map_x = _x;
		map_y = _y;
		
		latitude = _lat;
		longitude = _long;
		
		prev_min = 0;
		prev = null;
		// prevEdge = null;
		
		transfer = false;
		rating = _rating;
	}
	
	public void addEdge(Edge _edge){
		edge[numEdge++] = _edge;
	}
	
	public Edge getEdge (String _station){
		for(int i = 0; i < edge.length; i++){
			if(edge[i].to.name.equals(_station) || (edge[i].from.name.equals(_station)))
				return edge[i];
		}
		return null;
	}
}
