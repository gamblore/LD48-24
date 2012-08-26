package com.gamblore.ld48.twentyfour.worlds;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.android.PunkActivity.OnBackCallback;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.utils.Input;
import android.graphics.Point;
import android.util.Log;

import com.gamblore.ld48.twentyfour.MainEngine;
import com.gamblore.ld48.twentyfour.entities.Scientist;

public class ChallengeMenu extends World {

	private static final String TAG = "ChallengeMenu";
	
	private AtlasText mRandomText, mBackText;
	
	public final OnBackCallback IN_GAME_BACK_CALLBACK = new OnBackCallback() {
		
		@Override
		public boolean onBack() {
			FP.setWorld(new MainMenu());
			return true;
		}
	};
	
	public ChallengeMenu() {
		super();
		
		FP.activity.setOnBackCallback(IN_GAME_BACK_CALLBACK);
		
		Entity e = new Entity();
		
		Image background = new Image(MainEngine.mAtlas.getSubTexture("challengemenu"));
		background.scale = 2;
		
		e.setGraphic(new GraphicList(background));
		e.setLayer(10);
		
		add(e);
		
		Entity mBackEntity = new Entity(16*2, 336*2);
		mBackEntity.setHitbox(96 * 2, 48 * 2);
		mBackText = new AtlasText("Back", 24, MainEngine.mTypeface);
		mBackText.x = mBackEntity.width/2 - mBackText.getWidth()/2;
		mBackText.y = 16 * 2;
		mBackEntity.setGraphic(mBackText);
		mBackEntity.setType("back");
		add(mBackEntity);
		
		Entity mRandomEntity = new Entity(128*2, 336*2);
		mRandomEntity.setHitbox(96 * 2, 48 * 2);
		mRandomText = new AtlasText("Random", 24, MainEngine.mTypeface);
		mRandomText.x = mRandomEntity.width/2 - mRandomText.getWidth()/2;
		mRandomText.y = 16 * 2;
		mRandomEntity.setGraphic(mRandomText);
		mRandomEntity.setType("random");
		add(mRandomEntity);
		
		createScientists();
	}
	
	private void createScientists() {
		FP.setRandomSeed(22116);
		for (int i = 0; i < 8; i++) {
			if (MainEngine.PLAYER.hasBeaten(i)) {
				continue;
			}
			int position = (i > 3) ? i+1 : i;
			int x = position % 3;
			int y = position / 3;
			
			
			Scientist s = new Scientist();
			s.x = ((x * 5) * 16 + 16) * 2;
			s.y = ((y * 6) * 16 + 16) * 2;
			
			s.setIndex(i);
			
			add(s);
		}
		FP.setRandomSeed((int)(Math.random() * 23523));
	}
	
	@Override
	public void update() {
		super.update();
		
		if (Input.mousePressed) {
			Point p = Input.getTouches()[0];
			Entity e;
			if ((e = collidePoint(Scientist.TYPE, p.x, p.y)) != null) {
				Log.d(TAG, "Touched scientist");
				Scientist s = (Scientist)e;
				MainEngine.PLAYER.challengingIndex = s.getIndex();
				FP.setWorld(new FightWorld(MainEngine.PLAYER.getAntKit(), s.getAntKit()));
			}
			if ((e = collidePoint("back", p.x, p.y)) != null) {
				FP.setWorld(new MainMenu());
			} else if ((e = collidePoint("random", p.x, p.y)) != null) {
				FP.setWorld(new FightWorld(MainEngine.PLAYER.getAntKit(), null));
				MainEngine.PLAYER.challengingIndex = -1;
			}
		}
	}
}
