package com.absk.rtrader.core.models;

public class RealtimeStreamingSettings {

	private String ticker_name;
	private int brick_size;
	
	public RealtimeStreamingSettings(String ticker_name, int brick_size) {
		super();
		this.ticker_name = ticker_name;
		this.brick_size = brick_size;
	}
	public String getTicker_name() {
		return ticker_name;
	}
	public void setTicker_name(String ticker_name) {
		this.ticker_name = ticker_name;
	}
	public int getBrick_size() {
		return brick_size;
	}
	public void setBrick_size(int brick_size) {
		this.brick_size = brick_size;
	}
	
}
