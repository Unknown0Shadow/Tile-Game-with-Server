package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.util.vector.Vector2f;
import models.TexturedModel;
import toolbox.Rectangle;
/**
 * A being which exists. Generic class for all entities in the game.<br>
 * <br>
 * Variables:<br>
 * model - TexturedModel, stores the model of the texture<br>
 * position - Vector2f, the position of the entity in the world<br>
 * direction - Vector2f, the amount of movement to be performed. This variable is used to predict the position of the entity before it moves, in order to prevent it from illegal collisions<br>
 * currentSpeed - Vector2f, the speed at which an entity moves in a certain direction<br>
 * rotX, rotY, rotZ - float, rotation angles (unused)<br>
 * scale - float, the size of the entity<br>
 * textureIndex - integer, the index of the texture atlas<br>
 * box - Rectangle, a rectangle used for checking collisions with Rectangle's, such as triggers<br>
 * hasCollision - boolean, determines whether another entity can pass through it (Only used for tiles)<br>
 * lights - List of Entity objects, the collection of light tiles that an entity has around it 
 */
public class Entity implements Serializable{
	
	private TexturedModel model;
	protected Vector2f position, direction = new Vector2f(0,0);
	protected Vector2f currentSpeed = new Vector2f(0,0);
	private float rotX, rotY, rotZ;
	protected float scale;
	private int textureIndex = 0;
	protected Rectangle box;
	private boolean hasCollision = false;
	public List<Entity> lights = new ArrayList<Entity>();
	
	public Entity(TexturedModel model, Vector2f position, float rotX, float rotY, float rotZ,
			float scale) {
		super();
		this.model = model;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		this.box = new Rectangle(position.x-scale/2, position.y-scale/2, scale, scale);
		if (this.getClass() == Enemy.class || this.getClass() == Player.class){
			box.setDimensions(position.x-scale*0.4f, position.x-scale*0.4f, scale*0.8f, scale*0.8f);
		}
	}
	public Entity(TexturedModel model, int textureIndex, Vector2f position, float rotX,
			float rotY, float rotZ, float scale) {
		super();
		this.model = model;
		this.textureIndex = textureIndex;
		this.position = position;
		this.rotX = rotX;
		this.rotY = rotY;
		this.rotZ = rotZ;
		this.scale = scale;
		if(textureIndex > 23) hasCollision = true;
		this.box = new Rectangle(position.x-scale/2, position.y-scale/2, scale, scale);
		if (this.getClass() == Enemy.class || this.getClass() == Player.class){
			box.setDimensions(position.x-scale*0.4f, position.x-scale*0.4f, scale*0.8f, scale*0.8f);
		}
	}
	/**
	 * Checks if the entity will collide with a solid tile if it follows its current movement.
	 * @param terrain
	 */
	protected void checkCollision(int[][] terrain){
		if(terrain[(int)(position.y + 1 + direction.y-scale/2)][(int)(position.x + scale/2)]>23){
			direction.y = 0;
		}
		if(terrain[(int)(position.y + 1 - scale/2)][(int)(position.x + direction.x + scale/2)]>23){
			direction.x = 0;
		}
		move(direction.x, direction.y);
	}
	/**
	 * Offsets the entity relative to its position.
	 * @param dx horizontal offset
	 * @param dy vertical offset
	 */
	public void move(float dx, float dy){
		this.position.x += dx;
		this.position.y += dy;
		this.box.updatePosition(position.x, position.y);
		for(Entity light:lights){
			light.position.x +=dx;
			light.position.y +=dy;
		}
	}
	/**
	 * Determines how the entity moves.
	 * @param terrain
	 */
	public void move(int[][] terrain){
		// extend and implement
	}
	/**
	 * Rotates the entity (never used).
	 * @param dx around x axis
	 * @param dy around y axis
	 * @param dz around z axis
	 */
	public void rotate(float dx, float dy, float dz){
		this.rotX += dx;
		this.rotY += dy;
		this.rotZ += dz;
	}
	public void setTextureIndex(int textureIndex){
		this.textureIndex = textureIndex;
	}
	public float getTextureXOffset(){
		int column = textureIndex%model.getTexture().getAtlasSize();
		return (float)column/(float)model.getTexture().getAtlasSize();
	}
	public float getTextureYOffset(){
		int row = textureIndex/model.getTexture().getAtlasSize();
		return (float)row/(float)model.getTexture().getAtlasSize();
	}
	public TexturedModel getModel() {
		return model;
	}
	public void setModel(TexturedModel model) {
		this.model = model;
	}
	public Vector2f getPosition() {
		return position;
	}
	public void setPosition(Vector2f position) {
		this.position = position;
	}
	public float getRotX() {
		return rotX;
	}
	public void setRotX(float rotX) {
		this.rotX = rotX;
	}
	public float getRotY() {
		return rotY;
	}
	public void setRotY(float rotY) {
		this.rotY = rotY;
	}
	public float getRotZ() {
		return rotZ;
	}
	public void setRotZ(float rotZ) {
		this.rotZ = rotZ;
	}
	public float getScale() {
		return scale;
	}
	public void setScale(float scale) {
		this.scale = scale;
	}
	public Rectangle getBox() {
		return box;
	}
	public void setBox(float x, float y, float width, float height) {
		this.box.setDimensions(x, y, width, height);
	}
	public boolean hasCollision(){
		return this.hasCollision;
	}
	public Vector2f getDirection(){
		return direction;
	}
	public Vector2f getSpeed(){
		return currentSpeed;
	}
	/**
	 * Creates lights around this entity.
	 * @param texturedModel the texture model
	 * @param row The row of the atlas, basically the colour.
	 * @param x position x relative to the entity
	 * @param y position y relative to the entity
	 * @return list of lights
	 */
	public static List<Entity> createLight(TexturedModel texturedModel, int row, float x, float y){
		List<Entity> entlights = new ArrayList<Entity>();
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x-2, y-1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x-2, y+0), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x-2, y+1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x-1, y-2), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x-1, y-1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 4 + 8*row, new Vector2f(x-1, y+0), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x-1, y+1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x-1, y+2), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x+0, y-2), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 4 + 8*row, new Vector2f(x+0, y-1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 6 + 8*row, new Vector2f(x+0, y+0), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 4 + 8*row, new Vector2f(x+0, y+1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x+0, y+2), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x+1, y-2), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x+1, y-1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 4 + 8*row, new Vector2f(x+1, y+0), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x+1, y+1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x+1, y+2), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x+2, y-1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 3 + 8*row, new Vector2f(x+2, y+0), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x+2, y+1), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x+3, y+0), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x-3, y+0), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x+0, y+3), 0, 0, 0, 1));
		entlights.add(new Entity(texturedModel, 2 + 8*row, new Vector2f(x+0, y-3), 0, 0, 0, 1));
		return entlights;
	}
	public int getTextureIndex(){
		return textureIndex;
	}
}
