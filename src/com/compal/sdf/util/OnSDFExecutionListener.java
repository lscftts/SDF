package com.compal.sdf.util;

import android.view.View;

public interface OnSDFExecutionListener {
	
	void onExecution(View view, String packageName, boolean isInstall);
}
