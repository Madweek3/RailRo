package com.example.week3;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class RailView extends View {
	Drawable mapImage;
	Path mPath;
	Paint Pnt;
	ArrayList<Station> visit_station;

	public RailView (Context context, AttributeSet attrs){
		// INITIALIZE
		super(context, attrs);
		
		mapImage = this.getResources().getDrawable(R.drawable.railmap_bw);
		Pnt = new Paint();
		mPath = new Path();

		// CALCULATE THE PATH
		visit_station = new ArrayList<Station>();
		findpath(MainActivity.roadMap.get(0), MainActivity.roadMap.get(10), visit_station);
		
		mPath.moveTo(visit_station.get(0).map_x, visit_station.get(0).map_y);
		for(int i = 1; i < visit_station.size(); i++)
		  mPath.lineTo(visit_station.get(i).map_x, visit_station.get(i).map_y);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	@Override
	protected void onDraw(Canvas canvas){
		mapImage.setBounds(0, 0, 700, 980);
		mapImage.draw(canvas);
		
		Pnt.setColor(Color.argb(200, 85, 135, 237));
		Pnt.setStrokeWidth(10);
		Pnt.setStyle(Paint.Style.STROKE);
		
		canvas.drawPath(mPath, Pnt);
	}
	
	public static void findpath(Station from_station, Station to_station, ArrayList<Station> visit_station){
		add_using_station(visit_station, from_station, null, 0);
		
		for (;;) {
			ArrayList<Edge> adjacent_edges = new ArrayList<Edge>();
			for (int i = 0; i < visit_station.size(); i++)
				find_adjacent_edges(visit_station.get(i), adjacent_edges, visit_station);
			
			int smallest_edge = adjacent_edges.get(0).min;
			for (int i = 0; i < adjacent_edges.size(); i++) {
				if (smallest_edge > adjacent_edges.get(i).min)
					smallest_edge = adjacent_edges.get(i).min;
			}//find the smallest adjacent edge
			
			int j;
			for (j = 0; j < adjacent_edges.size(); j++) {
				if (smallest_edge == adjacent_edges.get(j).min) {
					smallest_edge =adjacent_edges.get(j).min;
					break;
				}
			}
			for (int i = 0; i < visit_station.size(); i++) {
				if (adjacent_edges.get(j).from.equals(visit_station.get(i))) {
					add_using_station(visit_station, adjacent_edges.get(j).to,adjacent_edges.get(j).from, smallest_edge);
					break;
				} 
				if (adjacent_edges.get(j).to.equals(visit_station.get(i))) {
					add_using_station(visit_station, adjacent_edges.get(j).from,adjacent_edges.get(j).to, smallest_edge);
					break;
				}
			}//add the vertex in the using_vertex
			
			int count_ = 0;
			for (int i = 0; i < visit_station.size(); i++) {
				if (visit_station.get(i).name.equals(to_station.name)) {
					count_ = 1;
					break;
				}
			}//finish condition that means find the final edge
			if (count_ == 1)
				break;
			
			adjacent_edges = null;
		}
	}
	
	public static void add_using_station(ArrayList<Station> visit_station, Station new_station, Station prev_station, int min_from_prev) {
		visit_station.add(new_station);
		new_station.prev = prev_station;
		
		if (prev_station != null) {
			new_station.prev_min = prev_station.prev_min + min_from_prev;
		} 
		else if (prev_station == null) {
			new_station.prev_min = min_from_prev;
		}
	}

	public static void find_adjacent_edges(Station st, ArrayList<Edge> ed, ArrayList<Station> visit_station) {
		for (int i = 0; i < st.numEdge; i++) {
			if(visit_station.contains(st.edge[i].from) && visit_station.contains(st.edge[i].to)){}
			else{
				st.edge[i].min = st.prev_min + st.edge[i].min;
				ed.add(st.edge[i]);
			}
		}
	}
}

