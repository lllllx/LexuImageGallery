package com.lexudev.imagegallery;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;


/*
 * This class, with a touch listener, extends from ImageView to create a single touchable image view for each page for the viewPager.
 * It darkens the background when it is clicked, by setting the dark background imageView visible.
 * It is to scale the tapped image to flip to full screen image.
 * 
 */
public class SimpleTouchImageView extends ImageView 
{
	boolean isTouchable = false;
	ImageView darkBG;
	ViewPager parentPager;
	View pageView;
	View mainView;
	int pageId;
	final int SCREENWIDTH;
	final int SCREENHEIGHT;
	ObjectAnimator scaleDownX; // Animator scale X of this to full screen
    ObjectAnimator scaleDownY; // Animator scale Y of this to full screen
    ObjectAnimator rotateY; // Animator to rotate around the Y axis.
    AnimatorSet flipAnimator;
    
	public static boolean anyOnFullScreen; // Whether any imageView is full screen
	private boolean thisOnFullScreen = false;// Whether this imageView is full screen
	
	public SimpleTouchImageView(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		
		// Acquire the resolution of the device
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		SCREENWIDTH = metrics.widthPixels;
		SCREENHEIGHT = metrics.heightPixels;
		
		anyOnFullScreen = false;
		
		this.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(isTouchable){
				
					switch(event.getAction())
					{
						case MotionEvent.ACTION_DOWN:
							return true;
						
						case MotionEvent.ACTION_UP:	
							darkBG.setVisibility(View.VISIBLE);
							
							// Run this on the UI thread to let it handle the animation.
							new Handler(Looper.getMainLooper()).post(new Runnable() {
							    @Override
							    public void run() {
							        ScaleToFullScreen();
							    }
							});
														
							break;
					}
				}	
				return false;
			}
			
		});	
	}
	
	public void EnableTouchListener(boolean _isTouchable)
	{
		isTouchable = _isTouchable;
		
	}
	
	public boolean getIsTouch()
	{
		return isTouchable;
	}
	
	public int getPageId()
	{
		return this.pageId;
	}
	
	public void ScaleToFullScreen()
	{
		// All the logic here only happens when no image is in full screen size
		if(!anyOnFullScreen)
		{
			float vertialScaleFactor = (float)SCREENHEIGHT / (this.getHeight() - 1);
			float horizontalScaleFactor = (float)SCREENWIDTH / (this.getWidth() - 1);			

			// Let the resized image fit the screen either vertically or horizontally, remaining the ratio.
			if(vertialScaleFactor > horizontalScaleFactor)
			{
				scaleDownX = ObjectAnimator.ofFloat(this, "scaleX",-1, -horizontalScaleFactor);
				scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", horizontalScaleFactor);		
			}
		
			else
			{	
				scaleDownX = ObjectAnimator.ofFloat(this, "scaleX", -1, -vertialScaleFactor);
				scaleDownY = ObjectAnimator.ofFloat(this, "scaleY", vertialScaleFactor);			
			}
		
			scaleDownX.setDuration(280);
			scaleDownY.setDuration(280);
			
			// Set delay to the scale animator. The image is mirrored by scaleX -1 when the image has almost rotated -90 degree, hence the flip will look smooth.
			scaleDownX.setStartDelay(240);
			scaleDownY.setStartDelay(240);
			
			flipAnimator = new AnimatorSet();
			

			rotateY = ObjectAnimator.ofFloat(this, "rotationY", 0f, -180f);
			rotateY.setDuration(500);
			rotateY.setInterpolator(new AccelerateDecelerateInterpolator());
					
			flipAnimator.play(rotateY).with(scaleDownY).with(scaleDownX);
			
			flipAnimator.start();
			anyOnFullScreen = true;
			thisOnFullScreen = true;
		}
	}
	
	public void SetContextDetails(ImageView _darkBG, ViewPager _parentPager, View _pageView, View _mainView, int _pos)
	{
		darkBG = _darkBG;
		parentPager = _parentPager;
		pageView = _pageView;
		mainView = _mainView;
		pageId  = _pos;
	
		
		if(pageId == 0)
		{
			isTouchable = true;
		}
	}

	public void SetFullScreen(boolean _isOnScreen)
	{
		SimpleTouchImageView.anyOnFullScreen = _isOnScreen;
		
	}
	
	/* This is the animation when the full screen image is swiped back to the gallery. The purpose of this function is not to animate, 
	 *  but rotate the image view back to the original, or it will be mirrored.
	 */
    public void FlipBackFromFullscreen()
    {
    	rotateY = ObjectAnimator.ofFloat(this, "rotationY", -180f, 0f);
		rotateY.setDuration(1);
		rotateY.setInterpolator(new AccelerateDecelerateInterpolator());
		
		rotateY.start();
    }
    
    // Whether this imageView is in full screen mode
    public boolean getIsImageOnFullScreen()
    {
    	return thisOnFullScreen;
    }
    
    // Reset the full screen mode of this imageview state 
    public void setIsImageOnFullScreen(boolean _onFullScreen)
    {
    	thisOnFullScreen = _onFullScreen;
    }

}
