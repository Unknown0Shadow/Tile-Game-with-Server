package entities;

import java.io.Serializable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.util.vector.Vector3f;
/**
 * The point of view in the game.<br>
 * <br>
 * Variables:<br>
 * position - Vector3f, camera position in the game<br>
 * step - float, the multiplier for camera movement
 */
public class Camera implements Serializable{
	
	private Vector3f position = new Vector3f(0,0,10);
	private float step;
	
	public Camera(float step){
		this.step = step;
	}
	/**
	 * Checks keyboard inputs and changes the position of the camera.<br>
	 * Zooms in and out.
	 */
	public void move(){
		if(Keyboard.isKeyDown(Keyboard.KEY_EQUALS)){
			if(position.z > 0.1+step*2){
				position.z -= step*2;
			}
		}
		if(Keyboard.isKeyDown(Keyboard.KEY_MINUS)){
			if(position.z < 20-step*2){
				position.z += step*2;
			}
		}
	}
	
	public void setPosition(float x, float y){
		this.position.x = x;
		this.position.y = y;
	}
	
	public Vector3f getPosition() {
		return position;
	}

}
