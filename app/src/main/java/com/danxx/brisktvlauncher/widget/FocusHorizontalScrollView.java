package com.danxx.brisktvlauncher.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.widget.HorizontalScrollView;

import com.danxx.brisktvlauncher.R;

public class FocusHorizontalScrollView extends HorizontalScrollView {

	private static final String TAG = "FocusHorizontalScrollView";
	public FocusHorizontalScrollView(Context context) {
		super(context);
	}
	public FocusHorizontalScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public FocusHorizontalScrollView(Context context, AttributeSet attrs,
									 int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	protected void measureChildWithMargins(View child,
			int parentWidthMeasureSpec, int widthUsed,
			int parentHeightMeasureSpec, int heightUsed) {
		super.measureChildWithMargins(child, parentWidthMeasureSpec, widthUsed,
				parentHeightMeasureSpec, heightUsed);
	}
	@Override
	protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
		if (getChildCount() == 0)
			return 0;

		int width = getWidth();
		int screenLeft = getScrollX();
		int screenRight = screenLeft + width;
		/*提前滚动的距离*/
		int fadingEdge = this.getResources().getDimensionPixelSize(
				R.dimen.d_160dp);

		// leave room for left fading edge as long as rect isn't at very left
		if (rect.left > 0) {
			screenLeft += fadingEdge;
		}

		// leave room for right fading edge as long as rect isn't at very right
		if (rect.right < getChildAt(0).getWidth()) {
			screenRight -= fadingEdge;
		}

		int scrollXDelta = 0;

		if (rect.right > screenRight && rect.left > screenLeft) {
			// need to move right to get it in view: move right just enough so
			// that the entire rectangle is in view (or at least the first
			// screen size chunk).

			if (rect.width() > width) {
				// just enough to get screen size chunk on
				scrollXDelta += (rect.left - screenLeft);
			} else {
				// get entire rect at right of screen
				scrollXDelta += (rect.right - screenRight);
			}

			// make sure we aren't scrolling beyond the end of our content
			int right = getChildAt(0).getRight();
			int distanceToRight = right - screenRight;
			scrollXDelta = Math.min(scrollXDelta, distanceToRight);

		} else if (rect.left < screenLeft && rect.right < screenRight) {
			// need to move right to get it in view: move right just enough so
			// that
			// entire rectangle is in view (or at least the first screen
			// size chunk of it).

			if (rect.width() > width) {
				// screen size chunk
				scrollXDelta -= (screenRight - rect.right);
			} else {
				// entire rect at left
				scrollXDelta -= (screenLeft - rect.left);
			}

			// make sure we aren't scrolling any further than the left our
			// content
			scrollXDelta = Math.max(scrollXDelta, -getScrollX());
		}
		return scrollXDelta;
	}
	@Override
	public void requestChildFocus(View child, View focused) {
		super.requestChildFocus(child, focused);
	}
	@Override
	protected boolean onRequestFocusInDescendants(int direction,
			Rect previouslyFocusedRect) {
		return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
	}
	@Override
	public void fling(int velocityX) {
		super.fling(velocityX);
	}
	/*boolean NeedsPage=false;
	int mydirection=-1;*/
	@Override
	public void scrollTo(int x, int y) {
		super.scrollTo(x, y);
		/*if(NeedsPage&&mydirection!=-1){
			NeedsPage = false;
			pageScroll(mydirection);
			return;
		}*/
		
	}

	
	
}
