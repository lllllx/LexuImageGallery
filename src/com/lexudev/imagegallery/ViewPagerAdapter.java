
package com.lexudev.imagegallery;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/* This is the  customized adaptor for the viewPager 
 * It's to populate pages inside of the viewPager
 */

public class ViewPagerAdapter extends PagerAdapter 
{
	Context context;
	int[] imagesID;
	LayoutInflater inflater;
	ImageView darkBG;
	ViewPager viewPager;
	View mainView;
	SparseArray<View> pageViewCollection;

	public ViewPagerAdapter(Context _context, int[] _imagesID, ImageView _darkBG, ViewPager _viewPager, View _mainView, SparseArray<View> collection) 
	{
		context = _context;
		imagesID = _imagesID;
		darkBG = _darkBG;
		viewPager = _viewPager;
		mainView = _mainView;
		pageViewCollection = collection;
	}

	@Override
	public int getCount() 
	{
		return imagesID.length;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) 
	{
		return view ==((RelativeLayout)object);
	}

	/*
	 * (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#instantiateItem(android.view.ViewGroup, int)
	 * Create the page for the given position
	 */
	
	@Override
	public Object instantiateItem(ViewGroup container, final int position) 
	{
		inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View imagePageView = inflater.inflate(R.layout.view_pager, container, false);
		
		//locate this image view to the image view in view_pager.xml file
		SimpleTouchImageView image = (SimpleTouchImageView)imagePageView.findViewById(R.id.image);		
		image.setImageResource(imagesID[position]);
		image.SetContextDetails(darkBG, viewPager, imagePageView, mainView, position);
		
		pageViewCollection.put(position, imagePageView);
		((ViewPager)container).addView(imagePageView);
		
		return imagePageView;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object)
	{
		//Remove view_pager.xml from ViewPager
		((ViewPager)container).removeView((RelativeLayout)object);
		pageViewCollection.remove(position);
	}

}
