package com.gamblore.ld48.twentyfour.entities;

import android.graphics.Color;

import com.gamblore.ld48.twentyfour.MainEngine;

import net.androidpunk.Entity;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;

public class Ant extends Entity {

	// pulled from defense.
	private int mLife, mLifeMax;
	
	private AntGroup mGroup;
	
	private SpriteMap mMap;
	
	public Ant(AntGroup group, int maxLife) {
		mGroup = group;
		mLife = mLifeMax = maxLife;
		
		SubTexture ant = MainEngine.mAtlas.getSubTexture("ant");
		mMap = new SpriteMap(ant, ant.getWidth(), ant.getHeight());
		mMap.setColor(mGroup.getAntColor());
		
		mMap.scale = 2;
		
		setGraphic(mMap);
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
		}
	}
	
	public String toString() {
		return mGroup.toString() + " " + getLifeString();
	}
	
	public void setFlipped(boolean flipped) {
		mMap.scaleX = flipped ? -1 : 1;
	}
}
