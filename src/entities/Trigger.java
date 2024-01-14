package entities;

import toolbox.Rectangle;
/**
 * An area which triggers an action if crossed.<br>
 * <br>
 * Variables:<br>
 * box - Rectangle, the rectangle of the trigger<br>
 * action_ID - integer, the id of the action to be performed if the trigger is crossed
 */
public class Trigger {
	private Rectangle box;
	private int action_ID;
	
	public Trigger(Rectangle box){
		this.box = box;
	}
	public Rectangle getBox() {
		return box;
	}
	public void setBox(Rectangle box) {
		this.box = box;
	}
	public void setActionID(int actionID){
		this.action_ID = actionID;
	}
	public int getActionID(){
		return action_ID;
	}	
}
