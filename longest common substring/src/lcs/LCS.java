/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
  Names     : Adam Hirata, Ray Law
  Last Edit : 3/29/19
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/

package lcs;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LCS {
    
    /**
     * memoCheck is used to verify the state of your tabulation after
     * performing bottom-up and top-down DP. Make sure to set it after
     * calling either one of topDownLCS or bottomUpLCS to pass the tests!
     */
    public static int[][] memoCheck;
    public static int[][] table;
    // -----------------------------------------------
    // Shared Helper Methods
    // -----------------------------------------------
    
    // [!] TODO: Add your shared helper methods here!
    
    private static Set<String> collectSolution(String rStr, int r, String cStr, int c, int[][] memo) {
    	Set<String> result = new HashSet<String>();
    	if (r == 0 || c == 0) {
    		result.add(new String(""));
    		return result;
    	}
    	else if (rStr.charAt(r - 1) == cStr.charAt(c - 1)) {
    		result = collectSolution(rStr, r - 1, cStr, c - 1, memo);
    		ArrayList<String> removal = new ArrayList<>();
    		ArrayList<String> insertion = new ArrayList<>();
    		for (String str : result) {
    			insertion.add(str + rStr.charAt(r - 1));
    			removal.add(str);
    		}
    		for (int i = 0; i < insertion.size(); i++) {
    			result.add(insertion.get(i));
    		}
    		for (int i = 0; i < removal.size(); i++) {
    			result.remove(removal.get(i));
    		}
    		return result;
    	}
    	else {
    		if (table[r][c - 1] >= table[r - 1][c]) {
    			Set<String> left = collectSolution(rStr, r, cStr, c - 1, memo);
    			result.addAll(left);
    		}
    		if (table[r - 1][c] >= table[r][c - 1]) {
    			Set<String> up = collectSolution(rStr, r - 1, cStr, c, memo);
    			result.addAll(up);
    		}
    		return result;
    	}
    }
    // -----------------------------------------------
    // Bottom-Up LCS
    // -----------------------------------------------
    
    /**
     * Bottom-up dynamic programming approach to the LCS problem, which
     * solves larger and larger subproblems iterative using a tabular
     * memoization structure.
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     *         [Side Effect] sets memoCheck to refer to table
     */
    public static Set<String> bottomUpLCS (String rStr, String cStr) {
    	bottomUpTableFill(rStr, cStr);
    	memoCheck = table;
        return collectSolution(rStr, rStr.length(), cStr, cStr.length(), table);
    }
    
    // [!] TODO: Add any bottom-up specific helpers here!
    
    private static void bottomUpTableFill(String rStr, String cStr) {
    	//initializing the table dimensions
    	table = new int[rStr.length() + 1][cStr.length() + 1];
    	
    	//filling the gutters and the rest of the table
    	for (int i = 0; i < rStr.length(); i++) {
    		table[i][0] = 0;
    	}
    	for (int j = 0; j < cStr.length(); j++) {
    		table[0][j] = 0;
    	}
    	for (int i = 1; i < rStr.length() + 1; i++) {
    		for (int j = 1; j < cStr.length() + 1; j++) {
    			if (rStr.charAt(i - 1) == cStr.charAt(j - 1)) {
    				table[i][j] = table[i - 1][j - 1] + 1;
    			} else {
    				table[i][j] = Math.max(table[i - 1][j], table[i][j - 1]);
    			}
    		}
    	}
    }
    
    // -----------------------------------------------
    // Top-Down LCS
    // -----------------------------------------------
    
    /**
     * Top-down dynamic programming approach to the LCS problem, which
     * solves smaller and smaller subproblems recursively using a tabular
     * memoization structure.
     * @param rStr The String found along the table's rows
     * @param cStr The String found along the table's cols
     * @return The longest common subsequence between rStr and cStr +
     *         [Side Effect] sets memoCheck to refer to table  
     */
    public static Set<String> topDownLCS (String rStr, String cStr) {
    	//initializing table dimensions and filling it with zeroes
    	table = new int[rStr.length() + 1][cStr.length() + 1];
    	for (int i = 0; i <= rStr.length(); i++) {
    		for (int j = 0; j <= cStr.length(); j++) {
    			table[i][j] = 0;
    		}
    	}
    	
    	//filling the table cells which are to be visited
    	topDownTableFill(rStr, cStr);
    	memoCheck = table;
    	return collectSolution(rStr, rStr.length(), cStr, cStr.length(), table);
    }
    
    // [!] TODO: Add any top-down specific helpers here!
    
    private static int topDownTableFill (String rStr, String cStr) {
    	//base case and memoized case
    	if (table[rStr.length()][cStr.length()] != 0) {
    		return table[rStr.length()][cStr.length()];
    	}
    	if (rStr.isEmpty() || cStr.isEmpty()) {
    		return 0;
    	}
    	
    	//other two cases
    	if (rStr.charAt(rStr.length() - 1) == cStr.charAt(cStr.length() - 1)) {
    		return table[rStr.length()][cStr.length()] = 1 + topDownTableFill (rStr.substring(0, rStr.length() - 1), cStr.substring(0, cStr.length() - 1));
    	} else {
    		return table[rStr.length()][cStr.length()] = Math.max(
    				topDownTableFill (rStr, cStr.substring(0, cStr.length() - 1)),
    				topDownTableFill (rStr.substring(0, rStr.length() - 1), cStr)
    				);
    	}
    }
}
