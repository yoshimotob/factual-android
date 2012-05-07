package com.factual.android.demo.base;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;

import com.google.android.maps.MapView;
import com.google.android.maps.Projection;

public class SearchMap extends MapView implements OnDoubleTapListener, OnGestureListener, OnScaleGestureListener {
	private ResultItemOverlay resultsOverlay;
	public ResultItemOverlay getResultsOverlay() {
		return resultsOverlay;
	}

	public void setResultsOverlay(ResultItemOverlay resultsOverlay) {
		this.resultsOverlay = resultsOverlay;
	}

	public SearchMap(Context context, AttributeSet arg1) {
		super(context, arg1); 
		gestureDetector = new GestureDetector(this);  
		gestureDetector.setOnDoubleTapListener(this);  
	    scaleDetector = new ScaleGestureDetector(context, this);

	}

	private GestureDetector gestureDetector; 
	private ScaleGestureDetector scaleDetector;

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
	}
	
	@Override
    public boolean onTouchEvent(MotionEvent evt) {
        scaleDetector.onTouchEvent(evt);  
 		gestureDetector.onTouchEvent(evt);  
        boolean consumed  = super.onTouchEvent(evt);
 		return consumed;
   }

public boolean onDoubleTap(MotionEvent e) {
  	 int x = (int)e.getX(), y = (int)e.getY();;  
     Projection p = getProjection();  
     getController().animateTo(p.fromPixels(x, y));
     getController().zoomIn();  
     return true;  
}

	public boolean onDoubleTapEvent(MotionEvent arg0) {
		return false;
	}

	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		return false;
	}

	public boolean onDown(MotionEvent arg0) {
		return false;
	}

	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	public void onLongPress(MotionEvent arg0) {
	}

	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		return false;
	}

	public void onShowPress(MotionEvent arg0) {
	}

	public boolean onSingleTapUp(MotionEvent arg0) {
        if (resultsOverlay != null) {
        	resultsOverlay.setFocus(null);
        }
		return false;
	}

	public boolean onScale(ScaleGestureDetector detector) {
		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		return true;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
	}
}
