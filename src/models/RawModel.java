package models;

import java.io.Serializable;

/**
 * Represents the model stored in memory.<br>
 * 
 * Variables:<br>
 * vaoID - integer, the id of the VAO
 */
public class RawModel implements Serializable{
	private int vaoID;
	
	public RawModel(int vaoID){
		this.vaoID = vaoID;
	}
	
	public int getVaoID(){
		return vaoID;
	}

}
