package com.gamblore.ld48.twentyfour.worlds;

import net.androidpunk.Entity;
import net.androidpunk.FP;
import net.androidpunk.World;
import net.androidpunk.graphics.atlas.GraphicList;
import net.androidpunk.graphics.atlas.Image;
import net.androidpunk.utils.Input;

import com.gamblore.ld48.twentyfour.MainEngine;
import com.gamblore.ld48.twentyfour.entities.AntKit;

public class MainMenu extends World {

	public MainMenu() {
		super();
		
		Image background = new Image(MainEngine.mAtlas.getSubTexture("titlescreen"));
		background.scale = 2;
		Entity e = new Entity();
		
		e.setGraphic(new GraphicList(background));
		
		add(e);
	}

	@Override
	public void update() {
		super.update();
		if (Input.mousePressed) {
			FP.setWorld(new FightWorld(MainEngine.PLAYER.getAntKit(), AntKit.getRandomAntKit()));
		}
	}
	
	
}
