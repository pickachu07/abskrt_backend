package com.absk.rtrader.model;

public class TickerData {
	private double open;
	private double high;
	private double low;
	private double close;
	private double volume;
	private Long timestamp;
	private String exchange;
	private String symbol;
	private double yearly_high;
	private double yearly_low;
	
	
	@Override
	public String toString() {
		return "TickerData [open=" + open + ", high=" + high + ", low=" + low + ", close=" + close + ", volume="
				+ volume + ", timestamp=" + timestamp + ", exchange=" + exchange + ", symbol=" + symbol
				+ ", yearly_high=" + yearly_high + ", yearly_low=" + yearly_low + "]";
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
	public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getSymbol() {
		return symbol;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public double getYearly_high() {
		return yearly_high;
	}
	public void setYearly_high(double yearly_high) {
		this.yearly_high = yearly_high;
	}
	public double getYearly_low() {
		return yearly_low;
	}
	public void setYearly_low(double yearly_low) {
		this.yearly_low = yearly_low;
	}
	
}
