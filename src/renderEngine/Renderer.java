package renderEngine;

import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

import entities.Entity;
import models.RawModel;
import models.TexturedModel;
import shaders.StaticShader;
import toolbox.Maths;
/**
 * Renders the model form the VAO.<br>
 * <br>
 * Variables:<br>
 * FOV - float, field of view angle<br>
 * NEAR_PLANE - float, the distance at which rendering starts<br>
 * FAR_PLANE - float, the distance at which rendering stops<br>
 * projectionMatrix - Matrix4f, projects items in a 3d environment<br>
 * shader - StaticShader, tells the GPU what to render and how
 */
public class Renderer {
	
	private static final float FOV = 90;
	private static final float NEAR_PLANE = 0.01f;
	private static final float FAR_PLANE = 20;
	
	private Matrix4f projectionMatrix;
	private StaticShader shader;

	public Renderer(StaticShader shader){
		this.shader = shader;
		GL11.glEnable(GL11.GL_CULL_FACE);
		GL11.glCullFace(GL11.GL_BACK); // disables rendering for the faces that are not seen by the camera
		createProjectionMatrix();
		shader.start();
		shader.loadProjectionMatrix(projectionMatrix);
		shader.stop();
	}
	/**
	 * Prepares OpenGL to render the game. Clears the screen and sets the colour of the background.
	 */
	public void prepare(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
	}
	/**
	 * Renders entities by the batch. Each entity from a batch has the same textured model.
	 * @param entities list of all entities in the game.
	 */
	public void render(Map<TexturedModel, List<Entity>> entities){
		for(TexturedModel model:entities.keySet()){
			prepareTexturedModel(model);
			List<Entity> batch = entities.get(model);
			for(Entity entity:batch){
				prepareEntity(entity);
				GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_INT, 0); // 2 triangles
			}
			unbindTexturedModel();
		}
	}
	
	/**
	 * Prepares a textured model to be rendered, along with it's raw model.
	 * @param texturedModel
	 */
	private void prepareTexturedModel(TexturedModel texturedModel){
		RawModel model = texturedModel.getRawModel();
		GL30.glBindVertexArray(model.getVaoID());
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		shader.loadAtlasSize(texturedModel.getTexture().getAtlasSize());
		if (texturedModel.isTransparent()){
			GL11.glEnable(GL11.GL_BLEND);
			if(texturedModel.isLight()){
				GL11.glBlendFunc(GL11.GL_DST_COLOR, GL11.GL_SRC_ALPHA);
			}
			else{
				GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			}
		}
		else{
			GL11.glDisable(GL11.GL_BLEND);
		}
		GL13.glActiveTexture(GL13.GL_TEXTURE0);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, texturedModel.getTexture().getID());
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST); // removes grid visual glitch
		GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST); // removes atlas bleeding
	}
	/**
	 * Unbinds the textured model from the VAO.
	 */
	private void unbindTexturedModel(){
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		// GL11.glDisable(GL11.GL_BLEND);
	}
	/**
	 * Prepares an entity to be rendered on the screen.
	 * @param entity
	 */
	private void prepareEntity(Entity entity){
		Matrix4f transformationMatrix = Maths.createTransformationMatrix(
				new Vector3f(entity.getPosition().x, entity.getPosition().y, 0), entity.getRotX(),
				entity.getRotY(), entity.getRotZ(), entity.getScale());
		shader.loadTransformationMatrix(transformationMatrix);
		shader.loadOffset(entity.getTextureXOffset(), entity.getTextureYOffset());
	}
	/**
	 * Initialises the projection matrix.
	 */
	private void createProjectionMatrix(){
		float aspectRatio = (float) Display.getWidth() / (float) Display.getHeight();
		float y_scale = (float) ((1f / Math.tan(Math.toRadians(FOV / 2f))) * aspectRatio);
		float x_scale = y_scale / aspectRatio;
		float frustum_length = FAR_PLANE - NEAR_PLANE;

		projectionMatrix = new Matrix4f();
		projectionMatrix.m00 = x_scale;
		projectionMatrix.m11 = y_scale;
		projectionMatrix.m22 = -((FAR_PLANE + NEAR_PLANE) / frustum_length);
		projectionMatrix.m23 = -1;
		projectionMatrix.m32 = -((2 * NEAR_PLANE * FAR_PLANE) / frustum_length);
		projectionMatrix.m33 = 0;
	}
}
