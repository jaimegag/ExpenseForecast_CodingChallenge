package com.learnvest.challenge;

public class Checking {
	private float min;
	private float begBal;
	private float endBal;
	public Checking(float m, float b, float e) {
		this.min = m;
		this.begBal = b;
		this.endBal = e;
	}
	public float getMin() {
		return this.min;
	}
	public float getBegBal() {
		return this.begBal;
	}
	public float getEndBal() {
		return this.endBal;
	}
	public void setMin(float m) {
		this.min = m;
	}
	public void setBegBal(float b) {
		this.begBal = b;
	}
	public void setEndBal(float e) {
		this.endBal = e;
	}
}