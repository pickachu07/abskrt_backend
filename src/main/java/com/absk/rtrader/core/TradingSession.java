package com.absk.rtrader.core;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.absk.rtrader.indicators.Renko;
import com.absk.rtrader.model.OHLC;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.utils.TickerUtil;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;


@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TradingSession {

	@Autowired
	private Renko renko;
	
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
	
	void calculatePnL() {
		
	}
	
	public void processAllData(OHLC[] ohlc) {
		//draw renko --> binary array
		Renko renkoInstance = renko.getInstance();
		int prev_len_bta = 0;
		int prev_last_elem_bta = -1;
		int last_signal = -1;
		 
		for(int i=0;i<ohlc.length;i++) {
			ArrayList<Integer> bta = new ArrayList<Integer>();
			rb.addAll(renkoInstance.drawRenko(tickerUtil.convertToTicker(ohlc[i]), this.brickSize));
			bta = renkoInstance.getRenkoBrickTypeArray();
			//Collections.reverse(bta);
			Object[] btarr = bta.toArray();
			//System.out.println("renko bricktype:"+ Arrays.toString(bta.toArray()));
			
			int len_bta = bta.toArray().length;
			
			if(len_bta > prev_len_bta) {//if new bricks created
				int last_elem = (int)btarr[0];
				
				if(prev_last_elem_bta == last_elem) {//two consecutive same bricks 
					
					//prevent similar conecutive signals
					if(last_signal != 1 && last_elem == 1) {
						//buy signal
						//registerSellOpt(rb.get(rb.size()-1).getData().getClose());
						last_signal = 1;
					}
					if(last_signal != 0 && last_elem == 0) {
						//sell_signal
						//registerBuyOpt(rb.get(rb.size()-1).getData().getClose());
						last_signal = 0;
					}
				}
				prev_last_elem_bta = last_elem;
			}
			prev_len_bta = len_bta;
			
		}
	}
	
	public void processData(ArrayList<Ticker> ohlc) {
		int brickCount =  ohlc.size();
		if(ohlc.size()<1)return;
		for(int i=0;i<brickCount;i++) {
			System.out.println("BrickCount:"+i+"Close: "+ohlc.get(i).getData().getClose());
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
					System.out.println("Signal:Sell at "+new Date(ohlc.get(brickCount-1).getData().getTimestamp())+":: More than 1 Brick generated "+brickCount +"at price:"+ohlc.get(brickCount-1).getData().getClose());
					
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
					this.tempProfit += (double)(orders.column(this.orderCount).values().toArray()[0]) - (double)(orders.column(this.orderCount-1).values().toArray()[0]) ;
				}else {
					this.tempProfit += (double)(orders.column(this.orderCount-1).values().toArray()[0]) - (double)(orders.column(this.orderCount).values().toArray()[0]) ;
					
				}
				}else {
				if(this.orderCount%2 ==0) {
					this.tempProfit +=  (double)(orders.column(this.orderCount-1).values().toArray()[0]) - (double)(orders.column(this.orderCount).values().toArray()[0]);	
				}else {
					this.tempProfit +=  (double)(orders.column(this.orderCount).values().toArray()[0]) - (double)(orders.column(this.orderCount-1).values().toArray()[0]);	
					
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
	
}