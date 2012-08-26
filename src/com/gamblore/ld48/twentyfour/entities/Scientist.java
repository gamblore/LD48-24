package com.gamblore.ld48.twentyfour.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;

import android.graphics.Color;

import com.gamblore.ld48.twentyfour.MainEngine;

public class Scientist extends Entity {

	public static final String TYPE = "scientist";
	
	private static final int[] HAIR_COLORS = new int[] { Color.rgb(255, 65, 0), Color.rgb(255, 255, 125), Color.rgb(20, 20, 20),
						Color.rgb(60,60,60), Color.rgb(125, 255, 128), Color.rgb(125, 223, 255)};
	private static final int[] FACE_COLORS = new int[] { Color.rgb(255, 219, 182), Color.rgb(255, 199, 182), Color.rgb(255, 199, 219),
						Color.rgb(255, 255, 182), Color.rgb(113, 97, 81), Color.rgb(255, 182, 235)};
	
	private Image mHair, mFace;
	
	private GraphicList mGraphicList;
	
	private AntKit mAntKit;
	
	private int mIndex = -1;
	
	public Scientist() {
		this(HAIR_COLORS[FP.rand(HAIR_COLORS.length)], FACE_COLORS[FP.rand(FACE_COLORS.length)]);
	}
	
	public Scientist(int hairColor, int faceColor) {
		super();
		
		mHair = new Image(MainEngine.mAtlas.getSubTexture("scientist_hair"));
		mHair.setColor(hairColor);
		mHair.scale = 2;
		
		mFace = new Image(MainEngine.mAtlas.getSubTexture("scientist_face"));
		mFace.setColor(faceColor);
		mFace.scale = 2;
		
		Image backdrop = new Image(MainEngine.mAtlas.getSubTexture("scientist_backdrop"));
		backdrop.scale = 2;
		
		Image card = new Image(MainEngine.mAtlas.getSubTexture("scientist_card"));
		card.scale = 2;
		
		mGraphicList = new GraphicList(backdrop, mFace, mHair, card);
		
		setGraphic(mGraphicList);
		
		mAntKit = AntKit.getRandomAntKit();
		
		setType(TYPE);
		setHitbox(backdrop.getWidth()*2, backdrop.getHeight()*2);
	}
	
	public void setIndex(int index) {
		mIndex = index;
	}
	
	public int getIndex() {
		return mIndex;
	}
	
	public void setBeaten() {
		collidable = false;
		mGraphicList.visible = false;
	}
	
	public AntKit getAntKit() {
		return mAntKit;
	}
}
