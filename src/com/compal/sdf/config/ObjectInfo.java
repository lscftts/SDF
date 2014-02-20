package com.compal.sdf.config;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.util.Log;

import com.compal.sdf.util.ExecuterInterface;
import com.compal.sdf.util.SDFImageLoader;

public class ObjectInfo {
	
	private static final String LOG_TAG = "ObjectInfo";
	
	public static final int INVALID_OBJECT_ID = -1;
	
	public enum ObjectState {
		RELEASED,
		PRESSED,
		DISABLED
	}
	
	public enum ObjectType {
		NULL,
		IMAGE,
		BUTTON,
		TEXT,
		SLIPUP,
		SLIPDOWN,
		SLIPRIGHT,
		SLIPLEFT
	}
	
	public enum TextLocation {
		LEFT,
		MIDDLE,
		RIGHT
	}
	
	private String mObjectName;
	private int mID;
	private int mNextID;
	private ObjectType mType;
	private Rect mLocation;
	private int mTextColor; // R&G&B
	private TextLocation mTextLocation; // ??
	private String mTextContent;
	private int mTextSize;
	private String[] mImagePath; // Pressed/Released/Disabled
	private int[] mInit_FID;
	private int[] mPressed_FID;
	private int[] mReleased_FID;
	private int mInitCount;
	private int mPressedCount;
	private int mReleasedCount;
	private String[] mInputFID;
	private int mInputFIDCount;
	
	private boolean mObjectParsed = false;
	
	private ExecuterInterface mExecuter = null;
	
	public ObjectInfo(String content, String imageFolder, float scaleX, float scaleY) {
		parseContent(content, imageFolder, scaleX, scaleY);
	}
	
	public boolean isObjectParsed() {
		return mObjectParsed;
	}
	
	private void parseContent(String content, String imageFolder, float scaleX, float scaleY) {
		
		String[] splitObjects = content.split("\\|");
		
		List<String> buffer = new ArrayList<String>();
		for (String object : splitObjects) {
			buffer.add(object);
		}
		
		mObjectParsed = parseObject(buffer, imageFolder, scaleX, scaleY);
		
	}
	
	private boolean parseObject(List<String> buffer, String imageFolder, float scaleX, float scaleY) {
		
		try {

			mObjectName = buffer.get(0);
	
			mID = Integer.parseInt(buffer.get(2));
			mNextID = Integer.parseInt(buffer.get(3));
	
			if (buffer.get(4).indexOf("Button") == 0) {
				mType = ObjectType.BUTTON;
			} else if (buffer.get(4).indexOf("Image") == 0) {
				mType = ObjectType.IMAGE;
			} else if (buffer.get(4).indexOf("Text") == 0) {
				mType = ObjectType.TEXT;
			} else if (buffer.get(4).indexOf("SlipUp") == 0) {
				mType = ObjectType.SLIPUP;
			} else if (buffer.get(4).indexOf("SlipDown") == 0) {
				mType = ObjectType.SLIPDOWN;
			} else if (buffer.get(4).indexOf("SlipRight") == 0) {
				mType = ObjectType.SLIPRIGHT;
			} else if (buffer.get(4).indexOf("SlipLeft") == 0) {
				mType = ObjectType.SLIPLEFT;
			} else {
				mType = ObjectType.NULL;
				return false;
			}
	
			String[] location = buffer.get(5).split("&");
			int l, t, r, b;
			l = (int) (((float) Integer.parseInt(location[0])) * scaleX);
			t = (int) (((float) Integer.parseInt(location[1])) * scaleY);
			r = (int) (((float) Integer.parseInt(location[2])) * scaleX);
			b = (int) (((float) Integer.parseInt(location[3])) * scaleY);
			r += l;
			b += t;
			mLocation = new Rect(l, t, r, b);
	
			if (mType == ObjectType.TEXT) {
				String[] Textinfo = buffer.get(6).split("&");
				mTextContent = Textinfo[0].substring(1, Textinfo[0].length() - 1);
				mTextColor = Color.rgb(Integer.parseInt(Textinfo[1]), Integer.parseInt(Textinfo[3]), Integer.parseInt(Textinfo[5]));

				if (Textinfo[6].matches("Middle"))
					mTextLocation = TextLocation.MIDDLE;
				if (Textinfo[6].matches("Left"))
					mTextLocation = TextLocation.LEFT;
				if (Textinfo[6].matches("Right"))
					mTextLocation = TextLocation.RIGHT;
				
				mTextSize = (int) (Integer.parseInt(Textinfo[7]) * scaleX);
	
			}
	
			mImagePath = new String[3];
			String[] imagePath = buffer.get(7).split("&");
			for (int i = 0; i < imagePath.length; i++) {
				mImagePath[i] = imageFolder + imagePath[i];
			}
	
			String functionID = buffer.get(9);
	
			mInitCount = 0;
			mPressedCount = 0;
			mReleasedCount = 0;
	
			if (functionID.indexOf("Pressed") != -1) {
				String pressedFunctionID = functionID.substring(functionID.indexOf("Pressed") + 8);
				pressedFunctionID = pressedFunctionID.substring(0, pressedFunctionID.indexOf(")"));
				String[] pressedFunctionIDs = pressedFunctionID.split("&", 0);
				mPressedCount = 0;
				mPressed_FID = new int[15];
				for (int i = 0; i < pressedFunctionIDs.length; i++) {
					if (pressedFunctionIDs[i].length() > 0) {
						mPressed_FID[mPressedCount] = Integer.parseInt(pressedFunctionIDs[i]);
						mPressedCount++;
					}
				}
			}
	
			if (functionID.indexOf("Released") != -1) {
				String releasedFunctionID = functionID.substring(functionID.indexOf("Released") + 9);
				releasedFunctionID = releasedFunctionID.substring(0, releasedFunctionID.indexOf(")"));
				String[] releasedFunctionIDs = releasedFunctionID.split("&", 0);
				mReleasedCount = 0;
				mReleased_FID = new int[15];
				for (int i = 0; i < releasedFunctionIDs.length; i++) {
					if (releasedFunctionIDs[i].length() > 0) {
						mReleased_FID[mReleasedCount] = Integer.parseInt(releasedFunctionIDs[i]);
						mReleasedCount++;
					}
				}
			}
	
			if (functionID.indexOf("Init") != -1) {
				String initFunctionID = functionID.substring(functionID.indexOf("Init") + 5);
				initFunctionID = initFunctionID.substring(0, initFunctionID.indexOf(")"));
				String[] initFunctionIDs = initFunctionID.split("&", 0);
				mInitCount = 0;
				mInit_FID = new int[15];
				for (int i = 0; i < initFunctionIDs.length; i++) {
					if (initFunctionIDs[i].length() > 0) {
						mInit_FID[mInitCount] = Integer.parseInt(initFunctionIDs[i]);
						mInitCount++;
					}
				}
			}
	
			String[] inputFunctionID = buffer.get(10).split("&", 0);
			mInputFIDCount = 0;
			mInputFID = new String[15];
			for (int i = 0; i < inputFunctionID.length; i++) {
				if (inputFunctionID[i].length() > 0) {
					mInputFID[mInputFIDCount] = inputFunctionID[i];
					mInputFIDCount++;
				}
			}
		}
		catch (IndexOutOfBoundsException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	public void drawObject(Context context, Canvas canvas) {
		drawObject(context, canvas, ObjectState.RELEASED);
	}
	
	public void drawObject(Context context, Canvas canvas, ObjectState state) {
		
		String imagePath = mImagePath[1];
		
		if (state != ObjectState.PRESSED) {
			imagePath = mImagePath[0];
		} 
		
		Bitmap bm = SDFImageLoader.getImage(context, imagePath);
		
		if (bm != null)
			canvas.drawBitmap(bm, mLocation.left, mLocation.top, new Paint());
			
		if (mTextContent != null) {
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setTextSize(mTextSize);
			paint.setColor(mTextColor);

			if (mTextLocation == TextLocation.MIDDLE)
				paint.setTextAlign(Align.CENTER);
			else
				paint.setTextAlign(Align.LEFT);
			Log.d(LOG_TAG, mTextContent);

			canvas.drawText(mTextContent, mLocation.left, mLocation.top, paint);
		}
		
	}
	
	public int getId() {
		return mID;
	}
	
	public void setId(int id) {
		mID = id;
	}
	
	public boolean contain(int x, int y) {

		return mLocation.contains(x, y);
	}
	
	public ObjectType getType() {
		return mType;
	}
	
	public String getName() {
		return mObjectName;
	}
	
	public void execute() {
		
		if (mExecuter == null)
			return;
		
		for (int i = 0; i < mReleasedCount; i++) {
			int releasedOffset = mInitCount + mPressedCount;
			int code = mReleased_FID[i];
			String input = mInputFID[releasedOffset + i];
			
			mExecuter.execute(code, input);
		}
	}
	
	public void setExecuter(ExecuterInterface executer) {
		mExecuter = executer;
	}
	
	/*
	
	public Rect getLocation() {
		return mLocation;
	}
	
	public int getTextColor() {
		return mTextColor;
	}
	
	public TextLocation getTextLocation() {
		return mTextLocation;
	}
	
	public int getTextSize() {
		return mTextSize;
	}
	
	public String getTextContent() {
		return mTextContent;
	}
	
	*/

}
