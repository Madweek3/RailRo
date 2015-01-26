package com.example.week3;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class RailView extends View {
	Drawable mapImage;
	Bitmap trainImage;
	
	Drawable sballoon;
	Drawable eballoon;
	
	Path mPath;
	Paint Pnt;

	PathMeasure pathMeasure;
	float pathLength;

	float step; // distance each step
	float distance; // distance moved

	float[] pos;
	float[] tan;

	Matrix matrix;
	
	float bm_offsetX, bm_offsetY;

	public RailView (Context context, AttributeSet attrs){
		// INITIALIZE
		super(context, attrs);
		
		mapImage = this.getResources().getDrawable(R.drawable.railmap_bw);
		
		sballoon =this.getResources().getDrawable(R.drawable.startballoon);
		eballoon =this.getResources().getDrawable(R.drawable.endballoon);
		
		Pnt = new Paint();
		mPath = new Path();
		
		Fragment2.visit_station = new ArrayList<Station>();
		Fragment2.not_selected_station = new ArrayList<Integer>();
		
		ArrayList<String> copyTrans = (ArrayList<String>) MainActivity.trans.clone();
		
		// CALCULATE THE PATH <TODO>
		if (!MainActivity.start.equals("") && !MainActivity.end.equals("")) {
			Station last = null;
			Station temp = OpenActivity.roadMap.get(MainActivity.start);
			
			// INITIALIZE
			temp.prev = null;
			temp.transfer = false;
			
			sballoon.setBounds(temp.map_x, temp.map_y, temp.map_x +70, temp.map_y + 70);
			
			while (copyTrans.size() != 0) {
				last = temp;
				temp = OpenActivity.roadMap.get(copyTrans.get(0));
				double min = distance(temp.map_x, temp.map_y, last.map_x, last.map_y);
				int min_i = 0;
				
				for (int i = 1; i < copyTrans.size(); i++) {
					Station check = OpenActivity.roadMap.get(copyTrans.get(i));
					double newdist = distance(check.map_x, check.map_y, last.map_x, last.map_y);
					if (min > newdist) {
						min = newdist;
						min_i = i;
					}
				}
				
				findpath(last, OpenActivity.roadMap.get(copyTrans.get(min_i)));
				copyTrans.remove(min_i);
			}

			last = temp;
			temp = OpenActivity.roadMap.get(MainActivity.end);
			eballoon.setBounds(temp.map_x, temp.map_y-70, temp.map_x + 70, temp.map_y);
			
			findpath(last, temp);
			Fragment2.visit_station.add(temp);
			
			trainImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.train);
			
			bm_offsetX = 25;
			bm_offsetY = 25;
			
			mPath.moveTo(Fragment2.visit_station.get(0).map_x, Fragment2.visit_station.get(0).map_y);
			for (int i = 1; i < Fragment2.visit_station.size(); i++)
				mPath.lineTo(Fragment2.visit_station.get(i).map_x, Fragment2.visit_station.get(i).map_y);
		}
		
		pathMeasure = new PathMeasure(mPath, false);
		pathLength = pathMeasure.getLength();
		
		step = 1;
		distance = 0;
		pos = new float[2];
		tan = new float[2];

		matrix = new Matrix();

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
		sballoon.draw(canvas);
		eballoon.draw(canvas);
		
		Pnt.setColor(Color.argb(200, 85, 135, 237));
		Pnt.setStrokeWidth(10);
		Pnt.setStyle(Paint.Style.STROKE);
		
		canvas.drawPath(mPath, Pnt);

		if (distance < pathLength) {
			pathMeasure.getPosTan(distance, pos, tan);

			matrix.reset();
			float degrees = (float) (Math.atan2(tan[1], tan[0]) * 180.0 / Math.PI);
			matrix.postTranslate(pos[0]-bm_offsetX, pos[1]-bm_offsetY);
			canvas.drawBitmap(trainImage, matrix, null);

			distance += step;
			
			if(distance < pathLength)
				invalidate();
		}
	}
	
	public static void findpath(Station from_station, Station to_station){
		ArrayList<Station> check_station = new ArrayList<Station>();
		add_using_station(from_station, from_station.prev, 0, check_station);
		
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
			
			Edge mEdge = adjacent_edges.get(smallest_edge);
			
			for (int i = 0; i < check_station.size(); i++) {
				if (mEdge.from.name.equals(check_station.get(i).name)) {
					add_using_station(mEdge.to, mEdge.from, smallest_min, check_station);
					break;
				} 
				if (mEdge.to.name.equals(check_station.get(i).name)) {
					add_using_station(mEdge.from, mEdge.to, smallest_min, check_station);
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

		// check whether transfer or not
		ArrayList<Station> tempArr = new ArrayList<Station>();
		Station temp = to_station;
		
		while (!temp.name.equals(from_station.name)) {
			tempArr.add(temp.prev);
			if(temp.prev.prev != null && !temp.getEdge(temp.prev.name).line.equals(temp.prev.getEdge(temp.prev.prev.name).line))
				temp.prev.transfer = true;
			temp = temp.prev;
		}
		
		Collections.reverse(tempArr);
		
		// add the partial path to the whole path
		for (int i = 0; i < tempArr.size(); i++){
			temp = tempArr.get(i);
			Fragment2.visit_station.add(temp);

			if(i != 0 && !temp.transfer)
				Fragment2.not_selected_station.add(Fragment2.visit_station.size()-1);	
		}
		
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

