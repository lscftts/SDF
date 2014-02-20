package com.compal.sdf;

import android.content.Context;

import com.compal.sdf.config.SDFConfig;
import com.compal.sdf.view.SDFView;
import com.compal.sdf.view.SDFViewPager;

public class SDFUtil {

	public static SDFInterface create(Context context, String configFileName) {
		
		SDFConfig config = SDFConfig.parseConfig(context, configFileName);
		
		if (config != null) {
			SDFInterface view = null;
			
			switch (config.getSDFType()) {
			case NORMAL:
				view = new SDFView(context, config);
				break;
			case VIEWPAGER:
				view = new SDFViewPager(context, config);
				break;
			default:
				break;
			}
			
			return view;
		}
		
		return null;
	}
	
}
