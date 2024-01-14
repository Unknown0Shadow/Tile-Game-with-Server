package entities;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;

import AI.AStarPathFinder;
import AI.Node;
import models.TexturedModel;
import renderEngine.DisplayManager;
import toolbox.Maths;
/**
 * Enemy NPC.<br>
 * <br>
 * Variables:<br>
 * speed - float, the multiplier for movement<br>
 * target - Vector2f, the position of the target entity<br>
 * currentTarget - Vector2f, the short range target to move to, the next tile's position<br>
 * error - float, acts as a tolerance factor, allowing the entity to "reach" a target position without trying to touch the exact pixels, allowing it to move more freely and prevent getting perpetually stuck<br>
 * pathFinder - AStarPathFinder, an instance of the AStarPathFinder which stores the nodes. This needs instantiated for each npc because the starting and ending nodes may differ from one entity to another<br>
 * detectRange - integer, the minimum distance to start targeting the player
 */
public class Enemy extends Entity{

	private float speed = 4f;
	private Vector2f target, currentTarget = new Vector2f(0,0);
	private float error = 0.05f;
	private AStarPathFinder pathFinder = null;
	private int detectRange = 3;
	
	public Enemy(TexturedModel model, Vector2f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, position, rotX, rotY, rotZ, scale);
	}
	
	public Enemy(TexturedModel model, int textureIndex, Vector2f position, float rotX, float rotY, float rotZ, float scale) {
		super(model, textureIndex, position, rotX, rotY, rotZ, scale);
	}
	/**
	 * Determines how the npc moves.
	 * @param tiles the terrain
	 */
	public void move(int[][] tiles){
		if(target != null){
			findPath(tiles);
		}
		direction.x = currentSpeed.x * DisplayManager.getFrameTimeSeconds();
		direction.y = currentSpeed.y * DisplayManager.getFrameTimeSeconds();
		checkCollision(tiles);
	}

	public Vector2f getTarget() {
		return target;
	}

	public void setTarget(Vector2f currentTarget) {
		this.target = currentTarget;
	}
	
	public void setPathFinder(AStarPathFinder pathFinder){
		this.pathFinder = pathFinder;
	}
	
	public ArrayList<Node> getPath(){
		if(pathFinder!=null)
			return pathFinder.pathNodes;
		return null;
	}
	/**
	 * Forces the npc to stop following the player.
	 */
	public void abortTarget(){
		target = null;
		currentSpeed.x = 0;
		currentSpeed.y = 0;
		pathFinder.pathNodes.clear();
	}
	/**
	 * Prepares A* path finder variables and asks it to find a path.
	 * @param terrain
	 */
	private void findPath(int[][] terrain){
		if(pathFinder == null) return;
		pathFinder.setNodes((int)(position.x+box.dx()/2),(int)(position.y+box.dy()/2),(int)target.x,(int)target.y);
		if(pathFinder.search()){
			currentTarget.x = pathFinder.pathNodes.get(0).x;
			currentTarget.y = pathFinder.pathNodes.get(0).y;
			if(position.x - error < currentTarget.x){
				currentSpeed.x = speed;
			}else if(position.x + error > currentTarget.x){
				currentSpeed.x = -speed;
			}else {
				currentSpeed.x = 0;
			}
			if(position.y - error < currentTarget.y){
				currentSpeed.y = speed;
			}else if(position.y + error > currentTarget.y){
				currentSpeed.y = -speed;
			}else {
				currentSpeed.y = 0;
			}
		}
		else{
			currentSpeed.x = 0;
			currentSpeed.y = 0;
		}
	}
	
	public void setSpeed(float x){
		this.speed = x;
	}
	public void setDectectRange(int detectRange){
		this.detectRange = detectRange;
	}
	public int getDetectRange(){
		return detectRange;
	}

}
