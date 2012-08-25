package com.gamblore.ld48.twentyfour.entities;

import net.androidpunk.FP;
import android.graphics.Color;
import android.util.Log;

public class AntGroup {

	private static final String TAG = "AntGroup";
	
	public static final int UPGRADE_HARDEN_FOOD = 1;
	public static final int UPGRADE_HIDE_FOOD = 2; 
	public static final int UPGRADE_OUT_OF_REACH_FOOD = 3; 
	public static final int UPGRADE_POOR_ACID = 4; 
	
	/**
	 * Used to determine how many survive the evolution.
	 * How many are pulled into battle.
	 */
	private int mColonyStrength;
	
	// All values are [0-100]
	// Determines damage
	private int mAttack;
	
	// Determines life
	private int mDefence;
	
	// Determines attack rate
	// rate = (speed + 25) / 25
	private int mSpeed;
	
	// Determines chance to hit a critical (2 * damage).
	private int mAccuracy;
	
	
	public AntGroup() {
		mAttack = mAccuracy = mDefence = mSpeed = 25;
		mColonyStrength = 100;
	}
	
	public void set(int attack, int defence, int speed, int accuracy, int colonyStrength) {
		mAttack = attack;
		mDefence = defence;
		mSpeed = speed;
		mAccuracy = accuracy;
		mColonyStrength = colonyStrength;
	}
	
	public int getColonyStrength() {
		return mColonyStrength;
	}
	
	public int getAntColor() {
		int r = (int)(((mAttack / 2 + mAccuracy /2) / 100f) * 255);
		int g = (int)(((mSpeed / 2 + mAccuracy /2) / 100f) * 255);
		int b = (int)((mDefence / 100f) * 255);
		return Color.argb(255, r, g, b);
		//return Color.WHITE;
	}
	
	/**
	 * Evolves the ant group.
	 * @param upgrade
	 */
	public void evolve(int upgrade) {
		switch (upgrade) {
		case UPGRADE_HARDEN_FOOD:
			mAttack += FP.rand(10);
			mAccuracy -= FP.rand(10);
			break;

		case UPGRADE_HIDE_FOOD:
			mAccuracy += FP.rand(10);
			mAttack -= FP.rand(5);
			mDefence -= FP.rand(5);
			break;
			
		case UPGRADE_OUT_OF_REACH_FOOD:
			mSpeed += FP.rand(10);
			mDefence -= FP.rand(5);
			mAccuracy -= FP.rand(5);
			break;
			
		case UPGRADE_POOR_ACID:
			mDefence += FP.rand(15);
			mSpeed -= FP.rand(5);
			break;
			
		default:
			break;
		}
	}
	
	public Ant getAnt() {
		Ant a = new Ant(this, mDefence + FP.rand(15) - 5);
		a.visible = false;
		return a;
	}
	
	/**
	 * Attacks per 100 steps
	 * @return [1-5]
	 */
	public float getAttackRate() {
		return ((mSpeed + 25) / 25);
	}
	
	public static int fight(Ant attack, Ant defender) {
		float critChance = 0.01f + (attack.getAntGroup().mAccuracy / 10000f);
		boolean crit = (FP.random() < critChance);
		
		int damage = FP.rand(attack.getAntGroup().mAttack/3) + attack.getAntGroup().mAttack/3;
		if (crit) {
			damage *= 2;
		}
		
		defender.damage(damage);
		Log.d(TAG, attack.toString() + " hit " + defender.toString() + " for " + damage);
		
		return damage;
	}
	
	public String toString() {
		return String.format("%d-%d-%d-%d-%d", mAttack, mDefence, mSpeed, mAccuracy, mColonyStrength);
	}
}
