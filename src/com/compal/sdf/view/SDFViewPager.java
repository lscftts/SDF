package com.compal.sdf.view;

import java.util.List;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.compal.sdf.SDFInterface;
import com.compal.sdf.config.SDFConfig;
import com.compal.sdf.util.Executer;
import com.compal.sdf.util.OnSDFExecutionListener;
import com.compal.sdf.util.OnSDFPageChangeListener;
import com.compal.sdf.util.SDFViewPagerAdapter;
import com.compal.sdf.util.SDFViewPagerTransformer;

public class SDFViewPager extends ViewPager implements SDFInterface {
	
	private SDFConfig mConfig = null;
	private Executer mExecuter = null;

	public SDFViewPager(Context context, SDFConfig config) {
		super(context);

		mConfig = config;
		
		setOverScrollMode(OVER_SCROLL_NEVER);
		setPageTransformer(false, new SDFViewPagerTransformer());
		
		mExecuter = new Executer(this);
		mConfig.setExecuter(mExecuter);
		
		setAdapter(new SDFViewPagerAdapter(context, mConfig));
		
		setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageSelected(int item) {
				if (mOnSDFPageChangeListener != null)
					mOnSDFPageChangeListener.onSDFPageChanged(SDFViewPager.this, getPageIdList().get(item));
			}
			
		});
	}

	@Override
	public boolean isParsed() {
		return mConfig.isParsed();
	}

	@Override
	public String getDescription() {
		return mConfig.getDescription();
	}

	@Override
	public int getPageId() {
		return getPageIdList().get(getCurrentItem());
	}

	@Override
	public boolean setPageId(int id) {
		Log.d("LLLLLL", "setPageId: " + id);
		int index = getItemFromPageId(id);
		if (index != -1) {
			setCurrentItem(index, true);
			return true;
		}
		return false;
	}
	
	private int getItemFromPageId(int id) {
		return getPageIdList().indexOf(id);
	}

	@Override
	public List<Integer> getPageIdList() {
		return mConfig.getPageIdList();
	}

	@Override
	public boolean execute(int functionId, String content) {
		if (mExecuter == null)
			return false;
		
		mExecuter.execute(functionId, content);
		return true;
	}

	@Override
	public boolean parseFile(Context context, String fileName) {
		SDFConfig config = SDFConfig.parseConfig(context, fileName);
		
		if (config.isParsed()) {
			mConfig = config;
			mConfig.setExecuter(mExecuter);
			setAdapter(new SDFViewPagerAdapter(context, config));
			return true;
		}
		
		return false;
	}

	@Override
	public View getView() {
		return this;
	}

	private OnSDFPageChangeListener mOnSDFPageChangeListener = null;
	private OnSDFExecutionListener  mOnSDFExecutionListener  = null;

	@Override
	public void setOnSDFPageChangeListener(OnSDFPageChangeListener l) {
		mOnSDFPageChangeListener = l;
	}

	@Override
	public void setOnSDFExecutionListener(OnSDFExecutionListener l) {
		mOnSDFExecutionListener = l;
		mExecuter.setOnSDFExecutionListener(mOnSDFExecutionListener);
	}

}
