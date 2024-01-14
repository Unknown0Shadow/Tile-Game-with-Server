package engineTester;

import java.util.ArrayList;
import java.util.List;

public class AStarDemo {

	static int size = 20;
	static List<Node> openNodes = new ArrayList<Node>();
	static int[][]grid;
	static Node [][] nodes = new Node[size][size];
	static Node startNode;
	static Node endNode;
	
	
	public static void main(String[] args) {
		grid = generateGrid();
		generateNodes();
		calculateCosts();
		displayGrid(grid);
		check();
		displayGrid(grid);

	}
	
	private static class Node{
		public int x, y;
		public int state;	// 0 open 1 block 2 start 3 end 4 checked
		public int HCost, GCost, FCost;
		public Node parent;
	
		public Node(int x, int y, int state){
			this.x = x;	this.y = y; this.state = state;
		}
	}
	
	public static int[][] generateGrid(){
		int[][] grid = {
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
				{1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,2,0,1,1,1,1,1,1,0,1,1,1,1,1,0,1,0,1},
				{1,1,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1,0,1},
				{1,0,0,0,1,0,1,0,0,1,1,1,1,1,1,1,1,1,0,1},
				{1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,1,0,1,0,0,0,0,0,0,0,0,0,0,0,0,1},
				{1,0,0,0,1,0,1,0,0,1,0,0,0,0,0,0,0,0,0,1},
				{1,0,1,1,1,0,1,0,0,0,0,0,0,1,1,1,0,0,0,1},
				{1,0,0,1,0,0,1,0,0,1,0,0,0,1,1,1,0,0,0,1},
				{1,0,1,1,0,0,1,0,0,0,0,0,1,1,1,1,1,1,1,1},
				{1,0,0,0,0,0,1,0,0,1,0,0,1,0,0,0,0,0,0,1},
				{1,1,1,1,1,1,1,0,0,0,0,0,1,0,0,0,0,0,0,1},
				{1,0,0,0,0,0,0,0,0,1,0,0,1,0,1,1,1,1,0,1},
				{1,0,1,0,1,0,0,0,0,0,0,0,1,0,1,0,0,0,0,1},
				{1,0,1,0,1,0,1,1,0,1,0,0,1,0,1,0,3,0,0,1},
				{1,0,1,0,1,0,1,1,0,0,0,0,0,0,1,0,0,0,0,1},
				{1,0,0,0,1,0,0,0,0,1,0,0,1,0,1,0,0,0,0,1},
				{1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1},
		};
		return grid;
	}
	
	public static void check(){
		boolean solution = false;
		int max_it = 1024, iter = 0;
		Node currentNode = startNode;
		int bestNodeIndex;
		int bestFCost;
		////////////////////
		while(!solution && iter++ < max_it){
			bestNodeIndex = 0;
			bestFCost = 1024;
			if(currentNode != startNode && currentNode != endNode)
				currentNode.state = 4;
			openNodes.remove(currentNode);
			if(currentNode.y-1 >= 0){
				openNode(nodes[currentNode.y-1][currentNode.x], currentNode);
			}
			if(currentNode.x-1 >= 0){
				openNode(nodes[currentNode.y][currentNode.x-1], currentNode);
			}
			if(currentNode.y+1 < size){
				openNode(nodes[currentNode.y+1][currentNode.x], currentNode);
			}
			if(currentNode.x+1 < size){
				openNode(nodes[currentNode.y][currentNode.x+1], currentNode);
			}
			if(openNodes.size()==0){
				System.out.printf("No solution was found in %d iterations\n", iter);
				break;
			}
			for(int i = 0; i < openNodes.size(); i++){
				if(openNodes.get(i).FCost < bestFCost){
					bestNodeIndex = i;
					bestFCost = openNodes.get(i).FCost;
				}
				else if(openNodes.get(i).FCost == bestFCost){
					if(openNodes.get(i).GCost < openNodes.get(bestNodeIndex).GCost){
						bestNodeIndex = i;
					}
				}
			}
			
			currentNode = openNodes.get(bestNodeIndex);
			if(currentNode == endNode){
				solution = true;
				trackPath();
			}
		}
	}
	
	public static void openNode(Node node, Node currentNode){
		if(node.state != 4 && node.state != 1){
			node.state = 4;
			node.parent = currentNode;
			openNodes.add(node);
		}
	}
	
	public static void trackPath(){
		Node currentNode = endNode;
		while(currentNode != startNode){
			currentNode = currentNode.parent;
			if(currentNode != startNode){
				grid[currentNode.y][currentNode.x] = 4;
			}
		}
	}
	
	public static void calculateCosts(){
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				if(nodes[i][j] == startNode || nodes[i][j] == endNode) continue;
				nodes[i][j].GCost = calculateDistance(nodes[i][j].x,
						nodes[i][j].y, startNode.x, startNode.y);
				nodes[i][j].HCost = calculateDistance(nodes[i][j].x,
						nodes[i][j].y, endNode.x, endNode.y);
				nodes[i][j].FCost = nodes[i][j].GCost + nodes[i][j].HCost;
			}
		}
	}
	
	public static int calculateDistance(int x1, int y1, int x2, int y2){
		return Math.abs(x2-x1) + Math.abs(y2-y1);	// distance in square moves
	}
	
	public static void generateNodes(){
		for(int y = 0; y < size; y++){
			for(int x = 0; x < size; x++){
				if(grid[y][x]==2){
					startNode = new Node(x,y, grid[y][x]);
					nodes[y][x] = startNode;
				}
				else if(grid[y][x]==3){
					endNode = new Node(x,y, grid[y][x]);
					nodes[y][x] = endNode;
				}
				else{
					nodes[y][x] = new Node(x,y,grid[y][x]);
				}
			}
		}
	}
	
	public static void displayGrid(int[][]grid){
		char c;
		for(int i = 0; i < size; i++){
			for(int j = 0; j < size; j++){
				c = (char)(grid[i][j]+48);
				if(c == '0') c = ' ';
				else if(c == '1') c = '#';
				else if(c == '2') c = 'A';
				else if(c == '3') c = 'B';
				else if(c == '4') c = 'o';
				System.out.printf("%c,", c);
			}
			System.out.println();
		}
	}

}
