package textures;

import java.io.Serializable;

/**
 * A model that represents a texture.<br>
 * <br>
 * Variables:<br>
 * textureID - integer, used to refer to this object<br>
 * atlasSize - integer, tells the program the size of the atlas. 1 = 1x1, 2 = 2x2, 6 = 6x6 etc...
 */
public class ModelTexture implements Serializable{

	private int textureID;
	private int atlasSize = 1;
	
	public ModelTexture(int id){
		this.textureID = id;
	}
	
	public int getID(){
		return this.textureID;
	}

	public int getAtlasSize() {
		return atlasSize;
	}

	public void setAtlasSize(int atlasSize) {
		this.atlasSize = atlasSize;
	}
	
}
