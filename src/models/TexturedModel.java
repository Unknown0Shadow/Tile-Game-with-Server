package models;

import java.io.Serializable;

import textures.ModelTexture;
/**
 * Represents a textured model.<br>
 * <br>
 * Variables:<br>
 * rawModel - RawModel, the model of the object<br>
 * texture - ModelTexture, the texture of the object<br>
 * isTransparent - boolean, determines whether the texture has an alpha channel. True => has transparency<br>
 * isLight - boolean, determines whether this texture is used as a light. Lights are rendered differently. True => is a light
 */
public class TexturedModel implements Serializable{
	private RawModel rawModel;
	private ModelTexture texture;
	private boolean isTransparent = false;
	private boolean isLight = false;
	
	public TexturedModel(RawModel model, ModelTexture texture){
		this.rawModel = model;
		this.texture = texture;
	}

	public RawModel getRawModel() {
		return rawModel;
	}

	public ModelTexture getTexture() {
		return texture;
	}

	public boolean isTransparent() {
		return isTransparent;
	}

	public void setTransparent(boolean isTransparent) {
		this.isTransparent = isTransparent;
	}
	
	public void setIsLight(boolean isLight){
		this.isLight = isLight;
	}
	public boolean isLight(){
		return isLight;
	}
	
}
