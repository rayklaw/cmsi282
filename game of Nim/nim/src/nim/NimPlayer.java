package nim;

//======================================
//@Names: Raymond Law, Adam Hirata 
//======================================

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Artificial Intelligence responsible for playing the game of Nim! Implements
 * the alpha-beta-pruning mini-max search algorithm
 */
public class NimPlayer {

	private final int MAX_REMOVAL;

	NimPlayer(int MAX_REMOVAL) {
		this.MAX_REMOVAL = MAX_REMOVAL;
	}

	/**
	 * 
	 * @param remaining Integer representing the amount of stones left in the pile
	 * @return An int action representing the number of stones to remove in the
	 *         range of [1, MAX_REMOVAL]
	 */
	public int choose(int remaining) {
		ArrayList<Integer> choices = new ArrayList<>();

		for (int i = 1; i <= this.MAX_REMOVAL; i++) {
			if ((remaining - i) >= 0) {
				choices.add(alphaBetaMinimax(new GameTreeNode(remaining - i, 0, false), Integer.MIN_VALUE,
						Integer.MAX_VALUE, true, new HashMap<>()));
			}
		}
		for (int i = choices.size() - 1; i > 0; i--) {
			if (choices.get(i) == 1) {
				return i + 1;
			}
		}
		return 1;
	}

	/**
	 * Constructs the minimax game tree by the tenets of alpha-beta pruning with
	 * memoization for repeated states.
	 * 
	 * @param node    The root of the current game sub-tree
	 * @param alpha   Smallest minimax score possible
	 * @param beta    Largest minimax score possible
	 * @param isMax   Boolean representing whether the given node is a max (true) or
	 *                min (false) node
	 * @param visited Map of GameTreeNodes to their minimax scores to avoid
	 *                repeating large subtrees
	 * @return Minimax score of the given node + [Side effect] constructs the game
	 *         tree originating from the given node
	 */
	private int alphaBetaMinimax(GameTreeNode node, int alpha, int beta, boolean isMax, Map<GameTreeNode, Integer> visited) {

		for (GameTreeNode curr : visited.keySet()) {
        	if (curr.equals(node)) {
        		return visited.get(curr);
        	}
        }
		// for-loop to get transitions and generate child nodes
		for (int i = 1; i <= this.MAX_REMOVAL; i++) {
			if ((node.remaining - i) >= 0) {
				node.children.add(new GameTreeNode(node.remaining - i, i, !node.isMax));
			}
		}

		if (node.children.isEmpty()) {
			if (node.remaining == 0 && !node.isMax) {
				return 1;
			} else {
				return 0;
			}
		}

		int v;

		if (node.isMax) {
			v = Integer.MIN_VALUE;
			for (GameTreeNode child : node.children) {
				v = Math.max(v, alphaBetaMinimax(child, alpha, beta, !node.isMax, visited));
				alpha = Math.max(v, alpha);
				if (beta < alpha) {
					break;
				}
			}
			visited.put(node, v);
			return v;
		} else {
			v = Integer.MAX_VALUE;
			for (GameTreeNode ligma : node.children) {
				v = Math.min(v, alphaBetaMinimax(ligma, alpha, beta, !node.isMax, visited));
				beta = Math.min(v, beta);
				if (beta < alpha) {
					break;
				}
			}
			visited.put(node, v);
			return v;
		}
	}
}

/**
 * GameTreeNode to manage the Nim game tree.
 */
class GameTreeNode {

	int remaining, action, score;
	boolean isMax;
	ArrayList<GameTreeNode> children;

	/**
	 * Constructs a new GameTreeNode with the given number of stones remaining in
	 * the pile, and the action that led to it. We also initialize an empty
	 * ArrayList of children that can be added-to during search, and a placeholder
	 * score of -1 to be updated during search.
	 * 
	 * @param remaining The Nim game state represented by this node: the # of stones
	 *                  remaining in the pile
	 * @param action    The action (# of stones removed) that led to this node
	 * @param isMax     Boolean as to whether or not this is a maxnode
	 */
	GameTreeNode(int remaining, int action, boolean isMax) {
		this.remaining = remaining;
		this.action = action;
		this.isMax = isMax;
		children = new ArrayList<>();
		score = -1;
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof GameTreeNode
				? remaining == ((GameTreeNode) other).remaining && isMax == ((GameTreeNode) other).isMax
						&& action == ((GameTreeNode) other).action
				: false;
	}

	@Override
	public int hashCode() {
		return remaining + ((isMax) ? 1 : 0);
	}

}
