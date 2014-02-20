package com.compal.sdf.view;

import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.compal.sdf.SDFInterface;
import com.compal.sdf.config.ObjectInfo;
import com.compal.sdf.config.SDFConfig;
import com.compal.sdf.util.Executer;
import com.compal.sdf.util.OnSDFExecutionListener;
import com.compal.sdf.util.OnSDFPageChangeListener;

public class SDFView extends View implements SDFInterface {
	
	private static final String LOG_TAG = "SDFView";
	
	private SDFConfig mConfig = null;
	private Executer mExecuter = null;
	
	private int mCurrentPage = -1;
	
	private int mPressedObjectId = ObjectInfo.INVALID_OBJECT_ID;

	public SDFView(Context context, SDFConfig config) {
		super(context);
		mConfig = config;
		
		mCurrentPage = mConfig.getStartPageId();
		
		if (!config.hasRegisteredExecuter()) {
			mExecuter = new Executer(this);
			mConfig.setExecuter(mExecuter);
		}
	}
	
	private boolean isValidState() {
		
		if (mCurrentPage == -1) {
			Log.e(LOG_TAG, "current page id is invalid.");
			return false;
		}
		
		if (mConfig == null) {
			Log.e(LOG_TAG, "config is null.");
			return false;
		}
		
		if (!mConfig.isParsed()) {
			Log.e(LOG_TAG, "config is not parsed.");
			return false;
		}
		
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if (!isValidState())
			return super.onTouchEvent(event);
		
		int x, y;
		x = (int) event.getX();
		y = (int) event.getY();
		
		boolean shouldHoldEvent = false;
		
		Log.d(LOG_TAG, "action: " + event.getAction());

		// Do something when touch event triggered:
		switch (event.getAction()) {

		// 1. Touch down:
		case MotionEvent.ACTION_DOWN:
			
			mPressedObjectId = mConfig.containButton(mCurrentPage, x, y);
			
			if ((mPressedObjectId != ObjectInfo.INVALID_OBJECT_ID)) {
				shouldHoldEvent = true;
				setPressed(true);
				invalidate();
			}
			
			break;

		// 2. Touch up:
		case MotionEvent.ACTION_UP:
			if (isPressed()) {
				setPressed(false);
				if (mPressedObjectId != ObjectInfo.INVALID_OBJECT_ID) {
					mConfig.execute(mCurrentPage, mPressedObjectId);
					shouldHoldEvent = true;
				}

				mPressedObjectId = ObjectInfo.INVALID_OBJECT_ID;
			}
			invalidate();
			
			break;
			
		case MotionEvent.ACTION_MOVE:
			
			if (mPressedObjectId != ObjectInfo.INVALID_OBJECT_ID)
				shouldHoldEvent = true;
			
			int pressedObjectId = mConfig.containButton(mCurrentPage, x, y);
			
			if (pressedObjectId != mPressedObjectId) {
				mPressedObjectId = ObjectInfo.INVALID_OBJECT_ID;
				invalidate();
			}
			
			break;
			
		case MotionEvent.ACTION_CANCEL:

			setPressed(false);
			invalidate();
			
			break;
			
		default:
			break;
		}
		
		return shouldHoldEvent;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		if (!isValidState())
			return;

		Log.d(LOG_TAG, "drawing..." + mPressedObjectId);
		if (isPressed()) {
			mConfig.draw(getContext(), canvas, mCurrentPage, mPressedObjectId);
		}
		else {
			mConfig.draw(getContext(), canvas, mCurrentPage, ObjectInfo.INVALID_OBJECT_ID);
		}
	}

	@Override
	public int getPageId() {
		return mCurrentPage;
	}

	@Override
	public boolean setPageId(int id) {
		if (!mConfig.isValidPageId(id))
			return false;
		
		if (mCurrentPage != id && mOnSDFPageChangeListener != null)
			mOnSDFPageChangeListener.onSDFPageChanged(this, id);
		
		mCurrentPage = id;
		invalidate();
		return true;
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
			invalidate();
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

	@Override
	public boolean isParsed() {
		if (mConfig != null)
			return mConfig.isParsed();
		
		return false;
	}

	@Override
	public String getDescription() {
		if (mConfig != null)
			return mConfig.getDescription();
		
		return null;
	}

}
