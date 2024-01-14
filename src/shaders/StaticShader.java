package shaders;

import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

import entities.Camera;
import toolbox.Maths;
/**
 * Creates the static models.<br>
 * <br>
 * Variables:<br>
 * VERTEX_FILE, FRAGMENT_FILE - the names of the shader files<br>
 * location_transformationMatrix - location of the transformationMatrix uniform variable<br>
 * location_projectionMatrix - location of the projectionMatrix uniform variable<br>
 * location_viewMatrix - location of the viewMatrix uniform variable<br>
 * location_atlasSize - location of the atlasSize uniform variable<br>
 * location_offset - location of the offset uniform variable
 */
public class StaticShader extends ShaderProgram{

	private static final String VERTEX_FILE = "src/shaders/vertexShader.txt";
	private static final String FRAGMENT_FILE  = "src/shaders/fragmentShader.txt";
	
	private int location_transformationMatrix;
	private int location_projectionMatrix;
	private int location_viewMatrix;
	private int location_atlasSize;
	private int location_offset;
	
	public StaticShader() {
		super(VERTEX_FILE, FRAGMENT_FILE);
	}
	
	/**
	 * Binds attributes n of the VAO to the variable name.
	 */
	protected void bindAttributes(){
		super.bindAttribute(0, "position");
		super.bindAttribute(1, "textureCoords");
	}
	/**
	 * Gets all locations of the uniform variables.
	 */
	@Override
	protected void getAllUniformLocations() {
		location_transformationMatrix = super.getUniformLocation("transformationMatrix");
		location_projectionMatrix = super.getUniformLocation("projectionMatrix");
		location_viewMatrix = super.getUniformLocation("viewMatrix");
		location_atlasSize = super.getUniformLocation("atlasSize");
		location_offset = super.getUniformLocation("offset");
	}
	/**
	 * Loads the atlas size to the uniform variable.
	 * @param atlasSize the size of the atlas.
	 */
	public void loadAtlasSize(int atlasSize){
		super.loadFloat(location_atlasSize, atlasSize);
	}
	/**
	 * Loads the offset to the uniform variable.
	 * @param x horizontal offset
	 * @param y vertical offset
	 */
	public void loadOffset(float x, float y){
		super.load2DVector(location_offset, new Vector2f(x,y));
	}
	/**
	 * Loads the transformation matrix to the uniform variable.
	 * @param matrix
	 */
	public void loadTransformationMatrix(Matrix4f matrix){
		super.loadMatrix(location_transformationMatrix, matrix);
	}
	/**
	 * Loads the projection matrix to the uniform variable.
	 * @param projection
	 */
	public void loadProjectionMatrix(Matrix4f projection){
		super.loadMatrix(location_projectionMatrix, projection);
	}
	/**
	 * Loads the view matrix to the uniform variable.
	 * @param camera
	 */
	public void loadViewMatrix(Camera camera){
		Matrix4f viewMatrix = Maths.createViewMatrix(camera);
		super.loadMatrix(location_viewMatrix, viewMatrix);
	}

}
