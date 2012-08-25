package com.gamblore.ld48.twentyfour;

import java.util.NoSuchElementException;
import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.Text;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.tweens.misc.ColorTween;

import com.gamblore.ld48.twentyfour.entities.Ant;
import com.gamblore.ld48.twentyfour.entities.AntGroup;
import com.gamblore.ld48.twentyfour.entities.AntKit;

public class FightWorld extends World {
	
	private static final String TAG = "FightWorld";
	
	private AntGroup mA, mB;
	
	private Vector<Ant> mPlayerQueue = new Vector<Ant>(10);
	private Vector<Ant> mEnemyQueue = new Vector<Ant>(10);
	
	private int mStep;
	
	private Text mPlayerText, mEnemyText;
	private Text mPlayerDamageText, mEnemyDamageText;
	
	private ColorTween mPlayerTextTween, mEnemyTextTween;
	
	private Entity mUIGraphics;
	
	private AntKit mPlayer, mEnemy;
	
	private boolean mWin = false;
	
	public FightWorld() {
		this(null, null);
	}
	
	public FightWorld(AntKit player, AntKit enemy) {
		super();
		
		buildUI();
		mStep = 0;
		
		mPlayer = (player != null) ? player : createRandomAntKit(25, 3);
		mEnemy = (enemy != null) ? enemy : createRandomAntKit(25, 1);
		
		for (int i = 0; i < 5; i++) {
			mPlayerQueue.add(mPlayer.getRandomAnt());
			add(mPlayerQueue.get(i));
			mEnemyQueue.add(mEnemy.getRandomAnt());
			add(mEnemyQueue.get(i));
		}
		
		
		mPlayerText = new Text("Player", 18, MainEngine.mTypeface);
		mPlayerText.x = FP.screen.getWidth()/2;
		mPlayerText.y = FP.screen.getHeight()/2 - FP.dip(64);
		
		mPlayerDamageText = new Text("", 18, MainEngine.mTypeface);
		mPlayerDamageText.x = FP.screen.getWidth()/2 + FP.dip(40);
		mPlayerDamageText.y = FP.screen.getHeight()/2 - FP.dip(30);
		mPlayerDamageText.setColor(0xffff3333);
		mPlayerDamageText.visible = false;
		
		mEnemyText = new Text("Enemy", 18, MainEngine.mTypeface);
		mEnemyText.x = FP.screen.getWidth()/2;
		mEnemyText.y = FP.screen.getHeight()/2 + FP.dip(32);
		
		mEnemyDamageText = new Text("", 18, MainEngine.mTypeface);
		mEnemyDamageText.x = FP.screen.getWidth()/2 + FP.dip(40);
		mEnemyDamageText.y = FP.screen.getHeight()/2 + FP.dip(10);
		mEnemyDamageText.setColor(0xffff3333);
		mEnemyDamageText.visible = false;
		
		Entity text = new Entity();
		text.setGraphic(new GraphicList(mPlayerText, mEnemyText, mPlayerDamageText, mEnemyDamageText));
		
		mPlayerTextTween = new ColorTween(null, PERSIST);
		mEnemyTextTween = new ColorTween(null, PERSIST);
		
		addTween(mPlayerTextTween, false);
		addTween(mEnemyTextTween, false);
		
		add(text);
		mPlayerText.setText(mPlayerQueue.firstElement().getLifeString());
		mEnemyText.setText(mEnemyQueue.firstElement().getLifeString());
		
		add(mPlayerQueue.firstElement());
		add(mEnemyQueue.firstElement());
	}
	
	private AntKit createRandomAntKit(int size, int level) {
		int sum = size;
		AntKit p = new AntKit();
		for (int i = 0; i < 4; i++) {
			AntGroup ag = new AntGroup();
			for (int j = 0; j < level; j++) {
				ag.evolve(FP.rand(4));
			}
			int num = FP.rand(5) + 5;
			if (num > sum) {
				num = sum;
			}
			p.add(ag, num);
		}
		return p;
	}
	
	private void buildUI() {
		mUIGraphics = new Entity();
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
		
		
		mUIGraphics.setLayer(0);
		mUIGraphics.setGraphic(new GraphicList(playerQueue, enemyQueue, playerSelect));
		
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
			mEnemyQueue.add(b);
			add(b);
		}
		
		while(mPlayerQueue.size() < 6) {
			Ant a = mPlayer.getRandomAnt();
			if (a == null) {
				break;
			}
			mPlayerQueue.add(a);
			add(a);
		}
	}
	
	private void positionAnts() {
		for(int i = 1; i < mPlayerQueue.size(); i++) {
			Ant a = mPlayerQueue.get(i);
			a.x = 4 + 8;
			a.y = 4 + 8 + ((i-1) * (33 * 2) + 2);
			a.visible = true;
		}
		
		int enemyX = FP.screen.getWidth() - 8 - 32 * 2;
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
			remove(mPlayerQueue.remove(0));
			
			try {
				mPlayerText.setText(mPlayerQueue.firstElement().getLifeString());
			} catch (NoSuchElementException e) {
				mPlayerText.setText("dead");
			}
		}
		
		if (mEnemyQueue.size() == 0) {
			mWin = true;
			//TODO play victory sound.
		} else if (!mEnemyQueue.firstElement().isAlive()) {
			remove(mEnemyQueue.remove(0));
			//add(mEnemyQueue.firstElement());
			mEnemyText.setText(mEnemyQueue.firstElement().getLifeString());
		}
		
		fillAnts();
		
		positionAnts();
	}

	@Override
	public void update() {
		super.update();
		
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
		
		mStep++;
		
		Ant a, b;
		try {
			a = mPlayerQueue.firstElement();
			a.visible = true;
			a.x = FP.screen.getWidth()/2;
			a.y = (int)(FP.screen.getHeight()/2 - FP.dip(32));
		} catch (NoSuchElementException e) {
			FP.setWorld(new FightWorld());
			return;
		}
		b = mEnemyQueue.firstElement();
		b.visible = true;
		b.setFlipped(true);
		b.x = FP.screen.getWidth()/2;
		b.y = (int)(FP.screen.getHeight()/2);
		
		checkAndEvaluateFight(a, b);
		
		updateQueues();
	}
	
	
	
}