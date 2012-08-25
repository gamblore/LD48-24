package com.gamblore.ld48.twentyfour;

import net.androidpunk.FP;
import net.androidpunk.android.PunkActivity;

public class MainActivity extends PunkActivity {
	static {

        PunkActivity.static_width = 480; 
        PunkActivity.static_height = 800;
        
        // Set this to a class that extends Engine
        PunkActivity.engine_class = MainEngine.class; 
        
        // If you want fixed time between frames set these. Value is in frames per second. 
        //FP.fixed = true;
        //FP.assignedFrameRate = 30;
        
        FP.debug = true;
	}
}
