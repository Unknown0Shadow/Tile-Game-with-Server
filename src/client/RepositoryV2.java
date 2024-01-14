package client;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import AI.AStarPathFinder;
import entities.Camera;
import entities.Enemy;
import entities.Entity;
import entities.Player;
import entities.Trigger;
import models.RawModel;
import models.TexturedModel;
import renderEngine.LevelLoader;
import renderEngine.Loader;
import renderEngine.MasterRenderer;
import textures.ModelTexture;
import toolbox.Rectangle;
/**
 * Handles data.<br>
 * <br>
 * Variables:<br>
 * tiles, objects, npcs, effects, shadows, lights - ArrayList of Entity objects<br>
 * entities - ArrayList of ArrayLists of Entity objects, stores all entities<br>
 * triggers - ArrayList of Trigger objects<br>
 * load_terrain, load_shadows, load_lights matrices containing the texture indices of corresponding tiles<br>
 * townFolk1, townFolk2, troll, frost1, frost2, husk1, husk2, husk3, husk4, boss - Enemy, enemies<br>
 * player - Player, the player<br>
 * spawn - Vector2f, the spawn position of players<br>
 * texturedModel_tiles, texturedModel_npcs, texturedModel_lights, texturedModel_shadows - TexturedModel, the textured models<br>
 * loader - Loader, an instance of the Loader class<br>
 * renderer - MasterRenderer, an instance of the MasterRenderer class<br>
 */
public class RepositoryV2 {
	private static List<Entity> tiles, objects, npcs, effects, shadows, lights;
	private static List<List<Entity>> entities;
	private static List<Trigger> triggers;
	
	private static int[][] load_terrain, load_shadows, load_lights;
	private static Enemy townFolk1, townFolk2, troll, frost1, frost2, husk1, husk2, husk3, husk4, boss;
	private static Player player;
	
	private static Vector2f spawn = new Vector2f(7, 14);
	
	private static TexturedModel texturedModel_tiles, texturedModel_npcs, texturedModel_lights, texturedModel_shadows;
	private static Loader loader;
	private static MasterRenderer renderer;
	
	/**
	 * Initialises essential variables.
	 */
	public static void init(){
		setupOpenGL();
		renderer = new MasterRenderer();
		tiles = new ArrayList<Entity>();
		objects = new ArrayList<Entity>();
		npcs = new ArrayList<Entity>();
		effects = new ArrayList<Entity>();
		shadows = new ArrayList<Entity>();
		lights = new ArrayList<Entity>();
		triggers = new ArrayList<Trigger>();
		entities = new ArrayList<List<Entity>>();
		entities.add(0, lights);
		entities.add(0, shadows);
		entities.add(0, effects);
		entities.add(0, npcs);
		entities.add(0, objects);
		entities.add(0, tiles);
		
		List<int[][]> ints = LevelLoader.loadLevel2("myLevel");
		load_terrain = ints.get(0);
		load_shadows = ints.get(1);
		load_lights = ints.get(2);
		
		
		int indices_width = load_terrain[0].length;
		int indices_height = load_terrain.length;
		for(int i = indices_height - 1; i >= 0; i--){
			for(int j = 0; j < indices_width; j++){
				tiles.add(new Entity(texturedModel_tiles,load_terrain[i][j],
						new Vector2f(j,i), 0, 0, 0, 1));
				if(load_shadows[i][j] != -1)
					shadows.add(new Entity(texturedModel_shadows, load_shadows[i][j], new Vector2f(j, i), 0, 0, 0, 1));
				if(load_lights[i][j] != -1)
					lights.add(new Entity(texturedModel_lights, load_lights[i][j], new Vector2f(j, i), 0, 0, 0, 1));
			}
		}
		
		Entity blueLamp1 = new Entity(texturedModel_npcs,48,new Vector2f(41,7), 0, 0, 0, 1);
		Entity blueLamp2 = new Entity(texturedModel_npcs,48,new Vector2f(50,8), 0, 0, 0, 1);
		blueLamp1.lights = Entity.createLight(texturedModel_lights, 0, blueLamp1.getPosition().x,
				blueLamp1.getPosition().y);
		blueLamp2.lights = Entity.createLight(texturedModel_lights, 0, blueLamp2.getPosition().x,
				blueLamp2.getPosition().y);
		entities.add(blueLamp1.lights);
		entities.add(blueLamp2.lights);
		entities.add(blueLamp1.lights);
		entities.add(blueLamp2.lights);	// double light = double intensity
		objects.add(blueLamp1);
		objects.add(blueLamp2);
	}
	
	/**
	 * Initialises npcs, entities and player.
	 */
	public static void restart(){
		npcs.clear();
		player.setPosition(new Vector2f(spawn.x, spawn.y));
		townFolk1 = new Enemy(texturedModel_npcs, 8, new Vector2f(10, 6), 0, 0, 0, 0.8f);
		townFolk2 = new Enemy(texturedModel_npcs, 8, new Vector2f(27, 7), 0, 0, 0, 0.8f);
		troll = new Enemy(texturedModel_npcs, 16, new Vector2f(35, 19), 0, 0, 0, 1f);
		frost1 = new Enemy(texturedModel_npcs, 24, new Vector2f(50, 4), 0, 0, 0, 0.7f);
		frost2 = new Enemy(texturedModel_npcs, 24, new Vector2f(46, 9), 0, 0, 0, 0.7f);
		husk1 = new Enemy(texturedModel_npcs, 32, new Vector2f(52, 20), 0, 0, 0, 0.8f);
		husk2 = new Enemy(texturedModel_npcs, 32, new Vector2f(67, 18), 0, 0, 0, 0.8f);
		husk3 = new Enemy(texturedModel_npcs, 32, new Vector2f(64, 5), 0, 0, 0, 0.8f);
		husk4 = new Enemy(texturedModel_npcs, 32, new Vector2f(46, 16), 0, 0, 0, 0.8f);
		boss = new Enemy(texturedModel_npcs, 40, new Vector2f(62, 15), 0, 0, 0, 1.2f);
		Trigger triggerTown = new Trigger(new Rectangle(15,6,6,7));	
		Trigger triggerFrost = new Trigger(new Rectangle(40,4,11,6));
		triggerTown.setActionID(0);
		triggerFrost.setActionID(1);
		triggers.add(triggerTown);
		triggers.add(triggerFrost);
		
		townFolk1.setPathFinder(new AStarPathFinder(load_terrain));
		townFolk2.setPathFinder(new AStarPathFinder(load_terrain));
		troll.setPathFinder(new AStarPathFinder(load_terrain));
		frost1.setPathFinder(new AStarPathFinder(load_terrain));
		frost2.setPathFinder(new AStarPathFinder(load_terrain));
		husk1.setPathFinder(new AStarPathFinder(load_terrain));
		husk2.setPathFinder(new AStarPathFinder(load_terrain));
		husk3.setPathFinder(new AStarPathFinder(load_terrain));
		husk4.setPathFinder(new AStarPathFinder(load_terrain));
		boss.setPathFinder(new AStarPathFinder(load_terrain));
		
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
		npcs.add(player);
	}
	/**
	 * Initialises models for OpenGL.
	 */
	private static void setupOpenGL(){
		loader = new Loader();
		RawModel model = loader.loadToVAO();
		ModelTexture texture_tiles = new ModelTexture(loader.loadTexture("tiles"));
		texturedModel_tiles = new TexturedModel(model, texture_tiles);
		texture_tiles.setAtlasSize(8);
		
		ModelTexture texture_npcs = new ModelTexture(loader.loadTexture("characters"));
		texturedModel_npcs = new TexturedModel(model, texture_npcs);
		texturedModel_npcs.setTransparent(true);
		texture_npcs.setAtlasSize(8);
		
		ModelTexture texture_lights = new ModelTexture(loader.loadTexture("lights"));
		texturedModel_lights = new TexturedModel(model, texture_lights);
		texturedModel_lights.setIsLight(true);
		texturedModel_lights.setTransparent(true);
		texture_lights.setAtlasSize(8);
		
		ModelTexture texture_shadows = new ModelTexture(loader.loadTexture("shadows"));
		texturedModel_shadows = new TexturedModel(model, texture_shadows);
		texturedModel_shadows.setTransparent(true);
		texture_shadows.setAtlasSize(8);
	}
	/**
	 * Creates an event upon triggering a trigger.
	 * @param action the id of the action
	 */
	public static void activateTrigger(int action){
		if(action == 0){
			townFolk1.setTarget(player.getPosition());
			townFolk2.setTarget(player.getPosition());
		}else if(action == 1){
			frost1.setTarget(player.getPosition());
			frost2.setTarget(player.getPosition());
		}
	}
	/**
	 * Inserts an effect into the "effects" list.
	 * @param textureIndex the index of the texture atlas
	 * @param pos position on map
	 * @param rotX rotation around x axis (not used)
	 * @param rotY rotation around y axis (not used)
	 * @param rotZ rotation around z axis (not used)
	 * @param scale the size of the tile
	 */
	public static void addEffect(int textureIndex, Vector2f pos, float rotX,
			float rotY, float rotZ, float scale){
		Entity effect = new Entity(texturedModel_lights, textureIndex, pos, rotX, rotY, rotZ, scale);
		effects.add(0, effect);
	}
	/**
	 * Clears the "effects" list.
	 */
	public static void clearEffects(){
		effects.clear();
	}
	public static void setPlayer(Player pl){
		player = pl;
	}
	/**
	 * Creates a player object.
	 * @param position the position on the map
	 * @param scale the size of the player
	 * @param camera the point of view of the player
	 * @return the player object
	 */
	public static Player createPlayer(Vector2f position, float scale, Camera camera){
		Player pl = new Player(texturedModel_npcs, position, 0, 0, 0, scale, camera);
		return pl;
	}
	
	public static List<Entity> getNPCs(){
		return npcs;
	}
	public static int[][] getTerrain(){
		return load_terrain;
	}
	public static List<Entity> getEffects(){
		return effects;
	}
	public static List<List<Entity>> getEntities(){
		return entities;
	}
	public static List<Trigger> getTriggers(){
		return triggers;
	}
	public static Loader getLoader(){
		return loader;
	}
	public static MasterRenderer getRenderer(){
		return renderer;
	}

}
