package com.lexudev.imagegallery;

/*
 *This the the customized ViewPager which allows the pages flingable.
 *It implements the GestureDetector.OnGestureListener interface, and make the viewPager react correspondingly to OnScroll and OnFligh gestures.
 *It manipulate three important methods of ViewPager: fakeDragBy(distX). beginFakeDrag(),fakeDragBy(distX), associated with a scroller make the fling work.
 *I take most of the code from online, and adjust it to my own project.
 */


import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.GestureDetector;
import android.widget.Scroller;

public class FlingViewPager extends ViewPager implements GestureDetector.OnGestureListener
{
	
	private GestureDetector mGestureDetector;
	private FlingRunnable mFlingRunnable = new FlingRunnable();
	private boolean mPageMoving = false;
	private boolean mIsBeingScrolled = false;
	
	
	public FlingViewPager(Context context)
	{
	    super(context);
	}
	
	public FlingViewPager(Context context, AttributeSet attrs)
	{
	    super(context, attrs);
	    mGestureDetector = new GestureDetector(context, this);
	}
	
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent event)
	{
		// When the image is full screen, or user finger is moving, intercept the event so the SimpleTouchView wont handle any
		if(SimpleTouchImageView.anyOnFullScreen == true || event.getAction() == MotionEvent.ACTION_MOVE)
		{
			return true;
		}
		
	    return false;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	   
		// Let the gestureDectector handle the event
		mGestureDetector.onTouchEvent(event);
		
		// If the finger lift up after a scroll(when a scroll ends), call endFakeDrag() to center the page that is closest to the mid
		if((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP && mIsBeingScrolled == true)
		{	
			mIsBeingScrolled = false; //Not being scrolled anymore
			this.endFakeDrag();
			mPageMoving = false;	
		}
		
	    return true;
	}
	
	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, final float velX, float velY) 
	{
	   	mFlingRunnable.startUsingVelocity((int)velX);
		return false;
	}
	
	private void trackMotion(float distX) {
	
	    // The following mimics the underlying calculations in ViewPager
		final int width = getWidth();
		final int widthWithMargin = width + this.getPageMargin();
		float scrollOffset = getScrollX() - (this.getCurrentItem() * widthWithMargin); 
	    float scrollX = getScrollX() - distX - scrollOffset;
	 
	   
	    final float leftBound = Math.max(0, (this.getCurrentItem() - 1) * widthWithMargin);
	    final float rightBound = Math.min(this.getCurrentItem() + 1, this.getAdapter().getCount() - 1) * widthWithMargin;
	
	    if (scrollX < leftBound) {
	        scrollX = leftBound;
	        // Now we know that we've hit the bound, flip the page
	        if (this.getCurrentItem() > 0) {
	            this.setCurrentItem(this.getCurrentItem() - 1, false);
	        }
	    } 
	    else if (scrollX > rightBound) {
	        scrollX = rightBound;
	        // Now we know that we've hit the bound, flip the page
	        if (this.getCurrentItem() < (this.getAdapter().getCount() - 1) ) {
	            this.setCurrentItem(this.getCurrentItem() + 1, false);
	        }
	    }
	
	    // Do the fake dragging
	    if (mPageMoving) {
	        this.fakeDragBy(distX);
	    }
	    else {
	        this.beginFakeDrag();
	        this.fakeDragBy(distX);
	        mPageMoving = true;
	    
	    }
	
	}
	
	private void endFlingMotion() {
		mPageMoving = false;
	    this.endFakeDrag();
	    
	}
	
	// The fling runnable which moves the view pager and tracks decay
	private class FlingRunnable implements Runnable {
	    private Scroller mScroller; // Use this to store the points which will be used to create the scroll
	    private int mLastFlingX;
	
	    private FlingRunnable() {
	        mScroller = new Scroller(getContext());
	    }
	
	    public void startUsingVelocity(int initialVel) {
	        if (initialVel == 0) {
	            // There is no velocity to fling!
	            return;
	        }
	
	        removeCallbacks(this); // Stop pending flings
	
	        int initialX = initialVel < 0 ? Integer.MAX_VALUE : 0;
	        mLastFlingX = initialX;
	        // Setup the scroller to calulate the new x positions based on the initial velocity. Impose no cap on the min/max x values.
	        mScroller.fling(initialX, 0, initialVel, 0, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
	
	        // Run this runnable on the UI thread to let it handle the result of the fling motion.
			new Handler(Looper.getMainLooper()).post(this);
	    }
	
	    private void endFling() {
	        mScroller.forceFinished(true);
	        endFlingMotion();
	    }
	
	    @Override
	    public void run() {
	        final Scroller scroller = mScroller;
	        boolean animationNotFinished = scroller.computeScrollOffset();
	        final int x = scroller.getCurrX();
	        int delta = x - mLastFlingX;
	
	        trackMotion(delta); 
	
	        if (animationNotFinished) {
	            mLastFlingX = x;
	            post(this);
	        }
	        else {
	            endFling();
	        }
	
	    }
	}
	
	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distX, float distY) {
	
		mIsBeingScrolled = true;
	    trackMotion(-distX);
	   
	    return false;
	}
	
	// Unused Gesture Detector functions below
	@Override
	public boolean onDown(MotionEvent event) {
	    return false;
	}
	
	@Override
	public void onLongPress(MotionEvent event) {
	    // We don't want to do anything on a long press, though you should probably feed this to the page being long-pressed.
	}
	
	@Override
	public void onShowPress(MotionEvent event) {
	    // We don't want to show any visual feedback
	}
	
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
	    // We don't want to snap to the next page on a tap so ignore this
		
	    return false;
	}

}