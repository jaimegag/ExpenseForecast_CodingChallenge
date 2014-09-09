package com.learnvest.challenge;

import org.joda.time.DateTime;

import java.util.TreeMap;

import com.learnvest.util.Json;
import com.google.gson.reflect.TypeToken;

public class App {
	
	public static enum Mode {A,B,C};
	
    public static void main(String...args) {
    	
    	// Read input parameters
    	// ClientCase cCase = Json.loadFromFile(args[0], ClientCase.class); // Equivalent result with the 
    																		// overloaded method using Class
    	ClientCase cCase = Json.loadFromFile(args[0], new TypeToken<ClientCase> () {});
    	
    	// Setting up the timeline
    	DateTime begin = new DateTime(2015, 01, 01, 0, 0, 0, 0); // January 01, 2015, 00:00:00
    	DateTime end = new DateTime(2015, 12, 31, 0, 0, 0, 0); // December 31, 2015, 00:00:00
    	DateTime now = new DateTime(begin);
    	
    	// We assume use case is realistic and that 28-day/monthly net gain is positive,
    	// otherwise no major purchases would be possible.
    	// There are monthly transactions and bi-weekly transactions, hence we can't manage monthly cycles. So I 
    	// decided to manage by-weekly cycles (or windows) to analyze the income-expense flow, from paycheck to paycheck.
    	// Some windows will be net positive, but some other may be net negative. We assume the use case is realistic and
    	// that 28-day/monthly net gain is positive, otherwise no major purchases would be possible ever. So every window or
    	// every other window must be positive, while we may have a negative window every other window (or every three in
    	// rare occasions, or only a couple a year in really rare occasions).
    	// Using the above assumptions these are the 3 different modes I have defined to try provide an answer to the case:
    	// Mode A (max safety):
    	//   - We assume the worst and consider we are always at the beginning of a negative window: it should be enough 
    	//     to have always enough balance in the checking to cover the maximum possible expense for the worst possible
    	//     window (rent + groceries + credit-card) plus the checking minimum balance, of course.
    	// Mode B (more flexible):
    	//   - We can try to be more flexible to allow these purchases earlier in the timeline, by confirming if we have
    	//     enough balance to cover the expenses of the window we are in (e.g: until next paycheck). But this assumption
    	//     makes this mode not 100% safe: if we are in a positive window and we cover this window expenses very tightly
    	//     and next window is a negative one then we may fail to maintain checking account balance above the minimum.
    	// Mode C (flexible and safe):
    	//   - We act differently depending of the type of window we are in. If we are in a negative window, we confirm we can
    	//     cover the remaining costs of the window we are in. If we are in a positive one and we are in a use case with
    	//     negative windows (next one) then we also simulate ahead if we will be above minimum balance during the next
    	//     window. We only check two windows max.
    	// I will only implement "Mode A" and "Mode C" below.
    	Mode mode = Mode.A;
    	
    	// Do we have negative windows?
    	boolean haveNegative = false;
    	if ( ((cCase.getRent()+cCase.getGroceries())>cCase.getIncome()) ||
    		 ((cCase.getGroceries()+cCase.getCredit().getPayment())>cCase.getIncome()) ||
    		 ((cCase.getRent()+cCase.getGroceries()+cCase.getCredit().getPayment())>cCase.getIncome()) ) {
    		haveNegative = true;
    	} else {
    		haveNegative = false; // Again, based on previous assumptions, the use case need "positive" weeks to be
    		                      // realistic, and this means that groceries' weekly expenses have to be saller than the income.
    	}
    	
    	// Timeline simulation approach to make sure we can provide the daily net worth
    	// for the CreditCard
    	TreeMap<String,Float> netWorth = new TreeMap<String,Float>(); // To store the daily net worth
    	boolean paidLastWeek = true; // To start paycheck on the second week of Jan (9th)
    	float currCheckBal = cCase.getChecking().getBegBal(); // Initialized to initial checking balance
    	float currCredBal = cCase.getCredit().getBegBal(); // Initialized to initial credit balance
    	// For the credit card interest expense we assume a constant monthly interest, based on the initial balance
    	// and the yearly APR. No recalculation every month.
    	float monthlyInterest = cCase.getCredit().getBegBal() * cCase.getCredit().getApr() / 12; // Balance * APR / 12
    	while (!now.isAfter(end)) {
    		// Process income
    		if (now.getDayOfWeek() == 6) { // Saturday (Paid on Friday and available next day)
    			if (!paidLastWeek) {
    				currCheckBal += cCase.getIncome();
    				paidLastWeek = true;
    			} else {
    				paidLastWeek = false;
    			}
    		}
    		// Process rent
    		if (now.getDayOfMonth() == 1) { // 1st of the Month
    			currCheckBal -= cCase.getRent(); // We assume the use case is realistic and rent can always be 
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
    				// First check if we have enough cash (checking balance + min balance)
    				if ((currCheckBal - p.getAmount()) >= cCase.getChecking().getMin()) {
    					// Second check if we risk going below minimum in the following days.
    					if (isPurchaseFeasible(cCase, p, haveNegative, mode, currCheckBal, now)) { // Purchase feasible!
    						currCheckBal -= p.getAmount();
    						p.setWhen(now.toString("yyyy-MM-dd"));
    					}
    				}
    			}
    		}
    		// Calculate Daily Net worth
    		netWorth.put(now.toString("yyyy-MM-dd"), currCheckBal - currCredBal);
    		// Jump to next day
    		System.out.println("->" + now.toString("yyyy-MM-dd") + ": Check[" + currCheckBal + "] Credit[" + currCredBal + "]");
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
    private static boolean isPurchaseFeasible(ClientCase cc, Purchase pu, boolean haveNegative, Mode m, 
    										  float cCBal, DateTime dt) {
    	// No need to do parameter error control ...
    	switch (m) {
    		case A:
    			if ( (cCBal - pu.getAmount()) >= (cc.getRent()+cc.getGroceries()+cc.getCredit().getPayment()+cc.getChecking().getMin()) ) {
    				return true;
    			}
    			break;
    		case C:
    			break;
    		default:
    			break;
    	}
    	return false; // If mode is not implemented we don't accept purchases.
    }
}