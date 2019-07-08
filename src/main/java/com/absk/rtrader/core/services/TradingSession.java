package com.absk.rtrader.core.services;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.indicators.NRenko;
import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.utils.TickerUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;


@Component
public class TradingSession {

	@Autowired
	private NRenko renko;
	
	@Autowired
	private TickerUtil tickerUtil;
	
	String tickerName;
	int sessionType;//0 -->realtime 1 --> optimization TODO: change to enum
	int timeFrame;
	float brickSize;
	Table<String, Integer, Double > orders;
	double profit;
	int orderCount;
	int last_signal_type;
	ArrayList<Ticker> rb;
	int buffer_signal_type;
	int lastCalculatedTrade;
	double tempProfit;
	
	
	public TradingSession(String tickerName,int sessionType,float brickSize) {
		this.tickerName = tickerName;
		this.sessionType = sessionType;
		this.brickSize = brickSize;
		this.orderCount = 0;
		orders = HashBasedTable.create();
		rb = new ArrayList<Ticker>();
		this.last_signal_type = -1;
		this.buffer_signal_type = -1;
		this.lastCalculatedTrade =0;
		this.tempProfit = 0.0;
	}
	public TradingSession(){
		this.brickSize = 10;//make it configurable
		this.orderCount = 0;
		orders = HashBasedTable.create();
		rb = new ArrayList<Ticker>();
		this.last_signal_type = -1;
		this.buffer_signal_type = -1;
		this.lastCalculatedTrade =0;
		this.tempProfit = 0.0;
	}
	
	void registerBuyOpt(Double price,Date date) {
		orderCount++;
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss");
		orders.put("Buy at "+dateFormat.format(date), orderCount, price);
	}
	
	void registerSellOpt(Double price,Date date) {
		orderCount++;
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss");
		orders.put("Sell at "+dateFormat.format(date), orderCount, price);
	}
	
	double getProfit() {
		return this.tempProfit;
	}
	
	public ArrayList<Double> processAllData(OHLC[] data,double bs) {
		
		ArrayList<Ticker> sourceTickArr = tickerUtil.toTickerArray(data);
		renko.setBrickSize(bs);
		renko.buildHistory(sourceTickArr, "open");
		ArrayList<Double> out = renko.getRenkoPrices();
		renko.reset();
		return out;
		
		/*
		for(int i=0;i<data.length;i++) {
    		
    		System.out.println("Historical data:"+data[i].getClose());
    		Ticker tick = tickerUtil.convertToTicker(data[i]);
    		tickArr = renko.drawRenko(tick,bs);
    		processData(tickArr);
    		calculateProfit();
    		rb.addAll(tickArr);
    		tickArr = null;	
    	}*/
		
	}
	
	public void processData(ArrayList<Ticker> ohlc) {
		int brickCount =  ohlc.size();
		if(ohlc.size()<1)return;
		for(int i=0;i<brickCount;i++) {
			//System.out.println("BrickCount:"+i+"Close: "+ohlc.get(i).getData().getClose());
		}
		int current_signal_type = getBrickType(ohlc.get(0));//1 --> positive brick 0 --> negetive brick
		if(brickCount ==1) {
			if(current_signal_type == 1) {//positive brick
				if(this.buffer_signal_type == 1){  //buffer is positive	
					if(this.last_signal_type != 1) { //last signal not buy(1) --> last signal sell(0) or null(-1)
						//buy Signal
						registerBuyOpt(ohlc.get(brickCount-1).getData().getClose(),new Date(ohlc.get(brickCount-1).getData().getTimestamp()));
						System.out.println("Signal:Buy :: last signal is not buy at"+new Date(ohlc.get(brickCount-1).getData().getTimestamp())+":: Single Brick generated"+brickCount +" at price:"+ohlc.get(brickCount-1).getData().getClose());
						
						this.last_signal_type = 1;//set last signal as sell(0)		
					}	
					this.buffer_signal_type = -1;//reset buffer
				}else {
					this.buffer_signal_type = 1;// set buffer as positive brick
				}
				
			}else {//negetive brick
				if(this.buffer_signal_type == 0){  //buffer is negative	
					if(this.last_signal_type != 0) { //last signal not sell(0) --> last signal buy(1) or null(-1)
						//sell Signal
						registerSellOpt(ohlc.get(brickCount-1).getData().getClose(),new Date(ohlc.get(brickCount-1).getData().getTimestamp()));
						
						System.out.println("Signal:Sell :: last signal is not sell at"+new Date(ohlc.get(brickCount-1).getData().getTimestamp())+" :: Single Brick generated"+brickCount +" at price:"+ohlc.get(brickCount-1).getData().getClose());
						this.last_signal_type = 0;//set last signal as sell(0)		
					}	
					this.buffer_signal_type = -1;//reset buffer
				}else {
					this.buffer_signal_type = 0;// set buffer as negative brick
				}	
			}
		}//end of brick count 1
		if(brickCount >1) {
			
			if(current_signal_type == 1) {//current is positive brick
				if(this.last_signal_type != 1) { //last signal not buy(1) --> last signal sell(0) or null(-1)
					//Buy Signal
					registerBuyOpt(ohlc.get(brickCount-1).getData().getClose(),new Date(ohlc.get(brickCount-1).getData().getTimestamp()));
					System.out.println("Signal:Buy at"+new Date(ohlc.get(brickCount-1).getData().getTimestamp())+":: More than 1 Brick generated"+brickCount +" at price"+ohlc.get(brickCount-1).getData().getClose());
					this.last_signal_type = 1;//set last signal as Buy(1)
				}
			}else {//negetive brick
				if(this.last_signal_type != 0) { //last signal not sell(0) last signal buy(1) or null(-1)
					//Sell Signal
					registerSellOpt(ohlc.get(brickCount-1).getData().getClose(),new Date(ohlc.get(brickCount-1).getData().getTimestamp()));
					//System.out.println("Signal:Sell at "+new Date(ohlc.get(brickCount-1).getData().getTimestamp())+":: More than 1 Brick generated "+brickCount +"at price:"+ohlc.get(brickCount-1).getData().getClose());
					
					this.last_signal_type = 0;//set last signal as Sell(0)
				}
			}
			this.buffer_signal_type = -1;//reset buffer
		}
		//no brick do nothing		
	}
	private int getBrickType(Ticker brick) {
		//return 1 for positive brick -1 for negetive
		if(brick.getData().getClose() > brick.getData().getOpen())return 1;
		return 0;
	}
	public double calculateProfit() {
		if(this.orderCount>1 && orderCount>lastCalculatedTrade) {
			String firstOrderSet = (String)(orders.column(1).keySet().toArray())[0];
			boolean isBuy = firstOrderSet.startsWith("Buy");
			
			if(isBuy) {
				if(this.orderCount %2 ==0) {
					this.tempProfit += (Double)(orders.column(this.orderCount).values().toArray()[0]) - (Double)(orders.column(this.orderCount-1).values().toArray()[0]) ;
				}else {
					this.tempProfit += (Double)(orders.column(this.orderCount-1).values().toArray()[0]) - (Double)(orders.column(this.orderCount).values().toArray()[0]) ;
					
				}
				}else {
				if(this.orderCount%2 ==0) {
					this.tempProfit +=  (Double)(orders.column(this.orderCount-1).values().toArray()[0]) - (Double)(orders.column(this.orderCount).values().toArray()[0]);	
				}else {
					this.tempProfit +=  (Double)(orders.column(this.orderCount).values().toArray()[0]) - (Double)(orders.column(this.orderCount-1).values().toArray()[0]);	
					
				}
				this.lastCalculatedTrade = this.orderCount;
			}		
			}
		System.out.println("Profit till now:"+tempProfit+":: oc:"+this.orderCount);
		return this.tempProfit;
	}
	
	public ArrayList<Ticker> getRenkoBricks(){
		return this.rb;
	}
	public Set<Cell<String, Integer, Double>> getTransactions(){
		return this.orders.cellSet();
	}
	
	public String getTickerName() {
		return tickerName;
	}
	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}
	public int getSessionType() {
		return sessionType;
	}
	public void setSessionType(int sessionType) {
		this.sessionType = sessionType;
	}
	public float getBrickSize() {
		return brickSize;
	}
	public void setBrickSize(float brickSize) {
		this.brickSize = brickSize;
	}
	public void resetTempProfit() {
		this.tempProfit=0;
	}
	public void reset() {
		this.brickSize = 10;//make it configurable
		this.orderCount = 0;
		orders = HashBasedTable.create();
		rb = new ArrayList<Ticker>();
		this.last_signal_type = -1;
		this.buffer_signal_type = -1;
		this.lastCalculatedTrade =0;
		this.tempProfit = 0.0;
	}
	
}
