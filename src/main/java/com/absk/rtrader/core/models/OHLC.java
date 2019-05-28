package com.absk.rtrader.core.models;

public class OHLC {
	private double open;
	private double high;
	private double low;
	private double close;
	private double volume;
	private Long timestamp;
	
	public OHLC(double open, double high, double low, double close, double volume, Long timestamp) {
		super();
		this.open = open;
		this.high = high;
		this.low = low;
		this.close = close;
		this.volume = volume;
		this.timestamp = timestamp;
	}
	public OHLC(String historicalOHLC) {
		String[] data = historicalOHLC.split(",");
		this.timestamp = Long.parseLong(data[0]);
		this.open = Double.parseDouble(data[1]);
		this.high = Double.parseDouble(data[2]);
		this.low = Double.parseDouble(data[3]);
		this.close = Double.parseDouble(data[4]);
		this.volume = Double.parseDouble(data[5]);
	}
	
	public double getOpen() {
		return open;
	}
	public void setOpen(double open) {
		this.open = open;
	}
	public double getHigh() {
		return high;
	}
	public void setHigh(double high) {
		this.high = high;
	}
	public double getLow() {
		return low;
	}
	public void setLow(double low) {
		this.low = low;
	}
	public double getClose() {
		return close;
	}
	public void setClose(double close) {
		this.close = close;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
}
