package com.gamblore.ld48.twentyfour.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.androidpunk.FP;

public class AntKit {

	private final Map<AntGroup, Integer> mKit = new HashMap<AntGroup, Integer>();
	
	public AntKit() {}
	
	public AntKit add(AntGroup group, int number) {
		mKit.put(group, number);
		return this;
	}
	
	public Set<AntGroup> getTypes() {
		return mKit.keySet();
	}
	
	public int getCount(AntGroup group) {
		return mKit.get(group);
	}
	
	public Ant getRandomAnt() {
		Ant a = null;
		boolean hasElements = false;
		
		AntGroup typeArray[] = new AntGroup[mKit.keySet().size()];
		mKit.keySet().toArray(typeArray);
		
		// Find out if we are going to find a random.
		for (int i = 0; i < typeArray.length; i++) {
			if (mKit.get(typeArray[i]) > 0) {
				hasElements = true;
				break;
			}
		}
		if (!hasElements) {
			return a;
		}
		
		for (int idx = FP.rand(typeArray.length); a == null; idx++) {
			idx %= typeArray.length;
			if (mKit.get(typeArray[idx]) > 0) {
				a = typeArray[idx].getAnt();
				mKit.put(typeArray[idx], mKit.get(typeArray[idx]) - 1);
			}
		}
		return a;
	}
}
