package assignment_mazeworld;

import java.util.ArrayList;
import java.util.Arrays;


public class MultiRobotsMazeProblem extends InformedSearchProblem {
	// 5 actions NOTMOVE:{0,0}
	private static int actions[][] = {{0,0}, Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	
	private int numofRobots;	//number of Robots
	
	private int[][] startState;	//initial state
	private int[][] goalState;	//goal state
	
	private Maze maze;
	
	public MultiRobotsMazeProblem(Maze m, int[][] ss, int[][] gs, int num) {
		numofRobots = num;

		startNode = new MultiRobotsMazeNode(ss, 0, null, 0);
/*		startState = new int[numofRobots][2];
		goalState = new int[numofRobots][2];
		for (int i = 0; i < numofRobots; i++) {
			System.arraycopy(ss[i], 0, startState[i], 0, 2);
			System.arraycopy(gs[i], 0, goalState[i], 0, 2);
		}*/
		startState = ss;
		goalState = gs;
		
		maze = m;		
	}
	

	
	// node class used by searches.  Searches themselves are implemented
	//  in SearchProblem and InformedSearchProblem.
	public class MultiRobotsMazeNode implements SearchNode {

		// cooridnates of robots in the maze
		protected int[][] state; 
		
		// how far the current node is from the start.  =depth
		// in multirobots nodes with same state dif cost are dif, due to NOTMOVE action
		private double cost;
		
		// for backchain  
		private SearchNode parent;
		
		// who's turn [0:numofRobots-1]
		private int currentTurn;

		public MultiRobotsMazeNode(int[][] s, double c, SearchNode pa, int turn) {
			state = new int[numofRobots][2];
			for (int i = 0; i < numofRobots; i++) 
				System.arraycopy(s[i], 0, state[i], 0, 2);

			cost = c;			
			parent = pa;
			currentTurn = turn;
		}
		
		public int[] getaState(int num) {
			return state[num];
		}
		

		public ArrayList<SearchNode> getSuccessors() {

			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();
			int nextTurn = (currentTurn + 1) % numofRobots;

			for (int[] action: actions) {
				int xNew = state[currentTurn][0] + action[0];
				int yNew = state[currentTurn][1] + action[1]; 
				
				//System.out.println("testing successor " + xNew + " " + yNew);
				
				//if not knock a wall or edge and not crash another robot
				if(maze.isLegal(xNew, yNew) && isSafeState(xNew, yNew, currentTurn)) {
					//System.out.println("legal successor found " + " " + xNew + " " + yNew);
					SearchNode succ = new MultiRobotsMazeNode(this.state, getCost() + 1.0, this, nextTurn);
					((MultiRobotsMazeNode)succ).state[currentTurn][0] = xNew;
					((MultiRobotsMazeNode)succ).state[currentTurn][1] = yNew;
					successors.add(succ);
				}
				
			}
			return successors;

		}
		
		// if crash another robot return false; not return true
		private boolean isSafeState(int x, int y, int turn) {
			for (int i = 0; i < numofRobots; i++)
				if (i != turn && Arrays.equals(state[i], new int[]{x, y}))
						return false;
			return true;
		}
		
		// every robot achieves its goal
		@Override
		public boolean goalTest() {
			for (int i = 0; i < numofRobots; i++)
				if (!Arrays.equals(state[i], goalState[i]))
					return false;
			return true;
		}


		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			for (int i = 0; i < numofRobots; i++)
				if (!Arrays.equals(state[i], ((MultiRobotsMazeNode) other).state[i]))
					return false;
			if (currentTurn != ((MultiRobotsMazeNode) other).currentTurn)
				return false;
			return true;
		}

		@Override
		public int hashCode() {
			int code = 0;

			for (int i = 0; i < numofRobots; i++)
				code = code * 10000 + (state[i][0] * 100 + state[i][1]);
			
			// because of action:NotMove, nodes may have same state but dif turn, nodes still dif. 
			code = code * 100 + currentTurn;
//			System.out.println(code);
			return code; 
//			return 1; 

		}

		@Override
		public String toString() {
			String str = new String("Maze state ");
			for (int i = 0; i < numofRobots; i++)
				str += state[i][0] + "," + state[i][1] + " ";
			str += "depth " + getCost() + " priority " + priority() + " ";
			return str;
		}

		@Override
		public double getCost() {
			return cost;
		}
		

		@Override
		public double heuristic() {
			//sum of manhattan distances of robots
			double d = 0;
			for (int i = 0; i < numofRobots; i++) {
				// manhattan distance metric for simple maze with one agent:
				double dx = goalState[i][0] - state[i][0];
				double dy = goalState[i][1] - state[i][1];
				d += Math.abs(dx) + Math.abs(dy);
			}
			return d;
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}
		
		@Override
		public double priority() {
			return heuristic() + getCost();
		}
		
		@Override
		public SearchNode getParent() {
			return parent;
		}

	}
}
