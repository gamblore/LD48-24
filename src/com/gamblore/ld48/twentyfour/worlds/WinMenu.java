package com.gamblore.ld48.twentyfour.worlds;

import com.gamblore.ld48.twentyfour.MainEngine;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.utils.Input;

public class WinMenu extends World {

	/**
	 * Display stats here?
	 */
	public WinMenu(int scorePlayer, int scoreEnemy) {
		super();
		
		FP.activity.setOnBackCallback(MainEngine.IN_GAME_BACK_CALLBACK);
		
		Entity e = new Entity();
		
		AtlasText playerScore = new AtlasText("You\n  "+String.valueOf(scorePlayer), 24, MainEngine.mTypeface);
		playerScore.x = FP.screen.getWidth()/3 - playerScore.getWidth()/2;
		playerScore.y = FP.screen.getHeight()/4;
		
		AtlasText enemyScore = new AtlasText("Them\n  "+String.valueOf(scoreEnemy), 24, MainEngine.mTypeface);
		enemyScore.x = 2*FP.screen.getWidth()/3;
		enemyScore.y = FP.screen.getHeight()/4;
		
		AtlasText result = new AtlasText(scorePlayer > scoreEnemy ? "YOU WON" : "YOU LOST", 42, MainEngine.mTypeface);
		result.x = FP.screen.getWidth()/2 - result.getWidth()/2;
		result.y = FP.screen.getHeight()/2 - result.getHeight()/2;
		
		e.setGraphic(new GraphicList(playerScore, enemyScore, result));
		
		add(e);
	}

	@Override
	public void update() {
		super.update();
		
		if(Input.mousePressed) {
			FP.setWorld(new MainMenu());
		}
	}
	
	
}
