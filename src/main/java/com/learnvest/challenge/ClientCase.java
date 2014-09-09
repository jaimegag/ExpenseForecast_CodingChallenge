package com.learnvest.challenge;

import java.util.List;
import java.util.Map;

public class ClientCase {

	private float income;
	private float rent;
	private float groceries;
	private List<Purchase> purchases;
	private Checking checking;
	private Credit credit;
	private Map<String,Float> netWorth;
	
	public float getIncome() {
		return this.income;
	}
	public float getRent() {
		return this.rent;
	}
	public float getGroceries() {
		return this.groceries;
	}
	public List<Purchase> getPurchases() {
		return this.purchases;
	}
	public Checking getChecking() {
		return this.checking;
	}
	public Credit getCredit() {
		return this.credit;
	}
	public Map<String,Float> getNetWorth() {
		return this.netWorth;
	}
	public void setNetWorth(Map<String,Float> m) {
		this.netWorth = m;
	}
}