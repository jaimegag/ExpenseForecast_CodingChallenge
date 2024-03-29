package com.learnvest.challenge;

import org.joda.time.DateTime;
import org.joda.time.Days;

import java.util.TreeMap;

import com.learnvest.util.Json;
import com.google.gson.reflect.TypeToken;

/* SOLUTION EXPLANATION
 * There are monthly transactions and bi-weekly transactions and so I decided to manage by-weekly cycles
 * (or windows) to analyze the income-expense flow, from paycheck to paycheck.
 * Some windows will be net positive, but some other may be net negative. I assume the use case is realistic and
 * that 28-day/monthly net gain is positive, otherwise no major purchases would be possible ever. So every window or
 * every other window must be positive, while there might be a negative window every other window (or every three in
 * rare occasions, or only a couple a year in really rare occasions).
 * Using the above assumptions these are the 3 different modes I have defined to try provide an answer to the case,
 * considering that the parameters of the input file can change:
 * Mode A (max safety):
 *   - I assume the worst and consider we are always at the beginning of a negative window: it should be enough 
 *     to have always enough balance in the checking to cover the maximum possible expense for the worst possible
 *     day (rent + groceries + credit-card) plus the checking minimum balance, of course.
 * Mode B (flexible but unsafe):
 *   - Attempt to be more flexible to allow these purchases earlier in the timeline, by simulating the future
 *     and checking if there is enough cash to cover the expenses of the current window (e.g: until next 
 *     paycheck) without going below the minimum. But this assumption makes this mode not 100% safe: if current window 
 *     is a positive window and we cover this window expenses very tightly and next window is a negative one then
 *     it may fail to maintain checking account balance above the minimum next week (or after).
 * Mode C (flexible and safe):
 *   - Like Mode B but simulating next window as well and confirming it will not go below the minimum balance during
 *     next window either. Assuming that 2 cycles of income are enough to pay for all monthly expenses, this should
 *     be safe enough and it shouldn't be necessary to simulate and check a third window. As we simulate up to 4 weeks
 *     to the future, this mode has the lowest performance.
 *     
 * IMPORTANT NOTE: If the application doesn't see feasible to make any of the big purchases, then no time will be provided
 * for that purchase (Output Json will not have a 'when' variable for that given purchase)
 * 
 * @author Jaime Gonzalez Aguilar
 * @version 0.1 9/9/2014
 */
public class App {
	
	public static enum Mode {A,B,C};
	
    public static void main(String...args) {
    	
    	// Read input parameters
    	ClientCase cCase = Json.loadFromFile(args[0], new TypeToken<ClientCase> () {});
    	
    	// Setting up the timeline
    	DateTime begin = new DateTime(2015, 01, 01, 0, 0, 0, 0); // January 01, 2015, 00:00:00
    	DateTime end = new DateTime(2015, 12, 31, 0, 0, 0, 0); // December 31, 2015, 00:00:00
    	DateTime now = new DateTime(begin);
    	
    	// Mode C selected
    	Mode mode = Mode.C;
    	 	
    	// Timeline simulation approach to make sure it can provide the daily net worth for the CreditCard
    	TreeMap<String,Float> netWorth = new TreeMap<String,Float>(); // To store the daily net worth
    	DateTime lastPaycheckReceived = new DateTime(2014, 12, 27, 0, 0, 0, 0); // To keep a timeline pointer to when last paycheck was received
    	float currCheckBal = cCase.getChecking().getBegBal(); // Initialized to initial checking balance
    	float currCredBal = cCase.getCredit().getBegBal(); // Initialized to initial credit balance
    	// For the credit card interest expense I assume a constant monthly interest, based on the initial balance
    	// and the yearly APR. No recalculation every month.
    	float monthlyInterest = cCase.getCredit().getBegBal() * cCase.getCredit().getApr() / 12; // Balance * APR / 12
    	while (!now.isAfter(end)) {
    		// Process income
    		if (now.getDayOfWeek() == 6) { // Saturday (Paid on Friday and available next day)
    			if (Days.daysBetween(lastPaycheckReceived, now).getDays() >= 14) { // More than 1 week since last paycheck. 
    				currCheckBal += cCase.getIncome();
    				lastPaycheckReceived = new DateTime(now);
    			}
    		}
    		// Process rent
    		if (now.getDayOfMonth() == 1) { // 1st of the Month
    			currCheckBal -= cCase.getRent(); // I assume the use case is realistic and rent can always be 
    											 // paid without going below min balance if no big purchases are made
    		}
    		// Process groceries
    		if (now.getDayOfWeek() == 6) { // Saturday
    			currCheckBal -= cCase.getGroceries();
    		}
    		// Process credit card payment
    		if (now.getDayOfMonth() == 20) { // 20th of the Month
    			currCheckBal -= cCase.getCredit().getPayment();
    			currCredBal -= (cCase.getCredit().getPayment() - monthlyInterest); // First pay interest, the remaining goes towards the balance
    		}
    		// Check feasibility for big purchases, and trigger them if possible
    		for (Purchase p : cCase.getPurchases()) { // For all purchases
    			if (p.getWhen() == null) { // Not yet purchased
    				// First check if there is enough cash (checking balance + min balance)
    				if ((currCheckBal - p.getAmount()) >= cCase.getChecking().getMin()) {
    					// Second check if there is risk to go below minimum in the following days.
    					if (isPurchaseFeasible(cCase, p, mode, currCheckBal, now, lastPaycheckReceived)) { // Purchase feasible!
    						currCheckBal -= p.getAmount();
    						p.setWhen(now.toString("yyyy-MM-dd"));
    					}
    				}
    			}
    		}
    		// Calculate Daily Net worth
    		netWorth.put(now.toString("yyyy-MM-dd"), currCheckBal - currCredBal);
    		// Jump to next day
    		now = now.plusDays(1);
    	}
    	// Update case with final balances and daily net worth
    	cCase.getChecking().setEndBal(currCheckBal);
    	cCase.getCredit().setEndBal(currCredBal);
    	cCase.setNetWorth(netWorth);
    	// Output all data after processing
        System.out.println( Json.toJson( cCase ) );
        
        // Initial code: System.out.println( Json.toJson( Json.loadFromFile(args[0]) ) );
    }
    private static boolean isPurchaseFeasible(ClientCase cc, Purchase pu, Mode m, float cCBal, DateTime dt, DateTime lastpc) {
    	// No need to do parameter error control ...
    	switch (m) {
    		case A:
    			if ( (cCBal - pu.getAmount()) >= (cc.getRent()+cc.getGroceries()+cc.getCredit().getPayment()+cc.getChecking().getMin()) ) {
    				return true;
    			}
    			break;
    		case B:
    			if ( confirmCheckingStaysAboveMinimumOnFollowingWindows(cc,(cCBal - pu.getAmount()),dt,lastpc,1) )  {
    				return true;
    			}
    			break;
    		case C:
    			if ( confirmCheckingStaysAboveMinimumOnFollowingWindows(cc,(cCBal - pu.getAmount()),dt,lastpc,2) ) {
    				return true;
    			}
    			break;
    		default:
    			break;
    	}
    	return false; // If mode is not implemented no purchases are accepted
    }

    private static boolean confirmCheckingStaysAboveMinimumOnFollowingWindows(ClientCase cc, float bal, DateTime currDate, DateTime lastpc, int numWindows) {
    	// No need to do parameter error control ...
    	float simBal = bal;
    	DateTime day = new DateTime(currDate);
    	day = day.plusDays(1); // I check big purchases after paying all expenses of the day, and processing income, so it starts cheking next day
    	DateTime windowEnd = new DateTime(lastpc);
    	windowEnd = windowEnd.plusWeeks(numWindows*2); // Two weeks after
    	while (!day.isAfter(windowEnd)) {
    		// Calculate income
    		if (day.getDayOfWeek() == 6) { // Saturday (Paid on Friday and available next day)
    			if (Days.daysBetween(lastpc, day).getDays() >= 14) { // More than 1 week since last paycheck. 
    				simBal += cc.getIncome();
    				lastpc = new DateTime(day);
    			}
    		}
    		// Calculate rent
    		if (day.getDayOfMonth() == 1) { // 1st of the Month
    			simBal -= cc.getRent(); // I assume the use case is realistic and rent can always be 
    											 // paid without going below min balance if no big purchases are made
    		}
    		// Calculate groceries
    		if (day.getDayOfWeek() == 6) { // Saturday
    			simBal -= cc.getGroceries();
    		}
    		// Calculate credit card payment
    		if (day.getDayOfMonth() == 20) { // 20th of the Month
    			simBal -= cc.getCredit().getPayment();
    		}
    		// Check if it went below the minimum
    		if (simBal < cc.getChecking().getMin()) {
    			return false;
    		}
    		day = day.plusDays(1);
    	}
    	return true;
    }
}