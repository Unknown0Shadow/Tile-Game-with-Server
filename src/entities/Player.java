package entities;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector2f;

import models.TexturedModel;
import renderEngine.DisplayManager;
/**
 * Player character.<br>
 * <br>
 * Variables:<br>
 * speed - float, the multiplier for movement<br>
 * camera - Camera, player's point of view<br>
 * target - boolean, if true, forces npc's to follow the player, if false, nothing happens<br>
 * noTarget - boolean, if true, forces npc's to stop following the player, if false, nothing happens
 */
public class Player extends Entity{
	
	private static final float speed = 8f; //4f
	private Camera camera;
	public boolean target, noTarget;

	public Player(TexturedModel model, Vector2f position, float rotX, float rotY, float rotZ,
			float scale, Camera camera) {
		super(model, position, rotX, rotY, rotZ, scale);
		this.camera = camera;
	}
	/**
	 * Determines how the player moves.
	 * @param tiles the terrain
	 */
	public void move(int[][] tiles){
		checkInputs();
		direction.x = currentSpeed.x * DisplayManager.getFrameTimeSeconds();
		direction.y = currentSpeed.y * DisplayManager.getFrameTimeSeconds();
		checkCollision(tiles);
		updateCamera();
	}
	/**
	 * Updates the view to be focused on the player.
	 */
	private void updateCamera(){
		camera.setPosition(getPosition().x, getPosition().y);
	}
	/**
	 * Reads player inputs.
	 * @param tiles the terrain
	 */
	private void checkInputs(){
		if(Keyboard.isKeyDown(Keyboard.KEY_UP)){
			this.currentSpeed.y = speed;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_DOWN)){
			this.currentSpeed.y = -speed;
		}else {
			this.currentSpeed.y = 0;
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_RIGHT)){
			this.currentSpeed.x = speed;
		}else if(Keyboard.isKeyDown(Keyboard.KEY_LEFT)){
			this.currentSpeed.x = -speed;
		}else {
			this.currentSpeed.x = 0;
		}
		if (Keyboard.next()){
			if (Keyboard.getEventKeyState()){
				if(Keyboard.getEventKey() == Keyboard.KEY_P){
					System.out.printf("Player Position [%.2f, %.2f]\n",
							this.getPosition().x, this.getPosition().y);
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_C){
					target = true;
					System.out.println("Engage targeting!");
				}
				if(Keyboard.getEventKey() == Keyboard.KEY_X){
					noTarget = true;
					System.out.println("Remove target!");
				}
			}
		}
	}
	public Camera getCamera(){
		return camera;
	}
}
