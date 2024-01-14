package AI;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *  A* uses 3 numbers: G cost, H cost and F cost<br>
 * G cost - distance between start node and current node<br>
 * H cost - distance between current node and end node<br>
 * F cost = G + H - this helps to find the most promising solution<br>
 * A* loops through the nodes, always trying to check the ones with the lowest F first.<br>
 * For nodes with similar F cost, G is the tie breaker<br>
 * <br>
 * variables<br>
 * nodes - two dimensional array containing objects of the class Node. Represents the game map<br>
 * activeNodes - ArrayList which contains the Nodes that are waiting to be checked<br>
 * pathNodes - ArrayList which contains the found solution<br>
 * startNode - the starting Node<br>
 * endNode - the destination Node<br>
 * currentNode - a variable that temporarily holds the value of the node that is being checked<br>
 * foundSolution - boolean which determines whether the destination can be reached<br>
 * max_iter - integer used to cap the maximum iterations of the algorithm<br>
 * iter - integer used to count the iterations up to max_iter<br>
 * WIDTH, HEIGHT - integers that hold the number of rows and columns of the nodes array - used for loops
 */
public class AStarPathFinder implements Serializable{
	public Node[][] nodes;
	public ArrayList<Node> activeNodes = new ArrayList<>();
	public ArrayList<Node> pathNodes = new ArrayList<>();
	public Node startNode, endNode, currentNode;
	public boolean foundSolution = false;
	public int max_iter = 1024, iter = 0;
	public final int WIDTH, HEIGHT;
	public AStarPathFinder(int[][] terrain){
		WIDTH = terrain[0].length;
		HEIGHT = terrain.length;
		initializeNodes(terrain);
	}
	/**
	 * Creates the node matrix using the integer matrix given as argument. If the integer at position y,x is greater than 23, it's considered a solid tile.
	 * @param terrain holds the texture atlas indices
	 */
	public void initializeNodes(int[][]terrain){
		nodes = new Node[HEIGHT][WIDTH];
		for(int y = 0; y < HEIGHT; y++){
			for(int x = 0; x < WIDTH; x++){
				nodes[y][x] = new Node(x,y, terrain[y][x] > 23? true : false);
			}
		}
	}
	/**
	 * Resets "checked" booleans of nodes to false, in order to allow reusage of the algorithm.
	 */
	public void resetNodes(){
		for(int y = 0; y < HEIGHT; y++){
			for(int x = 0; x < WIDTH; x++){
				if(nodes[y][x].checked){
					nodes[y][x].checked = false;
				}
			}
		}
		activeNodes.clear();
		pathNodes.clear();
		foundSolution = false;
		iter = 0;
	}
	/**
	 * Tells the algorithm which is the starting node, the destination node and prepares startNode to be checked.
	 * @param start_x initial position x
	 * @param start_y initial position y
	 * @param end_x second position x
	 * @param end_y second position y
	 */
	public void setNodes(int start_x, int start_y, int end_x, int end_y){
		resetNodes();
		startNode = nodes[start_y][start_x];
		endNode = nodes[end_y][end_x];
		currentNode = startNode;
		activeNodes.add(currentNode);
		calculateCosts();
	}
	/**
	 * sets the G, H and F costs for each Node
	 */
	private void calculateCosts(){
		for(int y=0;y<HEIGHT;y++){
			for(int x=0;x<WIDTH;x++){
				if(nodes[y][x] == startNode || nodes[y][x] == endNode) continue;
				nodes[y][x].G = Math.abs(x-startNode.x) + Math.abs(y-startNode.y);
				nodes[y][x].H = Math.abs(x-endNode.x) + Math.abs(y-endNode.y);
				nodes[y][x].F = nodes[y][x].G + nodes[y][x].H;
			}
		}
	}
	/**
	 * Starts the path finding process. Is called by the npc.
	 * @return foundSolution - true if the algorithm found a valid path, false otherwise
	 */
	public boolean search(){
		int x, y;
		int bestNodeIndex;
		int bestNodeFCost;
		while(foundSolution == false && iter++ < max_iter){
			x = currentNode.x;
			y = currentNode.y;
			currentNode.checked = true;
			activeNodes.remove(currentNode);
			if(currentNode.y - 1 >= 0){
				activateNode(nodes[y-1][x]);
			}
			if(currentNode.x - 1 >= 0){
				activateNode(nodes[y][x-1]);
			}
			if(currentNode.y + 1 < HEIGHT){
				activateNode(nodes[y+1][x]);
			}
			if(currentNode.x - 1 < WIDTH){
				activateNode(nodes[y][x+1]);
			}
			bestNodeIndex = 0;
			bestNodeFCost = 1024;
			for(int i = 0; i < activeNodes.size(); i++){
				if(activeNodes.get(i).F < bestNodeFCost){
					bestNodeIndex = i;
					bestNodeFCost = activeNodes.get(i).F;
				}
				else if(activeNodes.get(i).F == bestNodeFCost){
					if(activeNodes.get(i).G < activeNodes.get(bestNodeIndex).G){
						bestNodeIndex = i;
					}
				}
			}
			if(activeNodes.size() == 0){
				break;
			}
			
			currentNode = activeNodes.get(bestNodeIndex);
			if(currentNode == endNode){
				foundSolution = true;
				trackPath();
			}
		}
		return foundSolution;
	}
	/**
	 * Prepares a node for checking.
	 * @param node
	 */
	public void activateNode(Node node){
		if(node.checked == false && node.solid == false){
			node.checked = true;
			node.parent = currentNode;
			activeNodes.add(node);
		}
	}
	/**
	 * Backtracks from the current node (which practically is the destination node, as this function is called after it found a solution) to the starting node, saving all nodes into a list (pathNodes).
	 */
	public void trackPath(){
		Node current = endNode;
		while(current != startNode){
			pathNodes.add(0, current);
			current = current.parent;
		}
	}
}
