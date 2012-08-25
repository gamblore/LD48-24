package com.gamblore.ld48.twentyfour;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.opengl.Atlas;
import android.graphics.Typeface;

public class MainEngine extends Engine {
	
	public static final String TAG = "MainEngine";
	public static Typeface mTypeface;
	
	public static Atlas mAtlas;
	
	public MainEngine(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		
		mAtlas = new Atlas("textures/atlas.xml");
		mTypeface = Text.getFontFromRes(R.raw.font_fixed_bold);
		
		//FP.setWorld(new MenuWorld());
		FP.setWorld(new FightWorld());
		
		//TODO load sounds
		//Log.d(TAG, "Loading sounds");
		//mBonk = new Sfx(R.raw.bonk);

	}
}
