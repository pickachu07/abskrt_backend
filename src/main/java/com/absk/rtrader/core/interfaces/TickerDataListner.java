package com.absk.rtrader.core.interfaces;

public interface TickerDataListner {

	public void onNext(String data);
	
	public String getId();
	
	
}
