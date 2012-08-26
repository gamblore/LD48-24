package com.gamblore.ld48.twentyfour.entities;

import net.androidpunk.Entity;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.tweens.motion.LinearPath;

import com.gamblore.ld48.twentyfour.MainEngine;

public class Ant extends Entity {

	public static final String TYPE = "ant";
	// pulled from defense.
	private int mLife, mLifeMax;
	
	private AntGroup mGroup;
	
	private SpriteMap mMap;
	
	private LinearPath mMovementTween;
	
	private ColorTween mColorTween;
	
	public Ant(AntGroup group, int maxLife) {
		mGroup = group;
		mLife = mLifeMax = maxLife;
		
		SubTexture ant = MainEngine.mAtlas.getSubTexture("ant");
		mMap = new SpriteMap(ant, ant.getWidth(), ant.getHeight());
		mMap.setColor(mGroup.getAntColor());
		
		mMap.scale = 2;
		
		setGraphic(mMap);
		setLayer(1);
		setType(TYPE);
	}
	
	public AntGroup getAntGroup() {
		return mGroup;
	}
	
	public boolean isAlive() {
		return mLife > 0;
	}
	
	public String getLifeString() {
		return String.format("(%d/%d)", mLife, mLifeMax);
	}
	
	public void damage(int damage) {
		mLife -= damage;
		if (mLife < 0) {
			mLife = 0;
			
			// TODO play death.
			
			mColorTween = new ColorTween(null, ONESHOT);
			addTween(mColorTween);
			int color = getAntGroup().getAntColor();
			mColorTween.tween(1.0f, color, color & 0x00ffffff);
		}
	}
	
	public String toString() {
		return mGroup.toString() + " " + getLifeString();
	}
	
	public void setFlipped(boolean flipped) {
		mMap.scaleX = flipped ? -1 : 1;
	}
	
	public void tweenTo(int x, int y, OnCompleteCallback callback) {
		if (x == this.x && y == this.y) {
			return;
		}
		mMovementTween = new LinearPath(callback, ONESHOT);
		mMovementTween.addPoint(this.x, this.y);
		mMovementTween.addPoint(x, y);
		addTween(mMovementTween);
		mMovementTween.setMotion(0.5f);
		//mMovementTween.start();
	}

	@Override
	public void update() {
		super.update();
		
		if (mMovementTween != null) {
			x = (int)mMovementTween.x;
			y = (int)mMovementTween.y;
			if (!mMovementTween.active) {
				mMovementTween = null;
			}
		}
		
		if (mColorTween != null && mColorTween.active) {
			mMap.setColor(mColorTween.color);
		}
	}
	
	public int getWidth() {
		return (int)Math.abs(mMap.scale * mMap.scaleX * mMap.getWidth());
	}
	
	public int getHeight() {
		return (int)Math.abs(mMap.scale * mMap.scaleY * mMap.getHeight());
	}
	
}
