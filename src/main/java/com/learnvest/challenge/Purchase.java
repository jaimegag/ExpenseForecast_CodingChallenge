package com.learnvest.challenge;

public class Purchase {
	private String name;
	private float amt;
	private String when;
	public Purchase(String n, float a, String w) {
		this.name = n;
		this.amt = a;
		this.when = w;
	}
	public String getName() {
		return this.name;
	}
	public float getAmount() {
		return this.amt;
	}
	public String getWhen() {
		return this.when;
	}
	public void setName(String n) {
		this.name = n;
	}
	public void setAmount(float a) {
		this.amt = a;
	}
	public void setWhen(String w) {
		this.when = w;
	}
}