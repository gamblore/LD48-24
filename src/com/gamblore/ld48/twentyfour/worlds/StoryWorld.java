package com.gamblore.ld48.twentyfour.worlds;

import java.util.Vector;

import com.gamblore.ld48.twentyfour.MainEngine;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.utils.Input;
import android.util.Log;

public class StoryWorld extends World {

	private static final String TAG = "StoryWorld";
	
	private final float TIME_PER_LINE = 1.0f; 
	private String mStoryString;
	
	private String mSections[];
	private Vector<AtlasGraphic> mGraphics = new Vector<AtlasGraphic>();
	
	private int mCurrentLine = -1;
	
	private float mTimeUntilUpdate = 0;
	
	private ColorTween mColorTween = new ColorTween(null, PERSIST);
	
	public StoryWorld(int stringResourceId) {
		super();
		
		FP.activity.setOnBackCallback(MainEngine.IN_GAME_BACK_CALLBACK);
		
		mStoryString = FP.context.getString(stringResourceId);
		
		mSections = mStoryString.split("\\n");
		
		Entity e = new Entity();
		GraphicList gl = new GraphicList();
		
		for (int i = 0; i < mSections.length; i++) {
			if ("".equals(mSections[i])) {
				continue;
			}
			AtlasText line = new AtlasText(mSections[i], 20, MainEngine.mTypeface);
			line.visible = false;
			line.x = 2;
			line.y = i * 22;
			line.setColor(0);
			
			mGraphics.add(line);
			gl.add(line);
		}
		
		e.setGraphic(gl);
		
		add(e);
		addTween(mColorTween);
	}

	@Override
	public void update() {
		super.update();
		mTimeUntilUpdate -= FP.elapsed;
		
		if (Input.mousePressed) {
			if (mCurrentLine != mGraphics.size()-1) {
				mTimeUntilUpdate = -1;
			}
		}
		
		if (mTimeUntilUpdate < 0) {
			mCurrentLine++;
			if (mCurrentLine < mGraphics.size()) {
				Log.d(TAG, "Displaying " + mCurrentLine + " " + mSections[mCurrentLine]);
			}
			if (mCurrentLine == mGraphics.size()) {
				FP.setWorld(new MainMenu());
				return;
			} else if (mCurrentLine == mGraphics.size()-1) {
				mTimeUntilUpdate = 2.0f;
			} else {
				mTimeUntilUpdate = TIME_PER_LINE;
			}
			AtlasGraphic line = mGraphics.get(mCurrentLine);
			line.visible = true;
			mColorTween.tween(TIME_PER_LINE, 0x33ffffff, 0xffffffff);
		}
		
		if (mCurrentLine > 0) {
			mGraphics.get(mCurrentLine-1).setColor(0xffffffff);
		}
		if (mCurrentLine < mGraphics.size()) {
			mGraphics.get(mCurrentLine).setColor(mColorTween.color);
		}
	}
	
}
