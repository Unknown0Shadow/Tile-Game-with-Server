package toolbox;

import java.io.Serializable;
/**
 * Used for collision detection. The built-in Rectangle class uses integers, while this program needed floats.<br>
 * <br>
 * Variables:<br>
 * x, y - float, position of bottom left corner<br>
 * dx, dy - float, width and height<br>
 * dy-------
 * ^		|
 * |   o	|
 * |		|
 * x,y---->dx
 * For simpler calculations - corner coordinates are used, however for easier placement and update, the centre coordinates are used.
 */
public class Rectangle implements Serializable{
	private float x;
	private float y;
	private float dx;
	private float dy;
	
	public Rectangle(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.dx = width;
		this.dy = height;
	}
	/**
	 * Changes the position of the rectnagle based of its centre.
	 * @param x position x of the centre
	 * @param y position y of the centre
	 */
	public void updatePosition(float x, float y){
		this.x = x - dx/2; // left
		this.y = y - dy/2; // bottom
	}
	/**
	 * Resets the position, width and height of the rectangle.
	 * @param x left
	 * @param y bottom
	 * @param width
	 * @param height
	 */
	public void setDimensions(float x, float y, float width, float height){
		this.x = x;
		this.y = y;
		this.dx = width;
		this.dy = height;
	}
	/**
	 * Checks if this rectangle intersects with another.
	 * @param rect
	 * @return True if intersection has been determined. False otherwise.
	 */
	public boolean intersects(Rectangle rect){
		if(rect.x() > x + dx || rect.x() + rect.dx() < x){
			return false;
		}
		if(rect.y() > y + dy || rect.y() + rect.dy() < y){
			return false;
		}
		return true;
	}
	// simple names for easier use in the program
	// honestly I'm just too lazy to type .getX() a million times
	public float x(){
		return x;
	}
	public float y(){
		return y;
	}
	public float dx(){
		return dx;
	}
	public float dy(){
		return dy;
	}
	
}
