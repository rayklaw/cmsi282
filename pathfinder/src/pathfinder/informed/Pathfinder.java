package pathfinder.informed;
//======================================
// @Names: Raymond Law, Adam Hirata 
//======================================


import java.util.ArrayList;
import java.util.Queue;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.HashSet;

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
    	
    	ArrayList<String> 	result 		= new ArrayList<String>();
    	HashSet<MazeState> 	obj 		= new HashSet<>();
    	
    	if (problem.KEY_STATE == null) {
    		return null;
    	}
    	
    	obj.add(problem.KEY_STATE);
    	try {
    		result.addAll(partialSolve(problem, new SearchTreeNode(problem.INITIAL_STATE, null, null, 0, 0), obj));
        } catch (Exception e) {
    		return null;
    	}
    	obj = problem.goalSet;
    	try {
    		result.addAll(partialSolve(problem, new SearchTreeNode(problem.KEY_STATE, null, null, 0, 0), obj));
    	} catch (Exception e) {
    		return null;
    	}
    	return result;
    }
    
    public static ArrayList<String> partialSolve (MazeProblem problem, SearchTreeNode curr, HashSet<MazeState> obj) {
    	HashSet<MazeState>		visited		= new HashSet<MazeState>();
    	ArrayList<String> 		temp 		= new ArrayList<String>();
    	Queue<SearchTreeNode> 	frontier 	= new PriorityQueue<SearchTreeNode>((SearchTreeNode n1, SearchTreeNode n2) -> n1.evaluate() - n2.evaluate());
		
    	frontier.add(new SearchTreeNode(curr.state, null, null, 0, 0));
    	while (!frontier.isEmpty()) {
    		curr = frontier.remove();
    		if (obj.contains(curr.state)) {
				while (curr.parent != null) {
    				temp.add(curr.action);
    				curr = curr.parent;
    			}
    			ArrayList<String> result = new ArrayList<String>();
				for (int j = temp.size() - 1; j >= 0; j--) {
					result.add(temp.get(j));
				}
    			return result;
			}
    		
        	Map<String, MazeState> map = problem.getTransitions(curr.state);
    		visited.add(curr.state);
    		for (String action : map.keySet()) {
    			if (!visited.contains(map.get(action))) {
    				frontier.add(new SearchTreeNode(map.get(action), action, curr, curr.pastCost + problem.getCost(map.get(action)), problem.distance(map.get(action), obj)));
    			}
    		}
    	}
    	return null;
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
    int pastCost;
    int futureCost;
    
    /**
     * Constructs a new SearchTreeNode to be used in the Search Tree.
     * 
     * @param state The MazeState (col, row) that this node represents.
     * @param action The action that *led to* this state / node.
     * @param parent Reference to parent SearchTreeNode in the Search Tree.
     */
    SearchTreeNode (MazeState state, String action, SearchTreeNode parent, int pastCost, int futureCost) {
        this.state = state;
        this.action = action;
        this.parent = parent;
        this.pastCost = pastCost;
        
    }
    
    public int evaluate() {
    	return this.pastCost + this.futureCost;
    }
}