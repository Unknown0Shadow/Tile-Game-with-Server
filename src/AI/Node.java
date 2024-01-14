package AI;

import java.io.Serializable;
/**
 * A simple data container for the A* algorithm.<br>
 * <br>
 * Variables:<br>
 * parent - Node, points towards the previous Node, used for back tracking<br>
 * x, y - integer, the position in the matrix of nodes<br>
 * G cost - integer, distance from the starting node<br>
 * H cost - integer, distance from the destination node<br>
 * F cost - integer, sum of G and H costs<br>
 * solid - boolean, determines whether or not a path can be created through this node. True => the path cannot go through this node<br>
 * checked - boolean, prevents this block from being checked back and forth with each iteration, once it has been checked once
 */
public class Node implements Serializable{
	public Node parent;
	public int x, y;
	public int G, H, F;
	public boolean solid;
	public boolean checked;
	
	public Node(int x, int y, boolean solid){
		this.x = x; this.y = y; this.solid = solid;
	}
}