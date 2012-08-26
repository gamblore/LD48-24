package com.gamblore.ld48.twentyfour.worlds;

import java.util.Vector;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.flashcompat.OnCompleteCallback;
import net.androidpunk.graphics.atlas.AtlasGraphic;
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
import com.gamblore.ld48.twentyfour.entities.Potion;

public class EvolveMenu extends World {

	private static final String TAG = "EvolveMenu";
	
	private static final int[][] POTION_POSITIONS = new int[][] { new int[] { 48*2, 48*2 }, new int[] { 3 * 48*2, 48*2}, new int[] { 48*2, 3 * 48*2 }, new int[] {3 * 48*2,3 * 48*2 }};
	
	private static final String COST_TEXT_FORMAT = "Cost:  $%d";
	
	private AtlasText mEvolveText, mBackText, mCostText, mFundsText, mEvolvingText;
	
	private int mCost;
	
	private AntGroup mAntGroup;
	private Ant mAnt;
	
	private ColorTween mColorTween;
	
	private Vector<Integer> mColorTransforms = new Vector<Integer>();
	
	public EvolveMenu(AntGroup group) {
		
		//TODO set this to go back to MainMenu?
		FP.activity.setOnBackCallback(MainEngine.IN_GAME_BACK_CALLBACK);
		
		mAntGroup = group;
		
		Entity e = new Entity();
		
		Image background = new Image(MainEngine.mAtlas.getSubTexture("evolvemenu"));
		background.scale = 2;
		
		mCostText = new AtlasText(String.format(COST_TEXT_FORMAT, 0), 14, MainEngine.mTypeface);
		mCostText.x = 16 * 2 + 4;
		mCostText.y = 304 * 2 + 4;
		
		mFundsText = new AtlasText(String.format("$%d", MainEngine.PLAYER.getFunds()), 14, MainEngine.mTypeface);
		mFundsText.x = FP.screen.getWidth()/2 - mFundsText.getWidth()/2;
		mFundsText.y = 4;
		
		mEvolvingText = new AtlasText("Evolving", 30, MainEngine.mTypeface);
		mEvolvingText.x = FP.screen.getWidth()/2 - mEvolvingText.getWidth()/2;
		mEvolvingText.y = 208 * 2;
		mEvolvingText.visible = false;
		
		e.setGraphic(new GraphicList(background, mCostText, mFundsText, mEvolvingText));
		e.setLayer(10);
		
		Entity mBackEntity = new Entity(16*2, 336*2);
		mBackEntity.setHitbox(96 * 2, 48 * 2);
		mBackText = new AtlasText("Back", 24, MainEngine.mTypeface);
		mBackText.x = mBackEntity.width/2 - mBackText.getWidth()/2;
		mBackText.y = 16 * 2;
		mBackEntity.setGraphic(mBackText);
		mBackEntity.setType("back");
		add(mBackEntity);
		
		Entity mEvolveEntity = new Entity(128*2, 336*2);
		mEvolveEntity.setHitbox(96 * 2, 48 * 2);
		mEvolveText = new AtlasText("Evolve", 24, MainEngine.mTypeface);
		mEvolveText.x = mEvolveEntity.width/2 - mEvolveText.getWidth()/2;
		mEvolveText.y = 16 * 2;
		mEvolveEntity.setGraphic(mEvolveText);
		mEvolveEntity.setType("evolve");
		add(mEvolveEntity);
		
		for (int i = 1; i < 5; i++) {
			Potion p = new Potion(i);
			p.x = POTION_POSITIONS[i-1][0];
			p.y = POTION_POSITIONS[i-1][1];
			
			add(p);
		}
		
		mAnt = group.getAnt();
		mAnt.x = 48*2 * 2;
		mAnt.y = 48*2 * 5;
		mAnt.visible = true;
		((AtlasGraphic)mAnt.getGraphic()).scale = 3;
		
		add(mAnt);
		
		add(e);
		
		mColorTween = new ColorTween(null, PERSIST);
		addTween(mColorTween);
	}

	@Override
	public void update() {
		super.update();
		
		if (Input.mousePressed) {
			Point p = Input.getTouches()[0];
			Entity e;
			if ((e = collidePoint(Potion.TYPE, p.x, p.y)) != null) {
				Potion potion = (Potion)e;
				potion.togglePour();
				updatePrice();
			}
			if ((e = collidePoint("back", p.x, p.y)) != null) {
				FP.setWorld(new MainMenu());
			} else if ((e = collidePoint("evolve", p.x, p.y)) != null) {
				if (mCost > 0 && mCost <= MainEngine.PLAYER.getFunds()) {
					Log.d(TAG, "EVOLVING!");
					mEvolvingText.visible = true;
					Vector<Entity> potions = new Vector<Entity>();
					getType(Potion.TYPE, potions);
					for (int i = 0; i < potions.size(); i++) {
						Potion potion = (Potion)potions.get(i);
						if (potion.isPouring()) {
							MainEngine.PLAYER.useFunds(potion.getCost());
							mFundsText.setText(String.format("$%d", MainEngine.PLAYER.getFunds()));
							
							mColorTransforms.add(mAntGroup.getAntColor());
							mAntGroup.evolve(potion.getPotionType());
							mColorTransforms.add(mAntGroup.getAntColor());
						}
					}
					//MainEngine.PLAYER.save();
				}
			}
		}
		
		if (mColorTransforms.size() > 0 && !mColorTween.active) {
			if (mColorTransforms.size() == 2) {
				removeTween(mColorTween);
				
				mColorTween = new ColorTween(new OnCompleteCallback() {
					
					@Override
					public void completed() {
						FP.setWorld(new MainMenu());
					}
				}, ONESHOT);
				
				addTween(mColorTween);
				mColorTween.tween(2.0f, mColorTransforms.remove(0), mColorTransforms.remove(0));
			} else {
				mColorTween.tween(2.0f, mColorTransforms.remove(0), mColorTransforms.remove(0));
			}
		}
		if (mColorTween.active) {
			((AtlasGraphic)mAnt.getGraphic()).setColor(mColorTween.color);
		}
	}
	
	private void updatePrice() {
		int price = 0;
		Vector<Entity> potions = new Vector<Entity>();
		getType(Potion.TYPE, potions);
		for (int i = 0; i < potions.size(); i++) {
			Potion p = (Potion)potions.get(i);
			if (p.isPouring()) {
				price += p.getCost();
			}
		}
		
		mCost = price;
		if (mCost == 0 || mCost > MainEngine.PLAYER.getFunds()) {
			mCostText.setColor(0xffdd3333);
			mEvolveText.setColor(0xff666666);
		} else {
			mCostText.setColor(0xffffffff);
			mEvolveText.setColor(0xffffffff);
		}
		mCostText.setText(String.format(COST_TEXT_FORMAT, price));
	}
	
	
}
