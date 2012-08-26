package com.gamblore.ld48.twentyfour.worlds;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.utils.Input;
import android.util.Log;

import com.gamblore.ld48.twentyfour.MainEngine;
import com.gamblore.ld48.twentyfour.R;

public class WinMenu extends World {

	private static final String TAG = "WinMenu";
	
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
		
		int prize = 100;
		if (scorePlayer > scoreEnemy) {
			MainEngine.SFXS.get(MainEngine.SFX_VICTORY).play();
			prize += 100 + (FP.rand(3)+1) * 25;
			Log.d(TAG, "beat: " + MainEngine.PLAYER.challengingIndex);
			if (MainEngine.PLAYER.challengingIndex > -1) {
				MainEngine.PLAYER.setBeaten(MainEngine.PLAYER.challengingIndex);
			}
			MainEngine.PLAYER.challengingIndex = -1;
		}
		
		AtlasText fundsWon = new AtlasText(String.format("Prize: $%d", prize), 24, MainEngine.mTypeface);
		fundsWon.x = FP.screen.getWidth()/2 - fundsWon.getWidth()/2;
		fundsWon.y = FP.screen.getHeight()/2 + 22;
		
		e.setGraphic(new GraphicList(playerScore, enemyScore, result, fundsWon));
		
		add(e);
		
		MainEngine.PLAYER.addFunds(prize);
		MainEngine.PLAYER.increaseColonyStrength();
		MainEngine.PLAYER.save();
	}
	
	

	@Override
	public void update() {
		super.update();
		
		if (MainEngine.PLAYER.hasWonGame()) {
			Log.d(TAG, "Ending game!");
			FP.setWorld(new StoryWorld(R.string.end_story));
			MainEngine.PLAYER.reset();
		}
		
		if(Input.mousePressed) {
			FP.setWorld(new MainMenu());
		}
	}
	
	
}
