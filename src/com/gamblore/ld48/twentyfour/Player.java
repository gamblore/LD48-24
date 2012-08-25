package com.gamblore.ld48.twentyfour;

import net.androidpunk.utils.Data;
import android.util.Log;

import com.gamblore.ld48.twentyfour.entities.AntGroup;
import com.gamblore.ld48.twentyfour.entities.AntKit;

public class Player {
	private static final String TAG = "Player";

	private static final String PREF_SAVEGAME = "savegame";
	
	private AntGroup[] mAntGroups = new AntGroup[4];
	
	public Player() {
		load();
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
	
	public void save() {
		String data = "";
		for (int i = 0; i < 4; i++) {
			data += mAntGroups[i].toString() + "|";
		}
		
		data += "!";
		// TODO save other data
		
		Data.getData().edit().putString(PREF_SAVEGAME, data).commit();
	}
	
	public void load() {
		String data = Data.getData().getString(PREF_SAVEGAME, "");
		if ("".equals(data)) {
			for (int i = 0; i < 4; i++) {
				mAntGroups[i] = new AntGroup();
			}
		} else {
			String parts[] = data.split("!");
			
			// Part 0: Ant Group data
			String groups[] = parts[0].split("|");
			if (groups.length < 4) {
				// Corrupt savegame?
				Log.e(TAG, "BAD SAVEGAME!");
				reset();
				return;
			}
			for (int i = 0; i < 4; i++) {
				mAntGroups[i] = new AntGroup();
				mAntGroups[i].set(Integer.parseInt(groups[0]), Integer.parseInt(groups[1]),
						Integer.parseInt(groups[2]), Integer.parseInt(groups[3]), Integer.parseInt(groups[4]));
			}
		}
	}
	
	public void reset() {
		Data.getData().edit().remove(PREF_SAVEGAME).commit();
	}
}
