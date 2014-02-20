package com.compal.sdf;

import java.util.List;

import com.compal.sdf.util.OnSDFExecutionListener;
import com.compal.sdf.util.OnSDFPageChangeListener;

import android.content.Context;
import android.view.View;

public interface SDFInterface {
	
	boolean isParsed();
	String getDescription();

	int getPageId();
	boolean setPageId(int id);
	
	List<Integer> getPageIdList();
	
	boolean execute(int functionId, String content);
	
	void invalidate();
	boolean parseFile(Context context, String fileName);
	
	View getView();
	
	void setOnSDFPageChangeListener(OnSDFPageChangeListener l);
	void setOnSDFExecutionListener(OnSDFExecutionListener l);
	
}
