package com.gamblore.ld48.twentyfour.worlds;

import java.util.NoSuchElementException;
import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.atlas.AtlasText;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.utils.Input;
import android.graphics.Point;
import android.util.Log;

import com.gamblore.ld48.twentyfour.MainEngine;
import com.gamblore.ld48.twentyfour.entities.Ant;
import com.gamblore.ld48.twentyfour.entities.AntGroup;
import com.gamblore.ld48.twentyfour.entities.AntKit;

public class FightWorld extends World {
	
	private static final String TAG = "FightWorld";
	
	private Vector<Ant> mPlayerQueue = new Vector<Ant>(10);
	private Vector<Ant> mEnemyQueue = new Vector<Ant>(10);
	
	private int mStep;
	
	private AtlasText mPlayerText, mEnemyText;
	private AtlasText mPlayerDamageText, mEnemyDamageText;
	
	private AtlasText mPlayerScore, mEnemyScore;
	private int mPlayerScoreValue, mEnemyScoreValue;
	
	private ColorTween mPlayerTextTween, mEnemyTextTween;
	
	private Entity mUIGraphics;
	
	private AntKit mPlayer, mEnemy;
	
	private static final Point[] ANT_FIGHT_LOCATIONS = new Point[] { new Point(), new Point() };  
	
	private boolean mFighting = true;
	
	public FightWorld() {
		this(null, null);
	}
	
	public FightWorld(AntKit player, AntKit enemy) {
		super();
		
		buildUI();
		mStep = 0;
		
		mPlayerScoreValue = mEnemyScoreValue = 0;
		
		mPlayer = (player != null) ? player : AntKit.getRandomAntKit();
		mPlayer.setLayer(0);
		add(mPlayer);
		
		mEnemy = (enemy != null) ? enemy : AntKit.getRandomAntKit();
		
		for (int i = 0; i < 5; i++) {
			mPlayerQueue.add(mPlayer.getRandomAnt());
			add(mPlayerQueue.get(i));
			mEnemyQueue.add(mEnemy.getRandomAnt());
			add(mEnemyQueue.get(i));
		}
		
		ANT_FIGHT_LOCATIONS[0].set(FP.screen.getWidth()/2 - 64 - 16 + 16, FP.screen.getHeight()/2);
		ANT_FIGHT_LOCATIONS[1].set(FP.screen.getWidth()/2 + 16 + 16, FP.screen.getHeight()/2);
		
		mPlayerText = new AtlasText("(30/30)", 18, MainEngine.mTypeface);
		mPlayerText.x = ANT_FIGHT_LOCATIONS[0].x + 64 - mPlayerText.getWidth();
		mPlayerText.y = ANT_FIGHT_LOCATIONS[0].y + 32 * 2;
		
		mPlayerDamageText = new AtlasText("", 18, MainEngine.mTypeface);
		mPlayerDamageText.x = ANT_FIGHT_LOCATIONS[0].x;
		mPlayerDamageText.y = ANT_FIGHT_LOCATIONS[0].y;
		mPlayerDamageText.setColor(0xffff3333);
		mPlayerDamageText.visible = false;
		
		mEnemyText = new AtlasText("(31/31)", 18, MainEngine.mTypeface);
		mEnemyText.x = ANT_FIGHT_LOCATIONS[1].x;
		mEnemyText.y = ANT_FIGHT_LOCATIONS[1].y + 32 * 2;
		
		mEnemyDamageText = new AtlasText("", 18, MainEngine.mTypeface);
		mEnemyDamageText.x = ANT_FIGHT_LOCATIONS[1].x;
		mEnemyDamageText.y = ANT_FIGHT_LOCATIONS[1].y;
		mEnemyDamageText.setColor(0xffff3333);
		mEnemyDamageText.visible = false;
		
		mPlayerScore = new AtlasText("0", 14, MainEngine.mTypeface);
		mPlayerScore.x = 64 + 16;
		mPlayerScore.y = 8;
		
		mEnemyScore = new AtlasText("0", 14, MainEngine.mTypeface);
		mEnemyScore.x = FP.screen.getWidth() - (64 + 16 + mEnemyScore.getWidth());
		mEnemyScore.y = 8;
		
		Entity text = new Entity();
		text.setLayer(0);
		text.setGraphic(new GraphicList(mPlayerText, mEnemyText, mPlayerDamageText, mEnemyDamageText, mPlayerScore, mEnemyScore));
		
		mPlayerTextTween = new ColorTween(null, PERSIST);
		mEnemyTextTween = new ColorTween(null, PERSIST);
		
		addTween(mPlayerTextTween, false);
		addTween(mEnemyTextTween, false);
		
		add(text);
		mPlayerText.setText(mPlayerQueue.firstElement().getLifeString());
		mEnemyText.setText(mEnemyQueue.firstElement().getLifeString());
		
		add(mPlayerQueue.firstElement());
		add(mEnemyQueue.firstElement());
		
		positionAnts();
		tweenAnts();
	}
	
	private void buildUI() {
		mUIGraphics = new Entity();
		
		Image background = new Image(MainEngine.mAtlas.getSubTexture("background"));
		background.scale = 2;
		background.setColor(0xff888888);
		
		Image playerQueue = new Image(MainEngine.mAtlas.getSubTexture("playerqueuebox"));
		playerQueue.scale = 2;
		playerQueue.x = playerQueue.y = FP.dip(4);
		
		Image enemyQueue = new Image(MainEngine.mAtlas.getSubTexture("enemyqueuebox"));
		enemyQueue.scale = 2;
		enemyQueue.x = FP.screen.getWidth() - FP.dip(4) - enemyQueue.getWidth() * 2;
		enemyQueue.y = FP.dip(4);
		
		Image playerSelect = new Image(MainEngine.mAtlas.getSubTexture("playerselect"));
		playerSelect.scale = 2;
		playerSelect.x = FP.dip(4);
		playerSelect.y = FP.screen.getHeight() - playerSelect.getHeight() * 2 - FP.dip(4);
		
		
		mUIGraphics.setLayer(10);
		mUIGraphics.setGraphic(new GraphicList(background, playerQueue, enemyQueue, playerSelect));
		
		add(mUIGraphics);
	}
	
	private void checkAndEvaluateFight(Ant a, Ant b) {
		int damageA = 0, damageB = 0;
		
		boolean aAttacks = false, bAttacks = false;
		if (mStep % (int)(100f / a.getAntGroup().getAttackRate()) == 0) {
			aAttacks = true;
		}
		if (mStep % (int)(100f / b.getAntGroup().getAttackRate()) == 0) {
			bAttacks = true;
		}
		
		if (aAttacks && bAttacks) {
			if (FP.random() > 0.5) {
				damageB = AntGroup.fight(a, b);
				if (b.isAlive()) {
					damageA = AntGroup.fight(b, a);
				}
			} else {
				damageA = AntGroup.fight(b, a);
				if (a.isAlive()) {
					damageB = AntGroup.fight(a, b);
				}
			}
		} else if (aAttacks) {
			damageB = AntGroup.fight(a, b);
		} else if (bAttacks) {
			damageA = AntGroup.fight(b, a);
		}
		
		if (damageA > 0) {
			mPlayerDamageText.visible = true;
			mPlayerDamageText.setText(String.valueOf(damageA));
			mPlayerDamageText.x = ANT_FIGHT_LOCATIONS[0].x + 64 - mPlayerDamageText.getWidth();
			mPlayerTextTween.tween(0.5f, 0xffff3333, 0x00ff3333);
			mPlayerText.setText(a.getLifeString());
			
		}
		if (damageB > 0) {
			mEnemyDamageText.visible = true;
			mEnemyDamageText.setText(String.valueOf(damageB));
			mEnemyTextTween.tween(0.5f, 0xffff3333, 0x00ff3333);
			mEnemyText.setText(b.getLifeString());
		}
	}
	
	private void fillAnts() {
		while(mEnemyQueue.size() < 10) {
			Ant b = mEnemy.getRandomAnt();
			if (b == null) {
				break;
			}
			b.x = FP.screen.getWidth();
			b.y = FP.screen.getHeight()/2;
			mEnemyQueue.add(b);
			add(b);
		}
		
		while(mPlayerQueue.size() < 6) {
			Ant a = mPlayer.getRandomAnt();
			if (a == null) {
				break;
			}
			a.x = -64;
			a.y = FP.screen.getHeight()/2;
			mPlayerQueue.add(a);
			add(a);
		}
		
		//tweenAnts();
	}
	
	private void tweenAnts() {
		for(int i = 1; i < mPlayerQueue.size(); i++) {
			Ant a = mPlayerQueue.get(i);
			a.tweenTo(4 + 8, 4 + 8 + ((i-1) * (33 * 2) + 2), null);
			a.visible = true;
		}
		
		int enemyX = FP.screen.getWidth() - 10 - 32 * 2;
		for(int i = 1; i < mEnemyQueue.size() && i < 6; i++) {
			Ant e = mEnemyQueue.get(i);
			e.tweenTo(enemyX, 4 + 8 + ((i-1) * (33 * 2) + 2), null);
			e.setFlipped(true);
			e.visible = true;
		}
	}
	
	private void positionAnts() {
		for(int i = 1; i < mPlayerQueue.size(); i++) {
			Ant a = mPlayerQueue.get(i);
			a.x = 4 + 8;
			a.y = 4 + 8 + ((i-1) * (33 * 2) + 2);
			a.visible = true;
		}
		
		int enemyX = FP.screen.getWidth() - 10 - 32 * 2;
		for(int i = 1; i < mEnemyQueue.size() && i < 6; i++) {
			Ant e = mEnemyQueue.get(i);
			e.x = enemyX;
			e.y = 4 + 8 + ((i-1) * (33 * 2) + 2);
			e.setFlipped(true);
			e.visible = true;
		}
	}
	
	private void updateQueues() {
		
		if (!mPlayerQueue.firstElement().isAlive()) {
			mFighting = false;
			remove(mPlayerQueue.remove(0));
			if (!mPlayerQueue.isEmpty()) {
				mPlayerQueue.firstElement().tweenTo(ANT_FIGHT_LOCATIONS[0].x,  ANT_FIGHT_LOCATIONS[0].y, new OnCompleteCallback() {
					
					@Override
					public void completed() {
						mFighting = true;
					}
				});
			}
			mEnemyScoreValue++;
			mEnemyScore.setText(String.valueOf(mEnemyScoreValue));
			if (mEnemyScoreValue > 9) {
				mEnemyScore.x = FP.screen.getWidth() - (64 + 16 + mEnemyScore.getWidth());
			}
			
			try {
				mPlayerText.setText(mPlayerQueue.firstElement().getLifeString());
			} catch (NoSuchElementException e) {
				mPlayerText.setText("dead");
			}
		}
		
		if (mEnemyQueue.size() == 0) {
			//TODO play victory sound.
		} else if (!mEnemyQueue.firstElement().isAlive()) {
			mFighting = false;
			remove(mEnemyQueue.remove(0));
			if (!mEnemyQueue.isEmpty()) {
				mEnemyQueue.firstElement().tweenTo(ANT_FIGHT_LOCATIONS[1].x,  ANT_FIGHT_LOCATIONS[1].y, new OnCompleteCallback() {
					
					@Override
					public void completed() {
						mFighting = true;
					}
				});
			}
			mPlayerScoreValue++;
			mPlayerScore.setText(String.valueOf(mPlayerScoreValue));
			//add(mEnemyQueue.firstElement());
			try {
				mEnemyText.setText(mEnemyQueue.firstElement().getLifeString());
			} catch (NoSuchElementException e) {
				mEnemyText.setText("dead");
			}
		}
		
		fillAnts();
		
		tweenAnts();
	}

	private void checkInput() {
		if (Input.mousePressed) {
			Point p = Input.getTouches()[0];
			Log.d(TAG, String.format("Touch at %d, %d",p.x, p.y));
			if (mPlayer.collidePoint(0, 0, p.x, p.y)) {
				if (mPlayerQueue.size() < 11) {
					int type = (int)((p.x - 8) / (57 * 2));
					Log.d(TAG, "Touch! " + type);
					Ant a = mPlayer.getAntFromAntGroup(type);
					if (a != null) {
						a.x = -64;
						a.y = 4 + 8 + ((mPlayerQueue.size()) * (33 * 2) + 2);
						mPlayerQueue.add(a);
						add(a);
						tweenAnts();
					}
				}
			}
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		if (mPlayerQueue.isEmpty() || mEnemyQueue.isEmpty()) {
			FP.setWorld(new WinMenu(mPlayerScoreValue, mEnemyScoreValue));
		}
		
		if (mPlayerTextTween.active) {
			mPlayerDamageText.setColor(mPlayerTextTween.color);
		} else {
			mPlayerDamageText.visible = false;
		}
		
		if (mEnemyTextTween.active) {
			mEnemyDamageText.setColor(mEnemyTextTween.color);
		} else {
			mEnemyDamageText.visible = false;
		}
		
		checkInput();
		
		if (!mFighting) {
			return;
		}
		mStep++;
		
		Ant a, b;
		try {
			a = mPlayerQueue.firstElement();
			if (!a.visible) {
				a.visible = true;
				a.x = ANT_FIGHT_LOCATIONS[0].x;
				a.y = ANT_FIGHT_LOCATIONS[0].y;
			}
		} catch (NoSuchElementException e) {
			return;
		}
		
		try {
			b = mEnemyQueue.firstElement();
			if (!b.visible) {
				b.visible = true;
				b.x = ANT_FIGHT_LOCATIONS[1].x;
				b.y = ANT_FIGHT_LOCATIONS[1].y;
			}
			b.setFlipped(true);
		} catch (NoSuchElementException e) {
			return;
		}
		checkAndEvaluateFight(a, b);
		
		updateQueues();
	}
	
	
	
}
