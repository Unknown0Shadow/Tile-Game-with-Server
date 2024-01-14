package renderEngine;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

import models.RawModel;

/**
 * Loads models into memory, by storing data about the models such as position and colour.<br>
 * <br>
 * Variables:<br>
 * vaos - list of vao ID's<br>
 * vbos - list of vbo ID's<br>
 * textures - list of texture ID's<br>
 * vertices - the relative positions of vertices of an object<br>
 * indices - the order in which the vertices are being rendered, creates 2 triangles<br>
 * textureCoords - the coordinates of the texture on the object
 */
public class Loader {
	
	private List<Integer> vaos = new ArrayList<Integer>();
	private List<Integer> vbos = new ArrayList<Integer>();
	private List<Integer> textures = new ArrayList<Integer>();
	
	private final float [] vertices = {
			-0.5f, 0.5f,
			-0.5f, -0.5f,
			0.5f, -0.5f,
			0.5f, 0.5f
	};
	
	private final int[] indices = {
			0,1,3,
			3,1,2
	};
	
	private final float[] textureCoords = {
			0,0,
			0,1,
			1,1,
			1,0
	};
	
	/**
	 * Loads the data into a VAO and returns information about the VAO as a RawModel object.
	 * @return RawModel object.
	 */
	public RawModel loadToVAO(){
		int vaoID = createVAO();
		bindIndicesBuffer(indices);
		storeDataInAttributeList(0, vertices);
		storeDataInAttributeList(1, textureCoords);
		unbindVAO();
		return new RawModel(vaoID);
	}
	/**
	 * Loads up a 2^n by 2^n png texture into OpenGL. Uses slick-util library.
	 * @param fileName
	 * @return texture ID
	 */
	public int loadTexture(String fileName){
		Texture texture = null;
		try {
			texture = TextureLoader.getTexture("PNG",
					new FileInputStream("res/textures/"+fileName+".png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		int textureID = texture.getTextureID();
		textures.add(textureID);
		return textureID;
	}
	/**
	 * Deletes all VAOS and VBOS for memory management.
	 */
	public void cleanUp(){
		for(int vao:vaos){
			GL30.glDeleteVertexArrays(vao);
		}
		for(int vbo:vbos){
			GL15.glDeleteBuffers(vbo);
		}
		for(int texture:textures){
			GL11.glDeleteTextures(texture);
		}
	}
	/**
	 * Loads and binds the indices to a VAO.
	 * @param indices
	 */
	private void bindIndicesBuffer(int[] indices){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
		IntBuffer buffer = storeDataInIntBuffer(indices);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
	}
	/**
	 * Creates an empty VAO and binds it.
	 * @return the ID of the VAO
	 */
	private int createVAO(){
		int vaoID = GL30.glGenVertexArrays();
		vaos.add(vaoID);
		GL30.glBindVertexArray(vaoID);
		return vaoID;
	}
	/**
	 * Unbinds the VAO. Called after finished using the VAO.
	 */
	private void unbindVAO(){
		GL30.glBindVertexArray(0);
	}
	/**
	 * Stores the data into an attribute list of the VAO.
	 * @param attributeNumber
	 * @param data
	 */
	private void storeDataInAttributeList(int attributeNumber, float[] data){
		int vboID = GL15.glGenBuffers();
		vbos.add(vboID);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
		FloatBuffer buffer = storeDataInFloatBuffer(data);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(attributeNumber, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}
	/**
	 * Converts an integer array into IntBuffer, and prepares it to be read from.
	 * @param data
	 * @return IntBuffer data.
	 */
	private IntBuffer storeDataInIntBuffer(int[] data){
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
	/**
	 * Converts a float array data into a FloatBuffer and prepares it to be read from.
	 * @param data
	 * @return FloatBuffer data
	 */
	private FloatBuffer storeDataInFloatBuffer(float[] data){
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
