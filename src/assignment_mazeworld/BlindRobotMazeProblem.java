package assignment_mazeworld;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import assignment_mazeworld.BlindRobotMazeProblem.Location;


public class BlindRobotMazeProblem extends InformedSearchProblem {

	private static int actions[][] = {Maze.NORTH, Maze.EAST, Maze.SOUTH, Maze.WEST}; 
	
	private int xGoal, yGoal;

	private Maze maze;
	
	public BlindRobotMazeProblem(Maze m, int gx, int gy) {
//		System.out.println("Blind Robot begin!");
		maze = m;		

		startNode = new BlindRobotMazeNode(0, null);
//		System.out.println("Blind Robot begin!" + maze.width + maze.height);

		for (int i = 0; i < maze.width; i++)
			for (int j = 0; j < maze.height; j++) {
//				System.out.println(i + " " + j);
				if (maze.isLegal(i, j))
					((BlindRobotMazeNode)startNode).addLocation(i, j, null);
			}
//		System.out.println("Blind Robot begin!");

		xGoal = gx;
		yGoal = gy;
		
	}
	
	public List<List<Location>> findPaths(List<SearchNode> astarPath) {
//		System.out.println("findPaths begin" + astarPath.size());
		// get all trying paths
		List<List<Location>> paths = new LinkedList<List<Location>>();
		
		// reverse searching astarPath
		for (int i = astarPath.size(); i > 0; i--) {
			// current search node
			SearchNode node = astarPath.get(i-1);

			// all posible locations in this node
			ArrayList<Location> locs = ((BlindRobotMazeNode)node).state;
			
			// delete locations in the paths which we have found
			for (List<Location> path : paths) {
				Location loc = path.get(0);	//locations in next state
				loc = loc.getPrev();		//locations in this state
				((LinkedList)path).push(loc);				//add into path
				locs.remove(loc);			//delete from locations in this state
			}
			
			// locations left which will disappear in next state
			for (Location loc: locs) {
				LinkedList<Location> newPath = new LinkedList<Location>();
//				System.out.println(loc);
				newPath.add(loc);
				paths.add(newPath);
			}
//		SearchNode node = path.get(path.size() -1);

/*			for (Location loc: ((BlindRobotMazeNode)node).state) {
				Location locc = loc;
				System.out.println(loc);

				LinkedList<Location> p = new LinkedList<Location>();
				while (locc.getPrev() != null) {
					p.push(locc);
					locc = locc.getPrev();
					System.out.println(locc);
				}
				paths.add(p);

			}*/
		}
		for (Location loc: paths.get(1))
			System.out.println(loc);
		return paths;
	}
	

	
	// node class used by searches.  Searches themselves are implemented
	//  in SearchProblem.
	public class BlindRobotMazeNode implements SearchNode {

		// location of the agents in the maze  record state or flag
		// state store all locations like maze
		//  	if location(i,j) is not a possible location, state[j][i] = 0
		//		if location(i,j) is possible, state[j][i] = parent.j*width+parent.i
		//		if state is root, state.parent = null
		// using hashmap to store possible location
		protected ArrayList<Location> state;  
		
		// how far the current node is from the start.  Not strictly required
		//  for uninformed search, but useful information for debugging, 
		//  and for comparing paths
		private double cost;
		
		// for backchain   ---------------modify by M.K.
		private SearchNode parent;
		
		// for animation path 
		// which action lead from parent
		private int[] action;

		public BlindRobotMazeNode(double c, SearchNode pa) {
 
			state = new ArrayList<Location>();
			
			cost = c;
			
			parent = pa;
			
			action = null;
		}
		
		public void delLocation(Location loc) {
			state.remove(loc);
/*			for (Location loc: state) {
				if (loc.getX() == x && loc.getY() == y) {
					state.remove(loc); 
					return true;
				}
			}
			return false;*/
		}
		
		public boolean addLocation(int x, int y, Location prev) {
//			System.out.println("add loc");
			for (Location loc: state) {
				if (loc.getX() == x && loc.getY() == y) {
					return false;
				}
			}
			Location loc = new Location(x, y, prev);
			return state.add(loc);						//!!!!!repeat add??
		}
		

		public ArrayList<SearchNode> getSuccessors() {

			ArrayList<SearchNode> successors = new ArrayList<SearchNode>();

			// according to currentLocation, robot knows the blocked directions
			// 	if blocked, new successor not move, only keep loc will be blocked too
			
			for (int[] action: actions) {
/*				int curXNew = curLoc[0] + action[0];
				int curYNew = curLoc[1] + action[1];
				
				SearchNode succ;
				// keep tracking the real location of robot
				if (maze.isLegal(curXNew, curYNew))
					succ = new BlindRobotMazeNode(curXNew, curYNew, getCost() + 1.0, this);
				else	//not move
					succ = new BlindRobotMazeNode(curLoc[0], curLoc[1], getCost() + 1.0, this);
					
				for (Location loc: state) {
					int xNew = loc.getX() + action[0];
					int yNew = loc.getY() + action[1]; 
			
					//System.out.println("testing successor " + xNew + " " + yNew);
				
					//can move, successor includes all locations moved
					if (maze.isLegal(curXNew, curYNew) && maze.isLegal(xNew, yNew)) {
						//System.out.println("legal successor found " + " " + xNew + " " + yNew);
						((BlindRobotMazeNode)succ).addLocation(xNew, yNew, loc);
					}
					//cannot move, successor includes all locaitons cannot move
					else if (!maze.isLegal(curXNew, curYNew) && !maze.isLegal(xNew, yNew)){		
						// else locNew is illegal not add
						// not move, only keep loc illegal too
						((BlindRobotMazeNode)succ).addLocation(loc.getX(), loc.getY(), loc);
					}
				}
*/
				SearchNode succ = new BlindRobotMazeNode(getCost() + 1.0, this);
				for (Location loc: state) {
					int xNew = loc.getX() + action[0];
					int yNew = loc.getY() + action[1]; 
					
					//if legal add new location
					if (maze.isLegal(xNew, yNew)) {
						((BlindRobotMazeNode)succ).addLocation(xNew, yNew, loc);
					}
					//if not move add current location
					else {
						((BlindRobotMazeNode)succ).addLocation(loc.getX(), loc.getY(), loc);
					}
				}

				if (((BlindRobotMazeNode)succ).state.size() != 0)
					successors.add(succ);
				
			}
			return successors;

		}
		
		@Override
		public boolean goalTest() {
			if (state.size() == 1 && state.get(0).getX() == xGoal && state.get(0).getY() == yGoal)
				return true;
			return false;
		}


		// an equality test is required so that visited sets in searches
		// can check for containment of states
		@Override
		public boolean equals(Object other) {
			ArrayList<Location> state2 = ((BlindRobotMazeNode)other).state;
			if (state.size() == state2.size()) {
				boolean flag = false;
				for (Location loc: state) {
					flag = false;
					for (Location loc2: state2)
						if (loc.getX() == loc2.getX())
							flag = true;
					if (flag == false)
						return false;
				}
				return true;
			}
			return false;
					
		}

		@Override
		public int hashCode() {
			int hash = 0;
			for (Location loc: state) 
				hash = hash *100 + loc.hashCode();
			return hash; 
		}

		@Override
		public String toString() {
			String str = new String("Maze state ");
			for (Location loc: state)
				str += loc;
			str += " cost " + cost + " prior " + priority() + '\n';
			return str;
		}

		@Override
		public double getCost() {
			return cost;
		}
		

		@Override
		public double heuristic() {
			// manhattan distance metric for simple maze with one agent:
/*			double h = state.size() * state.size();
			double dx = xGoal - state.get(0).getX();
			double dy = yGoal - state.get(0).getY();
			return h*(Math.abs(dx)+Math.abs(dy));
			double h = state.size();
			double dx = xGoal - state.get(0).getX();
			double dy = yGoal - state.get(0).getY();
			if (h != 1)
				h = h * h * 100;
			return h * (Math.abs(dx)+Math.abs(dy));
			//size = 1 priority > (size > 1) priority that's why so slow!!!
			double h = state.size();
			double dx = xGoal - state.get(0).getX();
			double dy = yGoal - state.get(0).getY();
			dx = (Math.abs(dx)+Math.abs(dy));
			if (h == 1) {
				return dx;
			}
			else {
				return h/(maze.width * maze.height) + dx;
			}
			//h = longest distance to goal   cannot tell size
			double h = state.get(0).distance();
			for (Location loc: state) {
				double d = loc.distance();
				if (d > h)
					h = d;
			}
			return h;
*/			double h = 0;
			for (Location loc: state) {
				double d = loc.distance();
					h += d;
			}
			return h;
		}

		@Override
		public int compareTo(SearchNode o) {
			return (int) Math.signum(priority() - o.priority());
		}
		
		@Override
		public double priority() {
			return heuristic() + getCost();
		}
		
		//-----------------------------modify by M.K.
		@Override
		public SearchNode getParent() {
			return parent;
		}

	}
		
	// Now state in BlindRobotMazeNode needs to store many locations,
	// So this class represents location (x, y)
	public class Location extends Object {
		private int x;
		private int y;
		
		private Location prev;
		
		public Location(int m, int n, Location pa) {
			x = m;
			y = n;
			prev = pa;
		}
		
		public int getX() {
			return x;
		}
		
		public int getY() {
			return y;
		}
		
		public Location getPrev() {
			return prev;
		}
		
		public double distance() {
			double dx = xGoal - x;
			double dy = yGoal - y;
			return Math.abs(dx) + Math.abs(dy);
		}
		
		@Override
		public int hashCode() {
			return x*10 + y;
		}
		
		@Override
		public String toString() {
			return new String("Loc " + x + ", " + y + " ");
		}
	}

}
