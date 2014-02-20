package com.compal.sdf.util;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.compal.sdf.config.SDFConfig;
import com.compal.sdf.view.SDFView;

public class SDFViewPagerAdapter extends PagerAdapter {
	
	private Context mContext = null;
	
	private SDFConfig mConfig = null;
	private List<Integer> mPageIdList = null;
	
	public SDFViewPagerAdapter(Context context, SDFConfig config) {

		mContext = context;
		mConfig = config;
		
		mPageIdList = mConfig.getPageIdList();
	}

	@Override
	public int getCount() {
		return mPageIdList.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View) object);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		
		if (position < 0 || position >= getCount())
			return null;
		
		SDFView view = new SDFView(mContext, mConfig);
		container.addView(view, -1, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		view.setPageId(mPageIdList.get(position));

		return view;
	}
    
}
