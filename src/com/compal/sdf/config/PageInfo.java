package com.compal.sdf.config;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;

import com.compal.sdf.config.ObjectInfo.ObjectState;
import com.compal.sdf.config.ObjectInfo.ObjectType;
import com.compal.sdf.util.ExecuterInterface;

public class PageInfo {
	
	private static final String LOG_TAG = "PageInfo";
	
	private int mPageID = 0;
	private List<ObjectInfo> mObject;
	
	private boolean mPageParsed = false;
	
	public PageInfo(String content, String imageFolder, float scaleX, float scaleY) {
		parseContent(content, imageFolder, scaleX, scaleY);
	}
	
	public boolean isPageParsed() {
		return mPageParsed;
	}
	
	private void parseContent(String content, String imageFolder, float scaleX, float scaleY) {
		
		int pageId = content.indexOf("<Page=");
		if (pageId == -1) {
			mPageParsed = false;
			return;
		}
		
		int pageIdEnd = content.indexOf(">", pageId);
		try {
			mPageID = Integer.parseInt(content.substring(pageId+6, pageIdEnd));
		}
		catch (NumberFormatException e) {
			e.printStackTrace();
			mPageParsed = false;
			return;
		}
		
		int firstObject = content.indexOf("|@");
		
		if (firstObject == -1) {
			mPageParsed = false;
			return;
		}
		
		mObject = new ArrayList<ObjectInfo>();
		
		String contentObjects = content.substring(firstObject, content.length());
		String[] objectArrays = contentObjects.split("\\|@\\|");
		
		mPageParsed = true;
		for (String object : objectArrays) {
			
			if (object.contains("|$|")) {

				ObjectInfo info = new ObjectInfo(object, imageFolder, scaleX, scaleY);
				info.setId(mObject.size()+1);
				if (info.isObjectParsed())
					mObject.add(info);
				else {
					mPageParsed = false;
					break;
				}
			
			}
			
		}

	}
	
	public int getId() {
		return mPageID;
	}
	
	public int getObjectSize() {
		if (mObject != null)
			return mObject.size();
		
		return -1;
	}
	
	public boolean isValidObjectId(int id) {
		return getObject(id) != null;
	}
	
	public ObjectInfo getObject(int id) {
		for (int i=0; i<getObjectSize(); i++) {
			if (mObject.get(i).getId() == id)
				return mObject.get(i);
		}
		return null;
	}
	
	public int containButton(int x, int y) {
		if (mObject == null)
			return ObjectInfo.INVALID_OBJECT_ID;
		
		for (int i=0; i<getObjectSize(); i++) {
			ObjectInfo object = mObject.get(i);
			
			if (object.contain(x, y) && object.getType() == ObjectType.BUTTON)
				return object.getId();
		}

		return ObjectInfo.INVALID_OBJECT_ID;
	}
	
	public boolean drawPage(Context context, Canvas canvas, int pressedObjectId) {
		
		for (int i=0; i<getObjectSize(); i++) {
			ObjectInfo object = mObject.get(i);
			
			if (pressedObjectId == object.getId())
				object.drawObject(context, canvas, ObjectState.PRESSED);
			else
				object.drawObject(context, canvas, ObjectState.RELEASED);
		}
		
		return true;
	}
	
	public void setExecuter(ExecuterInterface executer) {
		if (mObject != null) {
			for (ObjectInfo object : mObject) {
				object.setExecuter(executer);
			}
		}
	}
	
	public void execute(int objectId) {
		ObjectInfo object = getObject(objectId);
		
		if (object != null)
			object.execute();
	}
}
