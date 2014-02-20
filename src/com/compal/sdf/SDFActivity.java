package com.compal.sdf;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;

public class SDFActivity extends Activity {

	private SDFInterface mSDF = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String filename = Environment.getExternalStorageDirectory().getPath() + java.io.File.separator
				+ "UI-Flow_ZBX00_Home.txt";
		
		mSDF = SDFUtil.create(this, filename);
		
		setContentView(mSDF.getView());
		
		mSDF.setPageId(102);
		//mSDF.invalidate();
	}

}
