package com.absk.rtrader.indicators;

import java.util.ArrayList;
import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.model.TickerData;

@Component
@Scope(value = "singleton")
public class Renko {

	private int brickSize;
	private Renko instance;
	
	private ArrayList<Double> ro;
	private ArrayList<Double> rc;
	private double buf;
	private double cur;
	private int bCount;
	private double currRenOpen;
	
	
	private Renko() {
		ro = new ArrayList<Double>();
		rc = new ArrayList<Double>();
		this.brickSize = -1;
		this.bCount = -1;
		this.cur = 0;
		this.currRenOpen = 0;
	}
	
	public Renko getInstance() {
		if(instance ==  null) {
			instance = new Renko();
			return instance;
		}
		return instance;
	}
	
	
	public void setBrickSize(int bs) {
		this.brickSize = bs;
	}
	public int getBrickSize() {
		return this.brickSize;
	}
	
	public ArrayList<Ticker> drawRenko(Ticker tick,int bs) {
		this.brickSize = bs;
		TickerData data = tick.getData();
		double cOpen = data.getOpen();
		double cClose = data.getClose();
		int tempRenkoBrickCount = 0;
		ArrayList<Ticker> tickerArray = new ArrayList<Ticker>();
		
		if (ro.size() == 0) {//first renko brick
			this.bCount = 0;
			currRenOpen = cOpen;
			ro.add(this.bCount, currRenOpen);
		}
		this.cur = cClose - currRenOpen;
		this.buf += this.cur;
		
		if( Math.abs(buf) >= this.brickSize && buf > 0) {//positive renko brick
			for(int i=0; i< (Math.abs(this.buf)/this.brickSize);i++) {
				
				double currRenClose = currRenOpen+this.brickSize;
				//create current renko brick in ticker
				TickerData currData = new TickerData(currRenOpen,currRenClose,currRenOpen,currRenClose,0,new Date().getTime(),"NSE_EQ","",0,0);
				Ticker currTick = new Ticker("Renko", currData,new Date());
				tickerArray.add(tempRenkoBrickCount, currTick);
				//increase temp renko brick count
				tempRenkoBrickCount++;
				
				rc.add(this.bCount,currRenClose);
				this.bCount++;
				currRenOpen = currRenClose;
				ro.add(this.bCount,currRenOpen);
			}
			this.buf = this.buf % this.brickSize;
		}
		if( Math.abs(buf) >= this.brickSize && buf < 0) {//negetive renko brick
			for(int i=0; i< (Math.abs(this.buf)/this.brickSize);i++){
				double currRenClose = currRenOpen-this.brickSize;
				//create current renko brick in ticker
				TickerData currData = new TickerData(currRenOpen,currRenOpen,currRenClose,currRenClose,0,new Date().getTime(),"NSE_EQ","",0,0);
				Ticker currTick = new Ticker("Renko", currData,new Date());
				tickerArray.add(tempRenkoBrickCount, currTick);
				//increase temp renko brick count
				tempRenkoBrickCount++;
				
				rc.add(this.bCount,currRenClose);
				this.bCount++;
				currRenOpen = currRenClose;
				ro.add(this.bCount,currRenOpen);
			}
			this.buf = this.buf % this.brickSize;
		}
		//System.out.println("RENKO CLOSE:: B count:"+rc.size()+":: Brick_size:: "+this.brickSize);
		/*for(int item=0;item < rc.size();item++) {
			System.out.println(rc.get(item));
		}*/
		
		return tickerArray; 
	}
	
	
	
	
}
