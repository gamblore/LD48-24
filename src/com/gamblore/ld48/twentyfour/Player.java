package com.gamblore.ld48.twentyfour;

import net.androidpunk.FP;
import net.androidpunk.utils.Data;
import android.util.Log;

import com.gamblore.ld48.twentyfour.entities.AntGroup;
import com.gamblore.ld48.twentyfour.entities.AntKit;

public class Player {
	private static final String TAG = "Player";

	private static final String PREF_SAVEGAME = "savegame";
	
	public boolean firstRun = false;
	
	public int challengingIndex = -1; 
	
	private AntGroup[] mAntGroups = new AntGroup[4];
	
	private int mFunds = 100;
	
	private int mBeaten = 0;
	
	public Player() {
		load();
	}
	
	public AntGroup[] getAntGroups() {
		return mAntGroups;
	}
	
	public AntKit getAntKit() {
		AntKit ak = new AntKit();
		int antSum = 0;
		int added = 0;
		for (int i = 0; i < mAntGroups.length; i++) {
			antSum += mAntGroups[i].getColonyStrength();
		}
		
		for (int i = 0; i < mAntGroups.length; i++) {
			int populationStrength = mAntGroups[i].getColonyStrength();
			float percentage = (float)populationStrength / antSum;
			int toAdd = (int)(percentage * 25);
			if (i == 3 && added + toAdd != 25) {
				toAdd = 25 - added;
			}
			added += toAdd;
			ak.add(mAntGroups[i], toAdd);
		}
		return ak;
	}
	
	public int getFunds() {
		return mFunds;
	}
	
	public void addFunds(int funds) {
		mFunds += funds;
	}
	
	public void useFunds(int funds) {
		mFunds -= funds;
	}
	
	public boolean hasBeaten(int index) {
		return (mBeaten & (1 << index)) > 0;
	}
	
	public void setBeaten(int index) {
		mBeaten |= (1 << index);
		Log.d(TAG, "Beaten index: " + index + " value " + (1 << index)+ " result: " + mBeaten);
	}
	
	public boolean hasWonGame() {
		return mBeaten == 0xff;
	}
	
	public void increaseColonyStrength() {
		for (int i = 0; i < mAntGroups.length; i++) {
			mAntGroups[i].increaseColonyStrength(FP.rand(25) + 35);
		}
	}
	
	public void save() {
		String data = "";
		for (int i = 0; i < 4; i++) {
			data += mAntGroups[i].toString() + "|";
		}
		
		data += "!";
		data += mFunds + "!";
		data += mBeaten + "!";
		
		Log.d(TAG, "PLAYER DATA: " + data);
		Data.getData().edit().putString(PREF_SAVEGAME, data).commit();
	}
	
	public void load() {
		String data = Data.getData().getString(PREF_SAVEGAME, "");
		Log.d(TAG, "PLAYER DATA: " + data);
		if ("".equals(data)) {
			reset();
		} else {
			String parts[] = data.split("!");
			for(int i = 0; i < parts.length; i++) {
				Log.d(TAG, "Part "+ i + ": " + parts[i]);
			}
			
			// Part 0: Ant Group data
			String groups[] = parts[0].split("\\|");
			for (int i = 0; i < groups.length; i++) {
				Log.d(TAG, "group "+i+": " +groups[i]);
			}
			if (groups.length != 4) {
				// Corrupt savegame?
				Log.e(TAG, "BAD SAVEGAME!");
				reset();
				load();
				return;
			}
			for (int i = 0; i < 4; i++) {
				Log.d(TAG, "Loading antgroup: " + groups[i] );
				String stats[] = groups[i].split("-");
				mAntGroups[i] = new AntGroup();
				mAntGroups[i].set(Integer.parseInt(stats[0]), Integer.parseInt(stats[1]),
						Integer.parseInt(stats[2]), Integer.parseInt(stats[3]), Integer.parseInt(stats[4]));
			}
			
			mFunds = Integer.parseInt(parts[1]);
			mBeaten = Integer.parseInt(parts[2]);
		}
	}
	
	public void reset() {
		for (int i = 0; i < 4; i++) {
			mAntGroups[i] = new AntGroup();
		}
		mFunds = 100;
		mBeaten = 0;
		firstRun = true;
		Data.getData().edit().remove(PREF_SAVEGAME).commit();
	}
}
