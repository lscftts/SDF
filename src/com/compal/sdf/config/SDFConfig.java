package com.compal.sdf.config;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;

import com.compal.sdf.util.Executer;
import com.compal.sdf.util.ExecuterInterface;

public class SDFConfig {
	
	public enum SDFType {
		NORMAL,
		VIEWPAGER,
	}
	
	private static final String LOG_TAG = "SDFConfig";
	
	public static SDFConfig parseConfig(Context context, CharSequence fileName) {
		SDFConfig sdfConfig = new SDFConfig(context, fileName);
		
		if (sdfConfig.isParsed())
			return sdfConfig;
		
		return null;
	}
	
	private boolean mParsed = false;
	
	private CharSequence mFileName = "";
	
	public SDFType mSDFType = SDFType.NORMAL;
	
	public String mVersion = "V0.0.0";
	public String mAuthor = "Nobody";
	public String mDate = "19000101";
	public String mDescription = "";

	public int mStartPageID = 0;
	
	public int mPageNum = 0;
	public int mScreenWidth = 800;
	public int mScreenHeight = 480;
	public String mImageFolder = Environment.getExternalStorageDirectory().getParent();
	
	private List<PageInfo> mPageInfo = null;
	
	private float mScaleX = 1.0f;
	private float mScaleY = 1.0f;
	
	
	public SDFConfig(Context context, CharSequence configFileName) {
		
		DisplayMetrics dm = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);

		int width = dm.widthPixels;
		int height = dm.heightPixels;

		parseFile(configFileName, width, height);
	}
	
	public boolean isParsed() {
		return mParsed;
	}
	
	private boolean parseFile(CharSequence fileName, int width, int height) {
		
		mFileName = fileName;
		mParsed = false;
		
		try {
	
			File file = new File((String) mFileName);
			FileInputStream inputStream = new FileInputStream(file);
		
			int length = inputStream.available();
			byte[] bytes = new byte[length];
			ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		
			while (inputStream.read(bytes) != -1) {
				arrayOutputStream.write(bytes, 0, bytes.length);
			}
		
			inputStream.close();
			arrayOutputStream.close();
		
			String content = new String(arrayOutputStream.toByteArray());
			
			int pageIndex = content.indexOf("|<Page");
			if (pageIndex > 0) {
				String headerContent = content.substring(0, pageIndex);
				
				parseConfigHeader(headerContent);

				mScaleX = (float) width  / (float) mScreenWidth ;
				mScaleY = (float) height / (float) mScreenHeight;
				
				if (mPageInfo == null)
					mPageInfo = new ArrayList<PageInfo>();
				mPageInfo.clear();

				String leftContent = content.substring(pageIndex, content.length());

				while(pageIndex != -1) {
					int start = 0;
					pageIndex = leftContent.indexOf("|<Page", 1);
					int end = pageIndex;
					if (end == -1)
						end = leftContent.length();
					
					String pageContent = leftContent.substring(start, end);
					
					PageInfo pageInfo = new PageInfo(pageContent, mImageFolder, mScaleX, mScaleY);
					if (pageInfo.isPageParsed())
						mPageInfo.add(pageInfo);
					else
						return false;
					
					if (pageIndex != -1)
						leftContent = leftContent.substring(pageIndex, leftContent.length());
				}
			}
			
			Log.d(LOG_TAG, "Parsed successfully.");
			
			mParsed = true;
		
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			mParsed = false;
		} catch (IOException e) {
			e.printStackTrace();
			mParsed = false;
		}
		
		return mParsed;
	}
	
	private void parseConfigHeader(String content) {

		String[] splitStringArray = content.split("\\|");

		for (String splitString : splitStringArray) {

			if (splitString.indexOf("PicPath=") == 0) {
				mImageFolder = splitString.substring(8, splitString.length());

				mImageFolder = Environment.getExternalStorageDirectory().getPath()
						+ java.io.File.separator + mImageFolder;

			}
			else if (splitString.indexOf("StartPageID=") == 0) {
				mStartPageID = Integer.parseInt(splitString.substring(12, splitString.length()));
			}
			else if (splitString.indexOf("AllPage=") == 0) {
				mPageNum = Integer.parseInt(splitString.substring(8, splitString.length()));
			}
			else if (splitString.indexOf("Version=") == 0) {
				mVersion = splitString.substring(8, splitString.length());
			}
			else if (splitString.indexOf("Description=") == 0) {
				mDescription = splitString.substring(12, splitString.length());
			}
			else if (splitString.indexOf("Screen_Width=") == 0) {
				mScreenWidth = Integer.parseInt(splitString.substring(13, splitString.length()));
			}
			else if (splitString.indexOf("Screen_Height=") == 0) {
				mScreenHeight = Integer.parseInt(splitString.substring(14, splitString.length()));
			}
			else if (splitString.indexOf("SDFType=") == 0) {
				String type = splitString.substring(8, splitString.length());
				if (type.contains("Normal"))
					mSDFType = SDFType.NORMAL;
				else if (type.contains("ViewPager"))
					mSDFType = SDFType.VIEWPAGER;
			}
		}
	}
	
	public boolean isValidPageId(int id) {
		if (mPageInfo != null) {
			for (PageInfo info : mPageInfo) {
				if (info.getId() == id)
					return true;
			}
		}
		
		return false;
	}
	
	private PageInfo getPageInfo(int id) {
		
		if (!isValidPageId(id))
			return null;

		if (mPageInfo != null) {
			for (PageInfo info : mPageInfo) {
				if (info.getId() == id)
					return info;
			}
		}
		
		return null;
	}
	
	public boolean draw(Context context, Canvas canvas, int pageId, int pressedObjectId) {
		
		PageInfo page = getPageInfo(pageId);
		
		if (page == null) {
			Log.e(LOG_TAG, "pageinfo is null");
			return false;
		}

		Log.d(LOG_TAG, "drawing...");
		page.drawPage(context, canvas, pressedObjectId);
		
		return true;
	}
	
	public int containButton(int page, int x, int y) {
		if (mPageInfo == null)
			return ObjectInfo.INVALID_OBJECT_ID;
		
		if (!isValidPageId(page))
			return ObjectInfo.INVALID_OBJECT_ID;
		
		PageInfo pageInfo = getPageInfo(page);
		
		return pageInfo.containButton(x, y);
	}
	
	public List<Integer> getPageIdList() {
		List<Integer> list = new ArrayList<Integer>();
		
		for (PageInfo info : mPageInfo) {
			list.add(info.getId());
		}
		
		return list;
	}
	
	public int getStartPageId() {
		return mStartPageID;
	}
	
	private boolean mHasRegisteredExecuter = false;
	
	public void setExecuter(ExecuterInterface executer) {
		
		mHasRegisteredExecuter = false;
		
		if (executer != null) {
			if (mPageInfo != null) {
				for (PageInfo page : mPageInfo) {
					page.setExecuter(executer);
				}
				mHasRegisteredExecuter = true;
			}
		}
	}
	
	public boolean hasRegisteredExecuter() {
		return mHasRegisteredExecuter;
	}
	
	public boolean execute(int pageId, int objectId) {

		PageInfo page = getPageInfo(pageId);
		
		if (page == null)
			return false;
		
		page.execute(objectId);
		return true;
	}
	
	public String getDescription() {
		return mDescription;
	}
	
	public SDFType getSDFType() {
		return mSDFType;
	}

}
