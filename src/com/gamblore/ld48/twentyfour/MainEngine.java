package com.gamblore.ld48.twentyfour;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.graphics.opengl.TextAtlas;
import android.graphics.Typeface;

import com.gamblore.ld48.twentyfour.worlds.MainMenu;
import com.gamblore.ld48.twentyfour.worlds.MainTitle;
import com.gamblore.ld48.twentyfour.worlds.StoryWorld;

public class MainEngine extends Engine {
	
	public static final String TAG = "MainEngine";
	public static Typeface mTypeface;
	
	public static Atlas mAtlas;
	
	public static Player PLAYER;
	
	public MainEngine(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		
		PLAYER = new Player();
		
		mAtlas = new Atlas("textures/atlas.xml");
		mTypeface = TextAtlas.getFontFromRes(R.raw.font_fixed_bold);
		
		//FP.setWorld(new WinMenu(25, 19));
		//FP.setWorld(new StoryWorld(R.string.intro_story));
		FP.setWorld(new MainMenu());
		//FP.setWorld(new MainMenu());
		
		//TODO load sounds
		//Log.d(TAG, "Loading sounds");
		//mBonk = new Sfx(R.raw.bonk);

	}
}
