package com.example.week3;

import java.util.ArrayList;
import java.util.Collections;

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
	
	public RailView (Context context, AttributeSet attrs){
		// INITIALIZE
		super(context, attrs);
		
		mapImage = this.getResources().getDrawable(R.drawable.railmap_bw);
		Pnt = new Paint();
		mPath = new Path();
		
		Fragment2.visit_station = new ArrayList<Station>();
		ArrayList<String> copyTrans = (ArrayList<String>) MainActivity.trans.clone();
		
		// CALCULATE THE PATH <TODO>
		if (!MainActivity.start.equals("") && !MainActivity.end.equals("")) {
			Station last = null;
			Station temp =  MainActivity.roadMap.get(MainActivity.start);
			
			while (copyTrans.size() != 0) {
				last = temp;
				temp = MainActivity.roadMap.get(copyTrans.get(0));
				double min = distance(temp.map_x, temp.map_y, last.map_x, last.map_y);
				int min_i = 0;
				
				for (int i = 1; i < copyTrans.size(); i++) {
					Station check = MainActivity.roadMap.get(copyTrans.get(i));
					double newdist = distance(check.map_x, check.map_y, last.map_x, last.map_y);
					if (min > newdist) {
						min = newdist;
						min_i = i;
					}
				}
				findpath(last, MainActivity.roadMap.get(copyTrans.get(min_i)));
				copyTrans.remove(min_i);
			}

			last = temp;
			temp = MainActivity.roadMap.get(MainActivity.end);
			findpath(last, temp);
			Fragment2.visit_station.add(temp);
			
			mPath.moveTo(Fragment2.visit_station.get(0).map_x, Fragment2.visit_station.get(0).map_y);
			for (int i = 1; i < Fragment2.visit_station.size(); i++)
				mPath.lineTo(Fragment2.visit_station.get(i).map_x, Fragment2.visit_station.get(i).map_y);
		}
	}
	
	public double distance(int x1, int y1, int x2, int y2){
		return (x1-x2)*(x1-x2) + (y1-y2)*(y1-y2);
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
	
	public static void findpath(Station from_station, Station to_station){
		ArrayList<Station> check_station = new ArrayList<Station>();
		
		add_using_station(from_station, null, 0, check_station);
		
		for (;;) {
			ArrayList<Edge> adjacent_edges = new ArrayList<Edge>();
			
			for (int i = 0; i < check_station.size(); i++)
				find_adjacent_edges(check_station.get(i), adjacent_edges, check_station);
			
			int smallest_min = adjacent_edges.get(0).min;
			int smallest_edge = 0;
			
			for (int i = 1; i < adjacent_edges.size(); i++) {
				if (smallest_min > adjacent_edges.get(i).min){
					smallest_min = adjacent_edges.get(i).min;
					smallest_edge = i;
				}
			} //find the smallest adjacent edge
			
			for (int i = 0; i < check_station.size(); i++) {
				if (adjacent_edges.get(smallest_edge).from.name.equals(check_station.get(i).name)) {
					add_using_station(adjacent_edges.get(smallest_edge).to, adjacent_edges.get(smallest_edge).from, smallest_min, check_station);
					break;
				} 
				if (adjacent_edges.get(smallest_edge).to.name.equals(check_station.get(i).name)) {
					add_using_station(adjacent_edges.get(smallest_edge).from, adjacent_edges.get(smallest_edge).to, smallest_min, check_station);
					break;
				}
			}//add the vertex in the using_vertex
			
			int count_ = 0;
			for (int i = 0; i < check_station.size(); i++) {
				if (check_station.get(i).name.equals(to_station.name)) {
					count_ = 1;
					break;
				}
			}//finish condition that means find the final edge
			if (count_ == 1)
				break;
			
			adjacent_edges = null;
		}

		// add the partial path to the whole path
		ArrayList<Station> tempArr = new ArrayList<Station>();
		Station temp = to_station;
		while (temp.prev != null && !temp.name.equals(from_station)) {
			tempArr.add(temp.prev);
			temp = temp.prev;
		}
		
		Collections.reverse(tempArr);
		for (int i = 0; i < tempArr.size(); i++)
			Fragment2.visit_station.add(tempArr.get(i));
	}
	
	public static void add_using_station(Station new_station, Station prev_station, int min_from_prev, ArrayList<Station> check_station) {
		check_station.add(new_station);
		new_station.prev = prev_station;
		
		if (prev_station != null)
			new_station.prev_min = prev_station.prev_min + min_from_prev;
		else if (prev_station == null)
			new_station.prev_min = min_from_prev;
	}

	public static void find_adjacent_edges(Station st, ArrayList<Edge> ed, ArrayList<Station> check_station) {
		for (int i = 0; i < st.numEdge; i++) {
			if(check_station.contains(st.edge[i].from) && check_station.contains(st.edge[i].to)){}
			else{
				st.edge[i].min = st.prev_min + st.edge[i].min;
				ed.add(st.edge[i]);
			}
		}
	}
}

