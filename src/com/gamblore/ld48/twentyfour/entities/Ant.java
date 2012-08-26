package com.gamblore.ld48.twentyfour.entities;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.atlas.SpriteMap;
import net.androidpunk.graphics.opengl.SubTexture;
import net.androidpunk.tweens.misc.ColorTween;
import net.androidpunk.tweens.motion.LinearPath;
import net.androidpunk.utils.Ease;

import com.gamblore.ld48.twentyfour.MainEngine;

public class Ant extends Entity {

	public static final String TYPE = "ant";
	// pulled from defense.
	private int mLife, mLifeMax;
	
	private AntGroup mGroup;
	
	private SpriteMap mMap;
	
	private LinearPath mMovementTween;
	
	private ColorTween mColorTween;
	
	private static final String ANIM_WALK = "walk";
	private static final String ANIM_DIE = "die";
	
	// For showing colony strength.
	private boolean mVibrate = false;
	private boolean mTookStep = false;
	private int mStepX, mStepY;
	private float mTimeBetweenMoves;
	private float mTimeUntilMove;
	
	public Ant(AntGroup group, int maxLife) {
		mGroup = group;
		mLife = mLifeMax = maxLife;
		
		SubTexture ant = MainEngine.mAtlas.getSubTexture("ant");
		mMap = new SpriteMap(ant, ant.getWidth()/5, ant.getHeight());
		mMap.setColor(mGroup.getAntColor());
		
		mMap.add(ANIM_WALK, FP.frames(0, 1), 15);
		mMap.add(ANIM_DIE, FP.frames(2, 5), 10, false);
		
		mMap.play(ANIM_WALK);
		mMap.scale = 2;
		
		setGraphic(mMap);
		setLayer(1);
		setType(TYPE);
	}
	
	public AntGroup getAntGroup() {
		return mGroup;
	}
	
	public void vibrate() {
		mVibrate = true;
		// Healthy move at 0.05f speed unhealthy move at 0.4f
		mTimeBetweenMoves = -0.35f * (getAntGroup().getColonyStrength()/ 100f) + 0.4f;
		mTimeUntilMove = -1;
	}
	
	public boolean isAlive() {
		return mLife > 0;
	}
	
	public String getLifeString() {
		return String.format("(%d/%d)", mLife, mLifeMax);
	}
	
	public void showCrit() {
		if (mColorTween == null) {
			mColorTween = new ColorTween(null, PERSIST);
			addTween(mColorTween);
		}
		mColorTween.tween(0.5f, 0xffff2222, getAntGroup().getAntColor(), Ease.quadOut);
	}
	
	public void damage(int damage) {
		damage(damage, false);
	}
	
	public void damage(int damage, boolean removeIfKilled) {
		mLife -= damage;
		if (mLife <= 0) {
			mLife = 0;
			
			kill();
			if (removeIfKilled) {
				final Ant thisAnt = this;
				mColorTween = new ColorTween(new OnCompleteCallback() {
					
					@Override
					public void completed() {
						FP.getWorld().remove(thisAnt);
					}
				}, ONESHOT);
			} else {
				mColorTween = new ColorTween(null, ONESHOT);
			}
			addTween(mColorTween);
			int color = getAntGroup().getAntColor();
			mColorTween.tween(1.0f, color, color & 0x00ffffff);
		}
	}
	
	public void kill() {
		mMap.play(ANIM_DIE, true);
	}
	
	public String toString() {
		return mGroup.toString() + " " + getLifeString();
	}
	
	public void setFlipped(boolean flipped) {
		mMap.scaleX = flipped ? -1 : 1;
	}
	
	public void tweenTo(int x, int y, OnCompleteCallback callback) {
		if (x == this.x && y == this.y) {
			return;
		}
		mMovementTween = new LinearPath(callback, ONESHOT);
		mMovementTween.addPoint(this.x, this.y);
		mMovementTween.addPoint(x, y);
		addTween(mMovementTween);
		mMovementTween.setMotion(0.5f);
		//mMovementTween.start();
	}

	@Override
	public void update() {
		super.update();
		
		if (mMovementTween != null) {
			x = (int)mMovementTween.x;
			y = (int)mMovementTween.y;
			if (!mMovementTween.active) {
				mMovementTween = null;
			}
		}
		
		if (mColorTween != null && mColorTween.active) {
			mMap.setColor(mColorTween.color);
		}
		
		if (mVibrate) {
			mTimeUntilMove -= FP.elapsed;
			if (mTimeUntilMove < 0) {
				if (mTookStep) {
					x -= mStepX;
					y -= mStepY;
					mTookStep = false;
				} else {
					int movementMax = (int)(10 * (getAntGroup().getColonyStrength() / 100f));
					mStepX = FP.rand(movementMax) - movementMax/2;
					mStepY = FP.rand(movementMax) - movementMax/2;
					
					x += mStepX;
					y += mStepY;
					
					mTookStep = true;
				}
				mTimeUntilMove = mTimeBetweenMoves;
			}
		}
	}
	
	public int getWidth() {
		return (int)Math.abs(mMap.scale * mMap.scaleX * mMap.getWidth());
	}
	
	public int getHeight() {
		return (int)Math.abs(mMap.scale * mMap.scaleY * mMap.getHeight());
	}
	
}
