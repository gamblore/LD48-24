package com.gamblore.ld48.twentyfour.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.masks.Hitbox;
import android.util.Log;

import com.gamblore.ld48.twentyfour.MainEngine;

public class AntKit extends Entity {
	
	private static final String TAG = "AntKit";
	
	private static final int EXTINCTION = 0xffcc0000;
	private final Map<AntGroup, Integer> mKit = new HashMap<AntGroup, Integer>();
	private final AntGroup[] mGroups = new AntGroup[4];
	
	private SubTexture mAntSubTexture;
	
	private SpriteMap[] mGraphics = new SpriteMap[4];
	private AtlasText[] mTexts = new AtlasText[4];
	
	private GraphicList mGraphicsList = new GraphicList();
	
	public AntKit() {
		mAntSubTexture = MainEngine.mAtlas.getSubTexture("ant");
		
		setGraphic(mGraphicsList);
		
		SubTexture playerselect = MainEngine.mAtlas.getSubTexture("playerselect");
		setMask(new Hitbox(playerselect.getWidth()*2, playerselect.getHeight()*2, 8, FP.screen.getHeight() - playerselect.getHeight()*2 - 8));
		
	}
	
	public static AntKit getRandomAntKit() {
		int level = FP.rand(3);
		int sum = 25;
		AntKit ak = new AntKit();
		for (int i = 0; i < 4; i++) {
			AntGroup ag = new AntGroup();
			for (int j = 0; j < level; j++) {
				ag.evolve(FP.rand(4));
			}
			int num = FP.rand(5) + 5;
			if (num > sum) {
				num = sum;
			}
			sum -= num;
			ak.add(ag, num);
		}
		return ak;
	}
	
	public AntKit add(AntGroup group, int number) {
		if (mKit.size() == 4) {
			Log.e(TAG, "The AntKit is full.");
			return this;
		}
		mKit.put(group, number);
		
		SpriteMap map = new SpriteMap(mAntSubTexture, mAntSubTexture.getWidth(), mAntSubTexture.getHeight());
		map.setColor(group.getAntColor());
		map.x = (mKit.size()-1) * (58 * 2) + 40;
		map.y = FP.screen.getHeight() - (32 * 2);
		map.scale = 2;
		mGraphics[mKit.size()-1] = map;
		
		AtlasText text = new AtlasText(String.valueOf(number), 18, MainEngine.mTypeface);
		text.x = (mKit.size()-1) * (58 * 2) + 16;
		text.y = FP.screen.getHeight() - (32 * 2) + 8;
		if (number == 0) {
			text.setColor(EXTINCTION);
		}
		mTexts[mKit.size()-1] = text;
		
		mGraphicsList.add(map);
		mGraphicsList.add(text);
		
		mGroups[mKit.size()-1] = group;
		
		return this;
	}
	
	public Set<AntGroup> getTypes() {
		return mKit.keySet();
	}
	
	public AntGroup getGroup(int index) {
		return mGroups[index];
	}
	
	public Ant getAntFromAntGroup(int index) {
		AntGroup g = mGroups[index];
		
		int count = mKit.get(g);
		if (count == 0) {
			return null;
		}
		mKit.put(g, count - 1);
		mTexts[index].setText(String.valueOf(count - 1));
		if (count - 1 == 0) {
			mTexts[index].setColor(EXTINCTION);
		}
		return g.getAnt();
	}
	
	public Ant getAntFromAntGroup(AntGroup group) {
		for (int i = 0; i < mGroups.length; i++) {
			if (mGroups[i].equals(group)) {
				return getAntFromAntGroup(i);
			}
		}
		return null;
	}
	
	public int getCount(AntGroup group) {
		return mKit.get(group);
	}
	
	public Ant getRandomAnt() {
		Ant a = null;
		boolean hasElements = false;
		
		// Find out if we are going to find a random.
		for (int i = 0; i < mGroups.length; i++) {
			if (mKit.get(mGroups[i]) > 0) {
				hasElements = true;
				break;
			}
		}
		if (!hasElements) {
			return a;
		}
		
		for (int idx = FP.rand(mGroups.length); a == null; idx++) {
			idx %= mGroups.length;
			if (mKit.get(mGroups[idx]) > 0) {
				a = mGroups[idx].getAnt();
				int count = mKit.get(mGroups[idx]);
				mKit.put(mGroups[idx], count - 1);
				mTexts[idx].setText(String.valueOf(count - 1));
				if (count - 1 == 0) {
					mTexts[idx].setColor(EXTINCTION);
				}
			}
		}
		return a;
	}
}
