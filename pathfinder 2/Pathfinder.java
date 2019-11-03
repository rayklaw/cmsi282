//======================================
// @Names: Raymond Law, Adam Hirata 
//======================================


import java.util.ArrayList;
import java.util.Queue;
import java.util.Map;
import java.util.LinkedList;
import java.util.PriorityQueue;

/**
 * Maze Pathfinding algorithm that implements a basic, uninformed, breadth-first tree search.
 */
public class Pathfinder {
    
    /**
     * Given a MazeProblem, which specifies the actions and transitions available in the
     * search, returns a solution to the problem as a sequence of actions that leads from
     * the initial to a goal state.
     * 
     * @param problem A MazeProblem that specifies the maze, actions, transitions.
     * @return An ArrayList of Strings representing actions that lead from the initial to
     * the goal state, of the format: ["R", "R", "L", ...]
     */
    public static ArrayList<String> solve (MazeProblem problem) {
        
    	Queue<SearchTreeNode> 	frontier 	= new PriorityQueue<SearchTreeNode>();
    	ArrayList<String> 		temp 		= new ArrayList<String>();
    	ArrayList<MazeState>	visited		= new ArrayList<MazeState>();
    	
    	frontier.add(new SearchTreeNode(problem.INITIAL_STATE, null, null));
    	
    	while(!frontier.isEmpty()) {
    		SearchTreeNode curr = frontier.remove();
    		if (curr.state.equals(problem.GOAL_STATE)) {
    			while (curr.parent != null) {
    				temp.add(curr.action);
    				curr = curr.parent;
    			}
    			ArrayList<String> result = new ArrayList<String>(temp.size());
				for (int i = temp.size() - 1; i >= 0; i--) {
					result.add(temp.get(i));
				}
				System.out.println(result.toString());
    			return result;
    		}
    		
    		Map<String, MazeState> map = problem.getTransitions(curr.state);
    		
    		visited.add(curr.state);
    		for (String action : map.keySet()) {
    			frontier.add(new SearchTreeNode(map.get(action), action, curr));
    		}
    	}
    	
    	return new ArrayList<String>();
    }
    
}

/**
 * SearchTreeNode that is used in the Search algorithm to construct the Search
 * tree.
 */
class SearchTreeNode {
    
    MazeState state;
    String action;
    SearchTreeNode parent;
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode (MazeState state, String action, SearchTreeNode parent) {
        this.state = state;
        this.action = action;
        this.parent = parent;
    }
    
}