package com.lexudev.imagegallery;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.SparseArray;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


public class MainActivity extends Activity 
{

	// Declaring variables
	FlingViewPager viewPager;
	SparseArray<View> pageViewCollection = new SparseArray<View>();
	PagerAdapter pagerAdapter;
	int[] imagesID;
	ImageView darkBackground; //create an imageView with a black picture to darken the background when an image is clicked
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        //initialize the group of images ID for the pager adapter
        imagesID = new int[]
        {
        	R.drawable.img_01,
        	R.drawable.img_02,
        	R.drawable.img_03,
        	R.drawable.img_04,
        	R.drawable.img_05,
        	R.drawable.img_06,
        	R.drawable.img_07,
        	R.drawable.img_08
        };
    	
        //This works like a dark filter to the background when users tap for a full screen image with a alpha value.
        //It will be made visible when users tap for full screen image.
        darkBackground = (ImageView)findViewById(R.id.darkImage);
        darkBackground.setAlpha(0.5f);
        darkBackground.setVisibility(View.INVISIBLE);
        
        /* Initialize the view pager
         * To make the previews for the next and last page, we need to set margin to the view pager by a negative value
         * Since the setPageMargin(float) function takes pixels into account, so here we need to set the parameter by some percentage of the screen resolution
         */
        viewPager = (FlingViewPager)findViewById(R.id.viewpager);
        viewPager.setClipToPadding(false);     
        

		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		int SCREENWIDTH = metrics.widthPixels;
		int margin =(int)(0.45 * SCREENWIDTH);
        viewPager.setPageMargin(-margin);
        viewPager.setOffscreenPageLimit(10);    
        viewPager.setPageTransformer(true, new ZoomOutPageTransfomer());
    
        //Change listener for the viewPager
        viewPager.addOnPageChangeListener(new OnPageChangeListener(){
			@Override
			public void onPageScrolled(int pos, float arg1, int arg2) 
			{
				// TODO Auto-generated method stub
				darkBackground.setVisibility(View.INVISIBLE);// When the user is scrolling, the background should always be bright			
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onPageSelected(int pos) {
				// TODO Auto-generated method stub
				
				/*
				 * This function is invoked when a new page is centered
				 * This function is to bring the centered page view to the top by setting the Z value. 
				 * this function also sets the previous and next view page back to its original Z value 0.
				 * Hence the page views wont overlap each other when the centered image is in full screen size
				 * 
				 * It also disable the touch listener of those pages that are not in the centered,
				 * but also enables the touch listener of the centered one
				 */
				int posRight = pos - 1;
				int posLeft = pos + 1;
				
				if(posRight >= 0)
				{
					View pageViewRight = pageViewCollection.get(posRight);
					if(pageViewRight != null)
						pageViewRight.setZ(0f);
					
					SimpleTouchImageView imageView = (SimpleTouchImageView)pageViewRight.findViewById(R.id.image);
					imageView.EnableTouchListener(false);
				}
				
				if(posLeft < imagesID.length)
				{
					View pageViewLeft = pageViewCollection.get(posLeft);
					if(pageViewLeft!= null)
						pageViewLeft.setZ(0f);
					
					SimpleTouchImageView imageView = (SimpleTouchImageView)pageViewLeft.findViewById(R.id.image);
					imageView.EnableTouchListener(false);
				}
				
				View view = pageViewCollection.get(pos);
				
				if(view != null)
				{
					view.setZ(1f);
					SimpleTouchImageView imageView = (SimpleTouchImageView)view.findViewById(R.id.image);
					imageView.EnableTouchListener(true);
				}
			}
        });
        
        //create an pagerAdapter and pass the the IDs of images to it
        View mainView = (View)findViewById(R.layout.activity_main);
        pagerAdapter = new ViewPagerAdapter(MainActivity.this, imagesID,darkBackground, viewPager, mainView, pageViewCollection);
        viewPager.setAdapter(pagerAdapter);
    }
}
