/**
 * @author Adam Hirata, Ray Law
 * Last Edited: 2019-5-9 4:20pm
 */
package csp;

import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * CSP: Calendar Satisfaction Problem Solver
 * Provides a solution for scheduling some n meetings in a given
 * period of time and according to some unary and binary constraints
 * on the dates of each meeting.
 */
public class CSP {

    /**
     * Public interface for the CSP solver in which the number of meetings,
     * range of allowable dates for each meeting, and constraints on meeting
     * times are specified.
     * @param nMeetings The number of meetings that must be scheduled, indexed from 0 to n-1
     * @param rangeStart The start date (inclusive) of the domains of each of the n meeting-variables
     * @param rangeEnd The end date (inclusive) of the domains of each of the n meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of the n meetings,
     *         indexed by the variable they satisfy, or null if no solution exists.
     */
    public static List<LocalDate> solve (int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, Set<DateConstraint> constraints) {
    	ArrayList<dateVar> 		domains 		= new ArrayList<>();  //a list of the domains 
    	LocalDate 				curr 			= rangeStart;
    	ArrayList<LocalDate> 	result 			= new ArrayList<>();
    	
    	domains = createDomains(nMeetings, rangeStart, rangeEnd, curr);
    	
    	//performing node-consistent filtering on the different domains
    	//performing arc-consistent filtering on the different domains
    	//returning null if a null solution occurs
    	unaryFilter(domains, constraints);
    	binaryFilter(domains, constraints);
    	for (dateVar dom: domains) {
    		if (dom.domain.isEmpty()) {return null;}
    	}
    	
    	//finding a solution through a backtracking recursion
    	return backtracking(nMeetings, result, domains, constraints);
    }
    
    /**
     * Recursively finds a solution for the CSP
     * @param nMeetings
     * @param solution
     * @param domains
     * @param constraints
     * @return solution		either null, or a valid solution
     */
    private static ArrayList<LocalDate> backtracking(int nMeetings, ArrayList<LocalDate> solution, ArrayList<dateVar> domains, Set<DateConstraint> constraints) {
    	//base case for recursion
    	if (solution.size() == nMeetings) {
    		if(isValidSolution(solution, constraints)) {return solution;}
    		else {return null;}
    	}
    	for (LocalDate d: domains.get(solution.size()).domain) {
    		solution.add(d);
    		ArrayList<LocalDate> subSoln = backtracking(nMeetings, solution, domains, constraints);
    		if (subSoln != null) {return subSoln;}
    		solution.remove(solution.size() - 1);
    	}
    	return null;
    }
    
    /**
     * Analyzes a given terminal state of the CSP problem
     * @param solution
     * @param constraints
     * @return boolean		true if the given solution is valid, false otherwise
     */
    private static boolean isValidSolution(ArrayList<LocalDate> solution, Set<DateConstraint> constraints) {
    	for (DateConstraint d : constraints) {
            LocalDate leftDate = solution.get(d.L_VAL),
                      rightDate = (d.arity() == 1) 
                          ? ((UnaryDateConstraint) d).R_VAL 
                          : solution.get(((BinaryDateConstraint) d).R_VAL);
            
            boolean sat = false;
            switch (d.OP) {
	            case "==": if (leftDate.isEqual(rightDate))  sat = true; break;
	            case "!=": if (!leftDate.isEqual(rightDate)) sat = true; break;
	            case ">":  if (leftDate.isAfter(rightDate))  sat = true; break;
	            case "<":  if (leftDate.isBefore(rightDate)) sat = true; break;
	            case ">=": if (leftDate.isAfter(rightDate) || leftDate.isEqual(rightDate))  sat = true; break;
	            case "<=": if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) sat = true; break;
            }
            if (!sat) {
                return false;
            }
    	}
    	return true;
    }
    
    /**
     * Filters the domains of each meeting based on the UnaryDateConstraints
     * @param domains
     * @param constraints
     */
    private static void unaryFilter(ArrayList<dateVar> domains, Set<DateConstraint> constraints) {
    	for (DateConstraint d : constraints) {
            Set<LocalDate> curDom = domains.get(d.L_VAL).domain;
            Set<LocalDate> removal = new HashSet<>();
            if (d.arity() == 1) {
            	LocalDate r1 = ((UnaryDateConstraint) d).R_VAL;
				switch (d.OP) {
			        case "==":
			        	if (curDom.contains(r1)) {
			        		curDom.clear();
			        		curDom.add(r1);
			        	} else {
			        		curDom.clear();
			        	}; break;
			        case "!=": 
			        	curDom.remove(r1); break;
			        case ">":
			        	for (LocalDate date: curDom) {
			        		if (!date.isAfter(r1)) {
			        			removal.add(date);
			        		}
			        	}
			        	curDom.removeAll(removal); break;
			        case "<":  
			        	for (LocalDate date: curDom) {
			        		if (!date.isBefore(r1)) {
			        			removal.add(date);
			        		}
			        	}
			        	curDom.removeAll(removal); break;
			        case ">=": 
			        	for (LocalDate date: curDom) {
			        		if (date.isBefore(r1)) {
			        			removal.add(date);
			        		}
			        	}
			        	curDom.removeAll(removal); break;
			        case "<=": 
			        	for (LocalDate date: curDom) {
			        		if (date.isAfter(r1)) {
			        			removal.add(date);
			        		}
			        	}
			        	curDom.removeAll(removal); break;
		        }
            }
        }
    }
    
    /**
     * Filters the domains of each meeting based on the BinaryDateConstraints... sorry for the dry code
     * @param domains
     * @param constraints
     */
    private static void binaryFilter(ArrayList<dateVar> domains, Set<DateConstraint> constraints) {
    	for (DateConstraint d : constraints) {
            Set<LocalDate> curDom = domains.get(d.L_VAL).domain;
            Set<LocalDate> removal = new HashSet<>();
            if (d.arity() == 2) {
            	Set<LocalDate> r2 = domains.get(((BinaryDateConstraint) d).R_VAL).domain;
            	switch (d.OP) {
    	            case "==":
    	            	curDom.retainAll(r2);
    	            	r2.retainAll(curDom);
    	            ; break;
    	            case ">":
    	            	for (LocalDate date: curDom) {
    	            		boolean found = false;
    	            		for (LocalDate date2: r2) {
    	            			if (date.isAfter(date2)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date);}
    	            	}
    	            	curDom.removeAll(removal);
    	            	removal.clear();
    	            	for (LocalDate date2: r2) {
    	            		boolean found = false;
    	            		for (LocalDate date: curDom) {
    	            			if (date2.isBefore(date)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date2);} 
    	            	}
    	            	r2.removeAll(removal);
    	            	removal.clear();
    	            ; break;
    	            case "<":
    	            	for (LocalDate date: curDom) {
    	            		boolean found = false;
    	            		for (LocalDate date2: r2) {
    	            			if (date.isBefore(date2)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date);}
    	            	}
    	            	curDom.removeAll(removal);
    	            	removal.clear();
    	            	for (LocalDate date2: r2) {
    	            		boolean found = false;
    	            		for (LocalDate date: curDom) {
    	            			if (date2.isAfter(date)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date2);} 
    	            	}
    	            	r2.removeAll(removal);
    	            	removal.clear();
    	            ; break;
    	            case ">=":
    	            	for (LocalDate date: curDom) {
    	            		boolean found = false;
    	            		for (LocalDate date2: r2) {
    	            			if (!date.isBefore(date2)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date);}
    	            	}
    	            	curDom.removeAll(removal);
    	            	removal.clear();
    	            	for (LocalDate date2: r2) {
    	            		boolean found = false;
    	            		for (LocalDate date: curDom) {
    	            			if (!date2.isAfter(date)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date2);} 
    	            	}
    	            	r2.removeAll(removal);
    	            	removal.clear();
    	            ; break;
    	            case "<=":
    	            	for (LocalDate date: curDom) {
    	            		boolean found = false;
    	            		for (LocalDate date2: r2) {
    	            			if (!date.isAfter(date2)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date);}
    	            	}
    	            	curDom.removeAll(removal);
    	            	removal.clear();
    	            	for (LocalDate date2: r2) {
    	            		boolean found = false;
    	            		for (LocalDate date: curDom) {
    	            			if (!date2.isBefore(date)) {
    	            				found = true;
    	            			}
    	            		}
    	            		if (!found) {removal.add(date2);} 
    	            	}
    	            	r2.removeAll(removal);
    	            	removal.clear();
    	            ; break;
            	}
            }
        }
    }
    
    /**
     * Creates dateVars with domains representing the whole date range of the CSP
     * @param nMeetings
     * @param rangeStart
     * @param rangeEnd
     * @param curr
     * @return result 		an arrayList of dateVars where each dateVar 
     * 						represents the entire range of dates for the CSP
     */
    private static ArrayList<dateVar> createDomains(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd, LocalDate curr) {
    	ArrayList<dateVar> 	result 			= new ArrayList<>();
    	Set<LocalDate> 		possibleDomain 	= new HashSet<LocalDate>();
    	
    	//adding all dates within range
    	while (!curr.equals(rangeEnd.plusDays(1))) {
    		possibleDomain.add(curr);
    		curr = curr.plusDays(1);
    	}
    	
    	//creating new dateVars
    	for (int i = 0; i < nMeetings; i++) {
    		result.add(new dateVar(new HashSet<LocalDate>(possibleDomain)));
    	}
    	return result;
    }
    
    /**
     * An object that represents the domain of possible dates
     * for each meeting in the CSP
     * Takes in a set of LocalDates for the constructor
     */
    private static class dateVar {
    	Set<LocalDate> domain;
    	
    	dateVar(Set<LocalDate> domain) {
    		this.domain = domain;
    	}
    }
}