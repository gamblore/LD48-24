package com.gamblore.ld48.twentyfour.worlds;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.AtlasGraphic;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;

import com.gamblore.ld48.twentyfour.MainEngine;
import com.gamblore.ld48.twentyfour.entities.Ant;
import com.gamblore.ld48.twentyfour.entities.AntGroup;

public class MainMenu extends World {
	
	
	private static final int[][] ANT_POSITIONS = new int[][] { new int[] { 48*2, 48*2 }, new int[] { 3 * 48*2, 48*2}, new int[] { 48*2, 3 * 48*2 }, new int[] {3 * 48*2,3 * 48*2 }};
	
	private AtlasText mLogText, mFundsText, mChallangeText;
	
	public MainMenu() {
		super();
		Entity e = new Entity();
		
		Image background = new Image(MainEngine.mAtlas.getSubTexture("mainmenu"));
		background.scale = 2;
		
		mLogText = new AtlasText("Tap on the ant species\nto evolve.\n\nEvolution reduces numbers\nbut modifies traits.", 14, MainEngine.mTypeface);
		mLogText.x = 18 * 2;
		mLogText.y = 208 * 2;
		
		mFundsText = new AtlasText("$100", 14, MainEngine.mTypeface);
		mFundsText.x = FP.screen.getWidth()/2 - mFundsText.getWidth()/2;
		mFundsText.y = 4;
		
		mChallangeText = new AtlasText("Challange!", 20, MainEngine.mTypeface);
		mChallangeText.x = FP.screen.getWidth()/2 - mChallangeText.getWidth()/2;
		mChallangeText.y = 352 * 2;
		
		e.setGraphic(new GraphicList(background, mLogText, mFundsText, mChallangeText));
		e.setLayer(10);
		
		add(e);
		
		getDisplayAnts();
	}
	
	private void getDisplayAnts() {
		AntGroup[] antGroups = MainEngine.PLAYER.getAntGroups();
		for (int i = 0; i < antGroups.length; i++) {
			Ant a = antGroups[i].getAnt();
			a.visible = true;
			if (i % 2 == 1) {
				((AtlasGraphic)a.getGraphic()).scaleX = -1;
			}
			
			((AtlasGraphic)a.getGraphic()).scale = 3;
			
			a.x = ANT_POSITIONS[i][0];
			a.y = ANT_POSITIONS[i][1];
			
			add(a);
		}
	}
}
