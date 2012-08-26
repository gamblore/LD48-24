package com.gamblore.ld48.twentyfour.worlds;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.utils.Input;

import com.gamblore.ld48.twentyfour.MainEngine;
import com.gamblore.ld48.twentyfour.R;
import com.gamblore.ld48.twentyfour.entities.AntKit;

public class MainTitle extends World {

	ColorTween mStartTween;
	AtlasText mStartText;
	
	public MainTitle() {
		super();
		
		FP.activity.setOnBackCallback(null);
		
		Image background = new Image(MainEngine.mAtlas.getSubTexture("titlescreen"));
		background.scale = 2;
		
		mStartText = new AtlasText("Tap to Start", 24, MainEngine.mTypeface);
		mStartText.x = FP.screen.getWidth()/2 - mStartText.getWidth()/2;
		mStartText.y = 3*FP.screen.getHeight()/4;
		mStartText.setColor(0);
		
		mStartTween = new ColorTween(new OnCompleteCallback() {
			
			@Override
			public void completed() {
				if (mStartTween.getAlpha() < 255) {
					mStartTween.tween(0.5f, mStartTween.color, 0xffffffff);
				} else {
					mStartTween.tween(0.5f, 0xffffffff, 0xaaffffff);
				}
			}
		}, PERSIST);
		
		addTween(mStartTween, false);
		mStartTween.tween(1.0f, 0x00ffffff, 0xffffffff);
		
		Entity e = new Entity();
		
		e.setGraphic(new GraphicList(background, mStartText));
		
		add(e);
	}

	@Override
	public void update() {
		super.update();
		if (Input.mousePressed) {
			if (MainEngine.PLAYER.firstRun) {
				FP.setWorld(new StoryWorld(R.string.intro_story));
			} else {
				FP.setWorld(new FightWorld(MainEngine.PLAYER.getAntKit(), AntKit.getRandomAntKit()));
			}
		}
		
		mStartText.setColor(mStartTween.color);
	}
	
	
}
