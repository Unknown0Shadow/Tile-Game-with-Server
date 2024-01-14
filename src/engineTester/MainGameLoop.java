package engineTester;

import java.util.ArrayList;
import java.util.List;
import org.lwjgl.util.vector.Vector2f;

import AI.AStarPathFinder;
import AI.Node;
import entities.Camera;
import entities.Enemy;
import entities.Entity;
import entities.Player;
import models.RawModel;
import models.TexturedModel;
import renderEngine.DisplayManager;
import renderEngine.LevelLoader;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.ModelTexture;
import toolbox.Maths;

public class MainGameLoop {

	public static void main(String[] args) {
		DisplayManager.CreateDisplay(800, 600, "Engine Tester");
		DisplayManager.toggleVsync();
		Loader loader = new Loader();
		
		
		RawModel model = loader.loadToVAO();
		ModelTexture texture_tiles = new ModelTexture(loader.loadTexture("tiles"));
		TexturedModel texturedModel_tiles = new TexturedModel(model, texture_tiles);
		texture_tiles.setAtlasSize(8);
		
		ModelTexture texture_npcs = new ModelTexture(loader.loadTexture("characters"));
		TexturedModel texturedModel_npcs = new TexturedModel(model, texture_npcs);
		texturedModel_npcs.setTransparent(true);
		texture_npcs.setAtlasSize(8);
		
		ModelTexture texture_lights = new ModelTexture(loader.loadTexture("lights"));
		TexturedModel texturedModel_lights = new TexturedModel(model, texture_lights);
		texturedModel_lights.setIsLight(true);
		texturedModel_lights.setTransparent(true);
		texture_lights.setAtlasSize(8);
		
		ModelTexture texture_shadows = new ModelTexture(loader.loadTexture("shadows"));
		TexturedModel texturedModel_shadows = new TexturedModel(model, texture_shadows);
		texturedModel_shadows.setTransparent(true);
		texture_shadows.setAtlasSize(8);
		
		List<Entity> tiles = new ArrayList<Entity>();
		List<Entity> objects = new ArrayList<Entity>();
		List<Entity> npcs = new ArrayList<Entity>();
		List<Entity> effects = new ArrayList<Entity>();
		List<Entity> shadows = new ArrayList<Entity>();
		List<Entity> lights = new ArrayList<Entity>();
		List<List<Entity>> entities = new ArrayList<List<Entity>>();
		entities.add(0, lights);
		entities.add(0, shadows);
		entities.add(0, effects);
		entities.add(0, npcs);
		entities.add(0, objects);
		entities.add(0, tiles);
		
		/*int [][] terrain = {
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,},
				{25,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,1,1,27,1,1,1,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,1,1,27,27,1,27,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,1,1,1,27,27,27,1,1,25,},
				{25,1,1,1,24,24,24,24,24,1,1,1,1,1,1,27,27,1,1,25,},
				{25,1,1,1,24,0,0,0,24,1,1,1,1,1,1,1,1,1,1,25,},
				{25,1,1,1,24,0,0,0,0,1,1,1,1,1,1,1,1,1,1,25,},
				{25,1,1,1,24,0,0,0,24,1,1,1,1,1,1,1,1,1,1,25,},
				{25,1,1,1,24,24,24,24,24,1,1,1,1,1,1,1,1,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,24,24,24,24,24,24,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,24,2,2,24,2,24,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,24,2,24,24,2,24,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,2,2,24,2,2,24,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,24,2,2,2,2,24,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,24,2,24,2,2,24,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,24,24,24,24,24,24,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,25,},
				{25,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,25,},
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,}
		};
		*/
		/*int[][] terrain = {
				{25,25,25,25,25,25,25,25,25,25,},
				{25, 1, 1, 1, 1, 1, 1, 1, 1,25,},
				{25, 1, 1,24, 1,24, 1,24,24,25,},
				{25,24, 1,24, 1,24, 1,24, 1,25,},
				{25, 1, 1, 1,24, 1, 1, 1, 1,25,},
				{25,24, 1,24, 1,24,24, 1, 1,25,},
				{25, 1, 1, 1, 1, 1,24, 1, 1,25,},
				{25,24,24,24,24, 1,24,24, 1,25,},
				{25, 1, 1, 1,24, 1, 1,24, 1,25,},
				{25, 1,24, 1, 1,24, 1,24, 1,25,},
				{25, 1,24,24, 1,24, 1,24, 1,25,},
				{25, 1, 1,24, 1, 1, 1,24, 1,25,},
				{25, 1, 1,24, 1,24,24, 1, 1,25,},
				{25,25,25,25,25,25,25,25,25,25,},
		};
		*/
		/*
		int[][] terrain = {
				{25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28, 28, 28, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24},
				{25, 25, 25, 25, 25, 25, 25, 25, 25, 25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28, 28, 28, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24},
				{25, 25, 27, 25, 25, 25, 25, 25, 25, 25, 24,  3,  3,  3,  3,  3,  3,  3,  3, 24,  6,  6,  6,  6, 26, 26, 26,  6,  6,  6,  6,  6,  6,  6, 26,  6,  6,  6,  6, 24, 24},
				{25, 25, 27, 27, 27,  1,  1,  1,  1, 25, 24,  3,  3,  3,  3,  3,  3,  3,  3,  3,  6,  6,  6,  6,  6,  6,  6,  6,  6, 26,  6,  6,  6,  6, 26,  6, 26, 26,  6, 24, 24},
				{25, 25, 25, 27, 27,  1,  1,  1,  1, 24, 24,  3,  3,  3,  3,  3,  3,  3,  3, 24,  6,  6,  6,  6,  6, 26, 26, 26, 26, 26,  6, 26,  6,  6,  6,  6,  6, 26,  6, 24, 24},
				{25, 25, 25, 27, 27,  1,  1,  1,  1, 24, 24, 24,  3, 24, 24, 24, 24, 24, 24, 24,  6,  6,  6,  6,  6,  6, 26,  6,  6,  6, 26,  6,  6,  6,  6,  6,  6, 26,  6, 24, 24},
				{25, 25, 25,  1,  1,  1,  1,  1,  1, 24,  3,  3,  3,  3,  3, 24,  3,  3,  3, 24,  6,  6,  6,  6,  6,  6, 26,  6, 26,  6, 26,  6,  6, 26,  6,  6,  6, 26,  6, 24, 24},
				{25, 25, 24, 24, 24, 24,  1,  0,  0,  3,  3,  3,  3,  3,  3, 24,  3, 24,  3, 24, 24, 24, 24, 24,  6,  6, 26,  6, 26,  6, 26,  6,  6, 26,  6,  6, 26, 26,  6, 24, 24},
				{25, 25, 24,  2,  2, 24,  1,  0,  1, 24,  3,  3,  3,  3,  3,  3,  3, 24,  3,  3,  4,  4,  4, 24, 28,  6,  6,  6, 26,  6, 28, 28, 28, 28, 28,  6,  6,  6,  6, 24, 24},
				{25, 25, 24,  2,  2,  2,  0,  0,  1, 24,  3,  3,  3,  3,  3, 24,  3, 24,  3, 24,  4,  4,  4, 24, 28, 26,  6,  6, 26,  6, 28,  6,  6,  6, 28,  6,  6, 26,  6, 24, 24},
				{25, 25, 24,  2,  2, 24,  0,  1,  1, 24,  3,  3,  3,  3,  3, 24,  3,  3,  3, 24,  4,  4,  4, 24, 28,  6, 26, 26,  6,  6, 28,  6,  6,  6, 28,  6,  6,  6, 26, 24, 24},
				{25, 25, 24, 24, 24, 24,  2, 24, 25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24,  4,  4,  4, 24, 28,  6, 26,  6,  6,  6, 28,  6,  6,  6, 28,  6,  6,  6,  6, 24, 24},
				{25, 25, 24,  2,  2,  2,  2, 24, 25, 25,  4,  5,  4,  4,  4,  4,  5,  5,  4,  4,  4,  4,  4, 24,  6,  6,  6,  6, 26,  6, 28, 28,  6, 28, 28, 26, 26, 26,  6, 24, 24},
				{25, 25, 24,  2,  2,  2,  2, 24, 25, 25,  4,  5,  4,  4,  4,  4,  5,  4,  4,  4,  4,  4,  4, 24,  6, 26,  6,  6, 26,  6,  6, 26,  6,  6, 26, 26,  6,  6,  6, 24, 24},
				{25, 25, 24,  2,  2,  2,  2, 24, 25, 24,  4,  5,  5,  5,  4,  4,  5,  4,  4,  4,  4,  4,  4, 24,  6,  6, 26, 26, 26,  6,  6, 26,  6, 26,  6, 26,  6,  6,  6, 24, 24},
				{25, 25, 24,  2,  2,  2,  2, 24, 25, 24,  4,  4,  5,  5,  5,  5,  5,  4,  4,  4,  4,  4,  4, 24,  6,  6,  6,  6,  6, 26,  6, 26,  6, 26,  6, 26,  6,  6, 26, 24, 24},
				{25, 25, 24,  2,  2,  2,  2, 24, 25, 24,  4,  4,  4,  5,  5,  4,  4,  4,  4,  4,  4,  4,  4, 24,  6,  6, 28, 28,  6,  6, 26,  6,  6, 26,  6,  6,  6, 26,  6, 24, 24},
				{25, 25, 24,  2,  2,  2,  2, 24, 25, 24,  4,  4,  4,  4,  5,  4,  4,  4,  4,  4,  4,  4,  4, 24,  6,  6,  6,  6,  6, 26,  6,  6, 26,  6,  6,  6,  6, 26,  6, 24, 24},
				{25, 25, 24, 24, 24, 24, 24, 24, 25, 24,  4,  4,  4,  4,  5,  4,  4,  5,  4,  4,  4,  4,  4, 24,  6,  6, 26, 26, 26, 26,  6,  6, 26,  6,  6, 26,  6, 26,  6, 24, 24},
				{25, 25, 25, 25, 25, 25, 25, 25, 25, 24,  4,  4,  4,  4,  5,  4,  4,  4,  5,  4,  4,  4,  4, 24,  6,  6,  6,  6,  6,  6,  6,  6,  6,  6,  6, 26,  6,  6,  6, 24, 24},
				{25, 25, 25, 25, 25, 25, 25, 25, 25, 24,  4,  4,  4,  4,  5,  4,  4,  4,  5,  4,  4,  4,  4, 24,  6,  6,  6,  6,  6,  6,  6,  6,  6,  6,  6, 26,  6,  6,  6, 24, 24},
				{25, 25, 25, 25, 25, 25, 25, 25, 25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24},
				{25, 25, 25, 25, 25, 25, 25, 25, 25, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24, 24},
		};
		*/
		// 71x25
		/*
		int[][] terrain = {
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,28,28,28,28,28},
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,28,28,28,28,28,28,28,28},
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,28,28,28,28,28,28,28,28,28,28,28},
				{25,25,25,25,25,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,27,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,28,28,28,28,28,28,28,28,28,28,28,28,28},
				{25,25,25,25,25,1,1,24,24,24,24,24,24,1,1,1,1,1,1,1,1,27,1,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,24,4,4,4,4,4,4,5,4,4,4,4,4,24,3,3,3,3,24,6,26,6,6,6,26,6,6,6,6,28,28,28},
				{25,25,25,25,1,1,1,24,2,2,2,2,24,1,1,1,1,1,1,1,1,27,1,1,1,1,1,1,1,1,1,1,25,25,25,25,25,25,25,24,5,5,5,4,4,5,5,4,4,4,4,4,24,3,3,3,3,24,6,6,6,26,6,26,6,6,26,6,28,28,28},
				{25,25,25,25,1,1,1,24,2,2,2,2,24,1,1,1,1,1,1,1,1,1,27,1,1,1,1,1,1,1,1,1,1,1,1,25,25,25,25,24,4,4,5,5,5,5,5,4,4,4,4,4,3,3,3,3,3,24,6,26,26,6,6,26,6,26,26,6,28,28,28},
				{25,25,25,1,1,1,1,24,2,2,2,2,2,0,0,0,0,0,0,1,1,1,27,1,1,1,1,1,1,1,1,1,1,1,1,1,25,25,25,24,4,4,4,5,5,5,5,5,5,4,4,4,3,3,3,3,3,24,6,6,26,26,6,6,6,6,6,6,28,28,28},
				{25,25,25,1,1,1,1,24,2,2,2,2,24,1,1,0,0,0,0,0,1,1,1,27,1,1,1,1,1,1,1,1,1,1,1,1,1,25,25,24,4,4,4,4,5,5,4,4,5,4,4,4,24,3,3,3,3,24,6,6,6,6,6,6,26,6,6,26,28,28,28},
				{25,25,25,1,1,1,1,24,24,24,24,24,24,1,1,0,0,0,0,0,0,0,2,2,2,1,1,1,1,1,25,1,1,1,1,1,1,25,25,24,4,4,4,4,4,4,4,4,5,4,4,4,24,3,3,3,3,24,26,26,6,26,6,26,26,26,6,26,28,28,28},
				{25,25,25,1,1,1,1,1,1,1,1,1,1,1,0,0,0,0,0,0,0,1,1,27,1,1,1,1,25,25,25,25,1,1,1,1,1,25,25,24,4,4,4,4,4,4,4,4,5,5,4,4,24,3,3,3,3,24,6,26,6,26,6,6,6,6,6,6,28,28,28},
				{25,25,25,1,24,24,24,24,24,24,1,1,1,0,1,0,0,0,0,0,0,1,1,27,1,1,25,25,25,25,25,25,25,24,3,3,24,25,25,24,4,4,4,24,24,24,24,24,24,24,24,24,24,24,3,3,24,24,6,26,6,26,26,26,26,26,26,6,28,28,28},
				{25,25,25,1,24,2,2,2,2,24,1,0,0,1,1,1,0,1,1,1,1,1,27,27,25,25,25,25,25,25,24,24,24,24,3,3,24,24,24,24,4,4,24,24,28,28,28,28,28,28,28,28,28,28,6,6,6,6,6,6,6,26,6,6,6,6,6,6,28,28,28},
				{25,25,25,1,24,2,2,2,2,2,0,1,1,1,1,1,0,1,1,1,1,27,27,27,27,27,25,25,25,25,24,3,3,3,3,3,3,3,24,3,3,3,3,24,28,6,6,6,6,26,26,6,6,6,6,6,6,26,26,26,28,28,6,28,28,26,26,6,28,28,28},
				{25,25,25,1,24,2,2,2,2,24,1,1,1,1,24,24,2,24,24,24,1,27,27,27,27,27,25,25,25,25,24,3,3,3,3,3,3,3,24,3,24,24,3,24,28,6,6,6,6,26,26,6,6,6,6,6,6,26,6,6,28,6,6,6,28,6,26,6,28,28,28},
				{25,25,25,1,24,2,2,2,2,24,1,1,1,1,24,2,2,2,2,24,1,27,27,27,27,25,25,25,25,25,24,3,3,3,3,3,3,3,24,3,24,24,3,24,28,6,26,26,26,26,26,6,6,26,26,26,26,26,6,6,28,6,6,6,28,6,6,6,28,28,28},
				{25,25,25,1,24,24,24,24,24,24,1,1,1,1,24,2,2,2,2,24,1,1,27,27,27,25,25,25,25,25,24,3,3,3,3,3,3,3,24,3,24,24,3,24,28,6,6,6,6,6,6,6,6,26,6,6,6,26,6,6,28,6,6,6,28,6,26,6,28,28,28},
				{25,25,25,1,1,1,1,1,1,1,1,1,1,1,24,2,2,2,2,24,1,1,27,25,25,25,25,25,25,25,24,3,3,24,24,24,24,24,24,3,24,24,3,24,28,6,6,26,26,26,26,26,26,26,26,26,6,26,6,26,28,28,28,28,28,26,26,6,28,28,28},
				{25,25,25,25,1,1,1,1,1,1,1,1,1,1,24,2,2,2,2,24,1,1,25,25,25,25,25,25,25,25,24,3,3,3,3,3,3,3,24,3,24,24,3,24,28,6,26,26,6,6,6,6,6,6,6,6,6,26,6,6,6,6,6,26,6,6,6,6,28,28,28},
				{25,25,25,25,25,25,25,25,1,1,1,1,1,1,24,24,24,24,24,24,1,1,25,25,25,25,25,25,25,25,24,3,3,3,3,3,3,3,3,3,24,24,3,24,28,6,6,6,6,6,26,26,6,6,26,26,26,26,26,26,6,26,6,26,26,6,26,26,28,28,28},
				{25,25,25,25,25,25,25,25,25,25,25,1,1,1,1,1,1,1,1,1,1,1,25,25,25,25,25,25,25,25,24,3,3,3,3,3,3,3,24,3,3,3,3,24,28,28,28,28,28,28,28,26,6,6,6,6,6,6,6,6,6,26,6,6,6,6,6,6,28,28,28},
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,1,1,1,1,1,1,1,1,25,25,25,25,25,25,25,25,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,24,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28},
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28,28},
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,28,28,28,28,28,28,28,28,28,28,28,28,28,28},
				{25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,25,28,28,28,28,28,28,28,28,28,28,28,28},
		};
		int[][] load_shadows = {
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,2,2,2,2,2,2,2,2,2,2,2,-1,8,8,8,8,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,4,-1,-1,-1,3,3,4,4,-1,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,2,2,2,2,2,2,2,2,2,2,2,-1,9,9,9,9,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,4,-1,-1,-1,3,4,4,5,-1,4,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,2,2,2,2,2,2,2,2,2,2,2,10,10,10,10,10,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,-1,-1,-1,4,4,5,5,6,4,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,2,2,2,2,2,2,2,2,2,2,2,10,11,11,11,11,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,-1,-1,-1,3,4,4,5,-1,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,2,2,2,2,2,2,2,2,2,2,2,-1,11,12,12,11,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,2,2,2,2,2,2,2,2,2,2,2,2,-1,12,13,13,12,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,4,4,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,2,2,2,2,2,2,2,2,2,2,2,2,-1,13,14,14,13,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,7,7,-1,-1,-1,-1,2,2,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,15,15,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,3,4,4,5,-1,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,7,7,-1,-1,-1,-1,2,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,4,4,5,5,6,4,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,5,6,6,6,6,6,6,-1,2,2,2,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,6,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,3,4,4,5,-1,4,4,4,-1,-1,-1,6,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,5,5,6,6,6,6,5,-1,2,-1,-1,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,5,4,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,3,3,4,4,-1,4,4,-1,-1,-1,5,5,5,4,-1,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,5,5,5,5,5,5,-1,2,-1,-1,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,3,4,3,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,4,5,4,4,-1,4,4,4,-1,-1,-1,-1,-1,-1,-1,-1,4,4,5,5,5,5,4,-1,2,-1,-1,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,2,3,2,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,4,4,3,-1,4,4,4,-1,-1,-1,-1,-1,-1,-1,-1,4,4,-1,-1,-1,-1,-1,-1,2,-1,-1,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,3,4,3,3,-1,4,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,3,3,3,3,2,2,2,-1,2,-1,-1,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,3,3,3,2,2,2,2,2,2,-1,-1,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,3,3,2,2,2,2,2,-1,2,2,2,2,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,4,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
		};
		int[][] load_lights = {
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,20,20,20,20,-1,20,16,20,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,20,20,-1,20,-1,20,20,-1,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,-1,20,20,-1,20,-1,-1,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,20,-1,-1,20,20,20,20,20,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,20,20,20,16,20,-1,20,20,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,20,-1,-1,-1,20,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,20,-1,20,20,20,20,20,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,20,-1,-1,-1,-1,-1,-1,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,20,20,20,20,-1,20,20,20,20,20,16,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,16,20,-1,-1,20,16,16,16,16,20,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,20,20,20,-1,-1,20,16,20,20,20,20,-1,20,20,-1,-1,-1,-1,-1,20,-1,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,-1,-1,-1,-1,20,20,-1,-1,-1,-1,-1,20,16,-1,-1,-1,-1,-1,16,20,16,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,20,20,20,20,20,20,20,-1,20,20,20,-1,20,20,-1,-1,-1,-1,-1,20,-1,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,20,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,20,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,-1,-1,20,20,20,20,20,20,20,20,20,-1,20,20,20,20,20,-1,20,20,20,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,16,20,20,16,20,-1,-1,20,20,-1,-1,-1,-1,-1,-1,20,-1,20,-1,-1,20,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,20,20,20,20,20,20,20,20,20,-1,20,20,20,16,20,20,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},
				{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1},	
		};
		*/
		/*
		List<int[][]> ints = new ArrayList<>();
		ints.add(0, load_lights);
		ints.add(0, load_shadows);
		ints.add(0, terrain);
		LevelLoader.saveLevel("myLevel", ints);
		*/
		List<int[][]> ints = LevelLoader.loadLevel2("myLevel");
		int[][] terrain = ints.get(0);
		int[][] load_shadows = ints.get(1);
		int[][] load_lights = ints.get(2);
		
		int indices_width = terrain[0].length;
		int indices_height = terrain.length;
		for(int i = indices_height - 1; i >= 0; i--){
			for(int j = 0; j < indices_width; j++){
				tiles.add(new Entity(texturedModel_tiles,terrain[i][j],
						new Vector2f(j,i), 0, 0, 0, 1));
				if(load_shadows[i][j] != -1)
					shadows.add(new Entity(texturedModel_shadows, load_shadows[i][j], new Vector2f(j, i), 0, 0, 0, 1));
				if(load_lights[i][j] != -1)
					lights.add(new Entity(texturedModel_lights, load_lights[i][j], new Vector2f(j, i), 0, 0, 0, 1));
			}
		}
		
		
		
		Camera camera = new Camera(0.05f);
		Player player = new Player(texturedModel_npcs, new Vector2f(7, 14), 0, 0, 0, 0.8f, camera);
		Enemy townFolk1 = new Enemy(texturedModel_npcs, 8, new Vector2f(10, 6), 0, 0, 0, 0.8f);
		Enemy townFolk2 = new Enemy(texturedModel_npcs, 8, new Vector2f(27, 7), 0, 0, 0, 0.8f);
		Enemy troll = new Enemy(texturedModel_npcs, 16, new Vector2f(35, 19), 0, 0, 0, 1f);
		Enemy frost1 = new Enemy(texturedModel_npcs, 24, new Vector2f(50, 4), 0, 0, 0, 0.7f);
		Enemy frost2 = new Enemy(texturedModel_npcs, 24, new Vector2f(46, 9), 0, 0, 0, 0.7f);
		Enemy husk1 = new Enemy(texturedModel_npcs, 32, new Vector2f(52, 20), 0, 0, 0, 0.8f);
		Enemy husk2 = new Enemy(texturedModel_npcs, 32, new Vector2f(67, 18), 0, 0, 0, 0.8f);
		Enemy husk3 = new Enemy(texturedModel_npcs, 32, new Vector2f(64, 5), 0, 0, 0, 0.8f);
		Enemy husk4 = new Enemy(texturedModel_npcs, 32, new Vector2f(46, 16), 0, 0, 0, 0.8f);
		Enemy boss = new Enemy(texturedModel_npcs, 40, new Vector2f(62, 15), 0, 0, 0, 1.2f);
		
		Entity blueLamp1 = new Entity(texturedModel_npcs,48,new Vector2f(41,7), 0, 0, 0, 1);
		Entity blueLamp2 = new Entity(texturedModel_npcs,48,new Vector2f(50,8), 0, 0, 0, 1);
		
		
		
		townFolk1.setPathFinder(new AStarPathFinder(terrain));
		townFolk2.setPathFinder(new AStarPathFinder(terrain));
		troll.setPathFinder(new AStarPathFinder(terrain));
		frost1.setPathFinder(new AStarPathFinder(terrain));
		frost2.setPathFinder(new AStarPathFinder(terrain));
		husk1.setPathFinder(new AStarPathFinder(terrain));
		husk2.setPathFinder(new AStarPathFinder(terrain));
		husk3.setPathFinder(new AStarPathFinder(terrain));
		husk4.setPathFinder(new AStarPathFinder(terrain));
		boss.setPathFinder(new AStarPathFinder(terrain));
		
		townFolk1.setSpeed(2);	townFolk1.setDectectRange(-1);
		townFolk2.setSpeed(2);	townFolk2.setDectectRange(-1);
		troll.setSpeed(1);		//troll.setDectectRange(3);
		frost1.setSpeed(2);		frost1.setDectectRange(-1);
		frost2.setSpeed(2);		frost2.setDectectRange(-1);
		husk1.setSpeed(2);		husk1.setDectectRange(5);
		husk2.setSpeed(2);		husk2.setDectectRange(5);
		husk3.setSpeed(2);		husk3.setDectectRange(5);
		husk4.setSpeed(2);		husk4.setDectectRange(5);
		boss.setSpeed(3);		boss.setDectectRange(7);
		
		npcs.add(player);
		npcs.add(townFolk1);
		npcs.add(townFolk2);
		npcs.add(troll);
		npcs.add(frost1);
		npcs.add(frost2);
		npcs.add(husk1);
		npcs.add(husk2);
		npcs.add(husk3);
		npcs.add(husk4);
		npcs.add(boss);
		
//		player.lights = Entity.createLight(texturedModel_lights, 0, player.getPosition().x,
//				player.getPosition().y);
		blueLamp1.lights = Entity.createLight(texturedModel_lights, 0, blueLamp1.getPosition().x,
				blueLamp1.getPosition().y);
		blueLamp2.lights = Entity.createLight(texturedModel_lights, 0, blueLamp2.getPosition().x,
				blueLamp2.getPosition().y);
		//entities.add(player.lights);
		entities.add(blueLamp1.lights);
		entities.add(blueLamp1.lights);
		entities.add(blueLamp2.lights);
		entities.add(blueLamp2.lights);
		objects.add(blueLamp1);
		objects.add(blueLamp2);
		

		MasterRenderer renderer = new MasterRenderer();
		//LevelLoader.saveLevel("testLevel", tiles, npcs, shadows, lights);
		/*
		ArrayList <Object> data = LevelLoader.loadLevel("testLevel");
		tiles = (List<Entity>) data.get(0);
		npcs = (List<Entity>) data.get(1);
		shadows = (List<Entity>) data.get(2);
		lights = (List<Entity>) data.get(3);
		*/

		while(!DisplayManager.isCloseRequested()){
			camera.move();
			for(Entity character:npcs){
				character.move(terrain);
				//character.move(tiles);
				if(character.getClass() == Enemy.class){
					if(Maths.distancef(player.getPosition(), character.getPosition()) <
							((Enemy)character).getDetectRange()){
						((Enemy)character).setTarget(player.getPosition());
					}
					if(((Enemy)character).getPath()!= null){
						for(Node node:((Enemy)character).getPath()){
							effects.add(0, new Entity(texturedModel_lights,13,
									new Vector2f(node.x, node.y), 0, 0, 0, 1));
						}
					}
					
				}
			}
			if(player.noTarget){
				player.target = false;
				for(Entity npc:npcs){
					if(npc != player){
						((Enemy)npc).abortTarget();
					}
				}
				player.noTarget = false;
			}
			if(player.target){
				for(Entity npc:npcs){
					if(npc != player){
						((Enemy)npc).setTarget(player.getPosition());
					}
				}
				player.target = false;
			}
//			for(Entity tile:tiles){renderer.processEntity(tile);}
//			renderer.processEntity(player);
//			for(Entity shadow:shadows){renderer.processEntity(shadow);}
//			for(Entity light:lights){renderer.processEntity(light);}
			renderer.render(camera, entities);
			effects.clear();
			DisplayManager.updateDisplay();
		}
		System.out.println("Closed!");
		
		renderer.cleanUp();
		loader.cleanUp();
		DisplayManager.closeDisplay();

	}

}
