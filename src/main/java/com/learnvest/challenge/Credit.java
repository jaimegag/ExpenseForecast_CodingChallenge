package com.learnvest.challenge;

public class Credit {
	private float payment;
	private float apr;
	private float begBal;
	private float endBal;
	public Credit(float p, float b, float a) {
		this.payment = p;
		this.begBal = b;
		this.apr = a;
	}
	public float getPayment() {
		return this.payment;
	}
	public float getApr() {
		return this.apr;
	}
	public float getBegBal() {
		return this.begBal;
	}
	public float getEndBal() {
		return this.endBal;
	}
	public void setPayment(float p) {
		this.payment = p;
	}
	public void setApr(float a) {
		this.apr = a;
	}
	public void setBegBal(float b) {
		this.begBal = b;
	}
	public void setEndBal(float e) {
		this.endBal = e;
	}
}