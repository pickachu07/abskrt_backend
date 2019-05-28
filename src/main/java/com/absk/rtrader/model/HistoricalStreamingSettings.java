package com.absk.rtrader.model;

import java.text.ParseException;

public class HistoricalStreamingSettings {

	private String ticker_name;
	private int brick_size;
	
	public HistoricalStreamingSettings(String ticker_name, int brick_size,String date) throws ParseException {
		super();
		this.ticker_name = ticker_name;
		this.brick_size = brick_size;
		//SimpleDateFormat dateFormatter=new SimpleDateFormat("dd-mm-yyyy");
		//this.date =dateFormatter.parse(date);
	}
	public HistoricalStreamingSettings(String ticker_name, int brick_size){
		super();
		this.ticker_name = ticker_name;
		this.brick_size = brick_size;
	}
	public HistoricalStreamingSettings(){
		super();
		this.ticker_name = "BANKNIFTY";
		this.brick_size = 10;
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
