package com.gamblore.ld48.twentyfour;

import java.util.HashMap;
import java.util.Map;

import net.androidpunk.Engine;
import net.androidpunk.FP;
import net.androidpunk.R;
import net.androidpunk.Sfx;
import net.androidpunk.android.PunkActivity.OnBackCallback;
import net.androidpunk.graphics.opengl.Atlas;
import net.androidpunk.graphics.opengl.TextAtlas;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Typeface;

import com.gamblore.ld48.twentyfour.worlds.MainTitle;

public class MainEngine extends Engine {
	
	public static final String TAG = "MainEngine";
	public static Typeface mTypeface;
	
	public static Atlas mAtlas;
	
	public static Player PLAYER;
	
	public static final Map<String, Sfx> SFXS = new HashMap<String, Sfx>();
	
	public static final String SFX_ATTACK = "attack";
	public static final String SFX_ATTACK_CRIT = "attack_crit";
	public static final String SFX_BUBBLE = "bubble";
	public static final String SFX_VICTORY = "victory";
	
	public static final OnBackCallback IN_GAME_BACK_CALLBACK = new OnBackCallback() {
		
		@Override
		public boolean onBack() {
			if (FP.engine != null)
				FP.engine.paused = true;
			AlertDialog.Builder builder = new AlertDialog.Builder(FP.context);
			
			builder.setTitle(R.string.return_to_menu_title);
			builder.setMessage(R.string.return_to_menu_message);
			
			OnClickListener ocl = new OnClickListener() {
				
				public void onClick(DialogInterface dialog, int which) {
					if (which == DialogInterface.BUTTON_POSITIVE) {
						FP.setWorld(new MainTitle());
					}
					if (FP.engine != null)
						FP.engine.paused = false;
				}
			};
			builder.setCancelable(false);
			builder.setPositiveButton(R.string.yes, ocl);
			builder.setNegativeButton(R.string.no, ocl);
			builder.create().show();
			return true;
		}
	};
	
	public MainEngine(int width, int height, float frameRate, boolean fixed) {
		super(width, height, frameRate, fixed);
		
		PLAYER = new Player();
		
		mAtlas = new Atlas("textures/atlas.xml");
		mTypeface = TextAtlas.getFontFromRes(R.raw.font_fixed_bold);
		
		SFXS.put(SFX_ATTACK, new Sfx(R.raw.attack_beep));
		SFXS.put(SFX_ATTACK_CRIT, new Sfx(R.raw.attack_crit));
		SFXS.put(SFX_BUBBLE, new Sfx(R.raw.bubble));
		SFXS.put(SFX_VICTORY, new Sfx(R.raw.victory));
		
		//Log.d(TAG, String.format("Won? %b", PLAYER.hasWonGame()));
		//FP.setWorld(new WinMenu(25, 19));
		//FP.setWorld(new StoryWorld(R.string.intro_story));
		//FP.setWorld(new MainMenu());
		FP.setWorld(new MainTitle());
		//FP.setWorld(new MainMenu());
		
		
		
	}
	 
}
