package com.lexudev.imagegallery;
import android.support.v4.view.ViewPager;
import android.view.View;


/* This is an implementation of PageTransformer interface. Which is used to customize the animation by rooming out the center image.
 * It realizes the effect that the centered image zooms out.
 * It realizes the effect that the centered image will fade away to the side.
 */
public class ZoomOutPageTransfomer implements ViewPager.PageTransformer
{

	private static final float MIN_SCALE = 0.909090f; // To Zoom the centered out image to 110%, so zoom it in by 1/1.1
	private static final float MIN_ALPHA = 0.6f;
	public static boolean FULL_Screen_Mode = false; // If a image is shown in full screen size, it will be true. By default it is false
	

	public void transformPage(View pageView, float position) 
	{
		SimpleTouchImageView image = (SimpleTouchImageView)pageView.findViewById(R.id.image);

		image.SetFullScreen(false);
		
		// Get the dimension of the image which the transformation will be applied to
		int imageWidth = image.getWidth();
		int imageHeight = image.getHeight();
		
		if(image.getIsImageOnFullScreen())
		{
			image.FlipBackFromFullscreen();
			image.setIsImageOnFullScreen(false);
		}
		
		if(Math.abs(position) <= 2) 
		{
			// If the page is centered, it won't get scaled. If the page is from the left or right to the centered, it will be scaled to smaller one.
			float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
			float verticalMargin = imageHeight * (1 - scaleFactor)/2;
			float horizontalMargin = imageWidth * (1 - scaleFactor)/2;
			
			// Adjust the new position after changing the size
			if(position < 0)
			{
				image.setTranslationX(horizontalMargin - verticalMargin/2);				
			}	
			else
			{
				image.setTranslationX(-horizontalMargin + verticalMargin/2);
			}
			
			image.setScaleX(scaleFactor);
			image.setScaleY(scaleFactor);
				 
			image.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)/(1f - MIN_SCALE)*(1f-MIN_ALPHA));		 
		}
	}
}
