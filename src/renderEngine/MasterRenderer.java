package renderEngine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import entities.Camera;
import entities.Entity;
import models.TexturedModel;
import shaders.StaticShader;
/**
 * Handles all the rendering code.<br>
 * <br>
 * Variables:<br>
 * shader - StaticShader, tells the GPU what to render and how<br>
 * renderer - Renderer, does the actual rendering<br>
 * map_entities - HashMap of TexturedModel and List of Entity objects, maps lists of entities with the textured models they use
 */
public class MasterRenderer {
	private StaticShader shader = new StaticShader();
	private Renderer renderer = new Renderer(shader);
	
	private Map<TexturedModel, List<Entity>> map_entities = new HashMap<TexturedModel, List<Entity>>();
	/**
	 * Renders all the entities. Groups entities in batches that use the same textured models, as to bind the model only once per all entities that use it.
	 * @param camera point of view
	 * @param entities list of entities
	 */
	public void render(Camera camera, List<List<Entity>> entities){
		renderer.prepare();
		shader.start();
		shader.loadViewMatrix(camera);
		for(List<Entity> ents : entities){
			for(Entity entity : ents){
				processEntity(entity);
			}
			renderer.render(map_entities);
			map_entities.clear();
		}
		shader.stop();
	}
	
	/**
	 * Puts the entity in the list which contains its textured model.
	 * @param entity
	 */
	public void processEntity(Entity entity){
		TexturedModel texturedModel = entity.getModel();
		List<Entity> batch = map_entities.get(texturedModel);
		if(batch!=null){
			batch.add(entity);
		}else{
			List<Entity> newBatch = new ArrayList<Entity>();
			newBatch.add(entity);
			map_entities.put(texturedModel, newBatch);
		}
	}
	/**
	 * Cleans the shader for memory management.
	 */
	public void cleanUp(){
		shader.cleanUp();
	}
}
