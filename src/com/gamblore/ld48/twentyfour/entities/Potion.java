package com.gamblore.ld48.twentyfour.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import android.util.Log;

import com.gamblore.ld48.twentyfour.MainEngine;

public class Potion extends Entity {
	private static final String TAG = "Potion";
	
	public static final String TYPE = "potion";
	
	private static final String[] TYPES = new String[] {"red_potion", "yellow_potion", "green_potion", "blue_potion"};
	private static final int[] COSTS = new int[] {100, 50, 75, 100};
	
	private static final int[] FRAMES = FP.frames(0, 2);
	private static final int[] FRAMES_POUR = FP.frames(3, 5);
	private static final String ANIM_BUBBLE = "bubble";
	private static final String ANIM_POUR = "pour";
	
	private SpriteMap mMap;
	private int mPotionType;
	
	public Potion(int potionType) {
		super();
		
		mPotionType = potionType;
		if (potionType < 1 || potionType > 4) {
			Log.e(TAG, "Invalid Potion Type!");
			mPotionType = 0;
		}
		
		SubTexture st = MainEngine.mAtlas.getSubTexture(TYPES[mPotionType-1]);
		mMap = new SpriteMap(st, st.getWidth()/6, st.getHeight());
		
		setGraphic(mMap);
		
		mMap.add(ANIM_BUBBLE, FRAMES, 10);
		mMap.add(ANIM_POUR, FRAMES_POUR, 10);
		
		mMap.setAnimFrame(ANIM_BUBBLE, FP.rand(3));
		mMap.play(ANIM_BUBBLE, false);
		
		mMap.scale = 3;
		
		setHitbox((int)(mMap.getFrameWidth() * mMap.scale * mMap.scaleX), (int)(mMap.getFrameHeight() * mMap.scale * mMap.scaleY));
		
		setType(TYPE);
	}
	
	public boolean isPouring() {
		return ANIM_POUR.equals(mMap.getCurrentAnim());
	}
	
	public void togglePour() {
		if (isPouring()) {
			mMap.setAnimFrame(ANIM_BUBBLE, FP.rand(3));
			mMap.play(ANIM_BUBBLE);
			mMap.angle = 0;
		} else {
			mMap.setAnimFrame(ANIM_POUR, 3+FP.rand(3));
			mMap.play(ANIM_POUR);
			mMap.angle = 180;
		}
		
	}
	
	public int getAngle() {
		return (int)mMap.angle;
	}
	
	public void setAngle(int angle) {
		mMap.angle = angle;
	}
	
	public int getPotionType() {
		return mPotionType;
	}
	
	public int getCost() {
		return COSTS[mPotionType-1];
	}
}
