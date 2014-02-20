package com.compal.sdf.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.util.Log;
import android.view.View;

import com.compal.sdf.SDFInterface;

public class Executer implements ExecuterInterface {
	
	private static final String LOG_TAG = "Executer";
	
	private SDFInterface mSDFInterface = null;
	
	private OnSDFExecutionListener  mOnSDFExecutionListener  = null;
	
	public Executer(SDFInterface sdf) {
		mSDFInterface = sdf;
	}
	
	public void setOnSDFExecutionListener(OnSDFExecutionListener l) {
		mOnSDFExecutionListener = l;
	}

	@Override
	public void execute(int code, String input) {
		
		Log.d(LOG_TAG, "execute with code: " + code + " and input: " + input);
		
		if (code == 23) {
			mSDFInterface.setPageId(Integer.parseInt(input));
		}
		else if (code == 2001) {
			String apkName = input;
			String packageName = apkName.substring(0, apkName.lastIndexOf("."));
			
			if (!isPackageExists(packageName)) {
				if (mOnSDFExecutionListener != null)
					mOnSDFExecutionListener.onExecution(mSDFInterface.getView(), packageName, false);
			}
			else {
				Intent intent = ((Activity) getContext()).getPackageManager().getLaunchIntentForPackage(packageName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
				((Activity) getContext()).startActivity(intent);
				if (mOnSDFExecutionListener != null)
					mOnSDFExecutionListener.onExecution(mSDFInterface.getView(), packageName, true);
				((Activity) getContext()).overridePendingTransition(0, 0);
			}
			
		}
		else if (code == 2005) {
			String apkName = "cn.com.tiros.android.navidog.map.MapActivity";
			String packageName = "cn.com.tiros.android.navidog";
			Intent intent = new Intent();
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.setClassName(packageName, apkName);
			((Activity) getContext()).startActivity(intent);

			if (mOnSDFExecutionListener != null)
				mOnSDFExecutionListener.onExecution(mSDFInterface.getView(), packageName, false);
		}
		else if (code == 2006) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("geo:"));
			((Activity) getContext()).startActivityForResult(intent, 0);
		}
		else if (code == 2007) {
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse("http://www.baidu.com/"));
			((Activity) getContext()).startActivityForResult(intent, 0);
		}
		else if (code == 2008) {
			String apkName = input;

			if (mOnSDFExecutionListener != null)
				mOnSDFExecutionListener.onExecution(mSDFInterface.getView(), apkName, false);
		}
		else if (code == 2009) {
			callUIBC(input, 1);
		}
		else if (code == 2010) {
			callUIBC(input, 0);
		}
		
	}
	
	private void callUIBC(String input, int param) {
		String apkName = input;
		String packageName = apkName.substring(0, apkName.lastIndexOf("."));
		
		if (!isPackageExists(packageName)) {
			if (mOnSDFExecutionListener != null)
				mOnSDFExecutionListener.onExecution(mSDFInterface.getView(), packageName, false);
		}
		else {
			Intent intent = ((Activity) getContext()).getPackageManager().getLaunchIntentForPackage(packageName);
			intent.putExtra("UIBC", 1);
			((Activity) getContext()).startActivity(intent);
			if (mOnSDFExecutionListener != null)
				mOnSDFExecutionListener.onExecution(mSDFInterface.getView(), packageName, true);
		}
	}
	
	private boolean isPackageExists(String packageName) {
		
		try {
			getContext().getPackageManager().getPackageInfo(packageName, 0);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private Context getContext() {
		return ((View) mSDFInterface).getContext();
	}

}
