package shaders;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;

/**
 * A generic shader program which tells the GPU how the objects should be rendered, such as shape, colour, position, lighting, etc...<br>
 * Shaders use GLSL (OpenGL Shading Language), a java-like language, to communicate with the GPU.<br>
 * Vertex Shader - executes once per vertex, uses the data stored in VAO, determines position on screen, outputs to Fragment Shader.<br>
 * Fragment Shader - executes once per pixel, outputs the pixel colour (RGB), uses u,v texture coordinates (start from top left corner)<br>
 * Uniform Variables are variables in the shader code that can be changed in the java code<br>
 * Transformation Matrix transforms the coordinates of the object to imitate a certain position on the screen ( such as being rotated, moved, scaled)<br>
 * Projection Matrix projects items in a 3D environment<br>
 * View Matrix acts as a camera, moves everything opposite to where the camera is heading.
 * <br>
 * Variables:<br>
 * programID, vertexShaderID, fragmentShaderID - integer, id's of the program, vertexShader and fragmentShader respectively.
 */
public abstract class ShaderProgram {
	private int programID;
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private static FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);
	
	/**
	 * Constructor. Loads the shader source codes, attaches them to the program, and links everything together.
	 * @param vertexFile name of the Vertex Shader file
	 * @param fragmentFile name of the Fragment Shader file
	 */
	public ShaderProgram(String vertexFile, String fragmentFile){
		vertexShaderID = loadShader(vertexFile, GL20.GL_VERTEX_SHADER);
		fragmentShaderID = loadShader(fragmentFile, GL20.GL_FRAGMENT_SHADER);
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		bindAttributes();
		GL20.glLinkProgram(programID);
		GL20.glValidateProgram(programID);
		getAllUniformLocations();
	}
	/**
	 * Gets all locations of the uniform variables.
	 */
	protected abstract void getAllUniformLocations();
	/**
	 * Gets the location of the uniform variable in the shader code.
	 * @param uniformName the name of the variable in the shader code
	 * @return integer location
	 */
	protected int getUniformLocation(String uniformName){
		return GL20.glGetUniformLocation(programID, uniformName);
	}
	/**
	 * Starts the program.
	 */
	public void start(){
		GL20.glUseProgram(programID);
	}
	/**
	 * Stops the program.
	 */
	public void stop(){
		GL20.glUseProgram(0);
	}
	/**
	 * Deletes the shaders and the program for memory management.
	 */
	public void cleanUp(){
		stop();
		GL20.glDetachShader(programID, vertexShaderID);
		GL20.glDetachShader(programID, fragmentShaderID);
		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		GL20.glDeleteProgram(programID);
	}
	/**
	 * Links the inputs to the shader programs to the attributes of the VAO
	 */
	protected abstract void bindAttributes();
	/**
	 * Binds an attribute from the VAO to a variable in the shader code.
	 * @param attribute the number of the attribute list in the VAO
	 * @param variableName the name of the variable in the shader code
	 */
	protected void bindAttribute(int attribute, String variableName){
		GL20.glBindAttribLocation(programID, attribute, variableName);
	}
	/**
	 * Loads float to the uniform location.
	 * @param location integer
	 * @param value float
	 */
	protected void loadFloat(int location, float value){
		GL20.glUniform1f(location, value);
	}
	/**
	 * Loads Vector2f to the uniform location.
	 * @param location integer
	 * @param vector Vector2f
	 */
	protected void load2DVector(int location, Vector2f vector){
		GL20.glUniform2f(location, vector.x, vector.y);
	}
	/**
	 * Loads boolean to the uniform location.
	 * @param location integer
	 * @param value boolean
	 */
	protected void loadBoolean(int location, boolean value){
		float toLoad = 0;
		if(value){
			toLoad = 1;
		}
		GL20.glUniform1f(location, toLoad);
	}
	/**
	 * Loads Matrix4f to the uniform location.
	 * @param location integer
	 * @param matrix Matrix4f
	 */
	protected void loadMatrix(int location, Matrix4f matrix){
		matrix.store(matrixBuffer);
		matrixBuffer.flip();
		GL20.glUniformMatrix4(location, false, matrixBuffer);
	}
	/**
	 * Loads the shader source code files
	 * @param file filename
	 * @param type integer, determines if it's a vertex or a fragment shader
	 * @return ID of the created shader
	 */
	private static int loadShader(String file, int type){
		StringBuilder shaderSource = new StringBuilder();
		try{
			BufferedReader reader = new BufferedReader(new FileReader(file));
			String line;
			while((line = reader.readLine())!=null){
				shaderSource.append(line).append("//\n");
			}
			reader.close();
		}catch(IOException e){
			e.printStackTrace();
			System.exit(-1);
		}
		int shaderID = GL20.glCreateShader(type);
		GL20.glShaderSource(shaderID, shaderSource);
		GL20.glCompileShader(shaderID);
		if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS )== GL11.GL_FALSE){
			System.out.println(GL20.glGetShaderInfoLog(shaderID, 500));
			System.err.println("Could not compile shader!");
			System.exit(-1);
		}
		return shaderID;
	}
}
