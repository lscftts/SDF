package com.compal.sdf.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.LruCache;
import android.util.Log;

public class SDFImageLoader {
	
	private static LruCache<String, Bitmap> mMemoryCache = null;
	
	public static Bitmap getImage(Context context, String fileName) {
		
		Bitmap bm = null;
		
		boolean shouldPutInCache = false;

		if (mMemoryCache == null)
			initialMemoryCache();
		
		bm = mMemoryCache.get(fileName);
		
		if (bm != null) {
			Log.d("IMAGELOADER", "found: " + fileName);
			shouldPutInCache = false;
		}
		
		if (bm == null) {
			Log.d("IMAGELOADER", "load: " + fileName);
			bm = BitmapFactory.decodeFile(fileName);
			shouldPutInCache = true;
		}
		
		if (bm != null && shouldPutInCache) {
			mMemoryCache.put(fileName, bm);
		}
		
		return bm;
	}
	
	private static void initialMemoryCache() {
		
		if (mMemoryCache == null) {

			final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

			// Use 1/8th of the available memory for this memory cache.
			final int cacheSize = maxMemory / 8;
		
			mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {
		        @Override
		        protected int sizeOf(String key, Bitmap bitmap) {
		            // The cache size will be measured in kilobytes rather than
		            // number of items.
		            return bitmap.getByteCount() / 1024;
		        }
			};
	    }
		
	}
	
}
