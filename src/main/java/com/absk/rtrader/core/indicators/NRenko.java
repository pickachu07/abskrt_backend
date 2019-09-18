package com.absk.rtrader.core.indicators;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.models.Ticker;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class NRenko {

	private ArrayList<Double> sourcePrices;
	private ArrayList<Double> renkoPrices;
	private ArrayList<Integer> renkoDirections;
	private double brickSize;
	private int lastRenkoArraySize;
	
	public NRenko() {
		this.brickSize = 10;
		sourcePrices = new ArrayList<Double>();
		renkoPrices = new ArrayList<Double>();
		renkoDirections = new ArrayList<Integer>();
		lastRenkoArraySize = 0;
	}
	
	public NRenko(double brickSize) {
		this.brickSize = brickSize;
		sourcePrices = new ArrayList<Double>();
		renkoPrices = new ArrayList<Double>();
		renkoDirections = new ArrayList<Integer>();
		lastRenkoArraySize = 0;
	}
	
	public int buildHistory(ArrayList<Ticker> tickerArray,String priceType) {
		
		if(tickerArray.size() > 0) {
			this.sourcePrices = getPriceArrayByPriceType(tickerArray, priceType);
			this.renkoPrices.add(sourcePrices.get(0));
			this.renkoDirections.add(0);
			for(int count = 1; count < sourcePrices.size() ; count++ ) {
				this.renkoRule(sourcePrices.get(count));
			}
		}
		
		return this.renkoPrices.size();
	}
	
	//Getting next renko value for last price
	
	public int doNext(double currentPrice) {
		if(this.renkoPrices.size() == 0) {
			
			this.sourcePrices.add(currentPrice);
			this.renkoPrices.add(currentPrice);
			this.renkoDirections.add(0);
			return 1;
			
		}else {
			
			this.sourcePrices.add(currentPrice);
			return this.renkoRule(currentPrice);
		
		}
	}
	
	public ArrayList<Double> historicalDoNext(double currentPrice) {
		if(this.renkoPrices.size() == 0) {
			
			this.sourcePrices.add(currentPrice);
			this.renkoPrices.add(currentPrice);
			this.renkoDirections.add(0);
			ArrayList<Double> openingPrice = new ArrayList<Double>();
			openingPrice.add(currentPrice);
			return openingPrice;
			
		}else {
			
			this.sourcePrices.add(currentPrice);
			return this.historicalRenkoRule(currentPrice);
		
		}
	}
	
	
	public ArrayList<Double> getSourcePrices(){
		return this.sourcePrices;
	}
	
	public ArrayList<Double> getRenkoPrices(){
		if (this.renkoPrices.size() > this.lastRenkoArraySize) {
			lastRenkoArraySize = this.renkoPrices.size();
			return this.renkoPrices;
		}
		return null;
	}
	
	public double getBrickSize() {
		return this.brickSize;
	}
	
	public void setBrickSize(double bs) {
		this.brickSize = bs;
	}
	
	public int renkoRule(double currentPrice) {
		
		int gap = (int)((currentPrice - lastRenkoPrice())/this.brickSize);
		boolean isNewBrick = false;
		int startBrick = 0;
		int numNewBricks = 0;
		
		//When we have some gap in prices
		if(gap != 0) {
			//Forward any direction (up or down)
			if( (gap > 0 && lastRenkoDirection()>=0) || (gap < 0 && lastRenkoDirection() <= 0)) {
				numNewBricks = gap;
				isNewBrick = true;
				startBrick = 0;
			}
			// Backward direction (up -> down or down -> up)
			else if(Math.abs(gap) >= 2) {
				numNewBricks = gap;
				numNewBricks -= signOfDirection(gap);
				startBrick = 2;
				isNewBrick = true;
				
				this.renkoPrices.add(lastRenkoPrice() + (this.brickSize * signOfDirection(gap)));
				this.renkoDirections.add(signOfDirection(lastRenkoDirection()));
			}
			
			if(isNewBrick) {
				for(long count = startBrick; count < Math.abs(gap); count++) {
					this.renkoPrices.add(lastRenkoPrice() + (this.brickSize * signOfDirection(gap)));
					this.renkoDirections.add(signOfDirection(lastRenkoDirection()));
				}
			}
			return numNewBricks;
			
		}
		
		return numNewBricks;
	}
	
	public ArrayList<Double> historicalRenkoRule(double currentPrice){
		int gap = (int)((currentPrice - lastRenkoPrice())/this.brickSize);
		boolean isNewBrick = false;
		int startBrick = 0;
		int numNewBricks = 0;
		ArrayList<Double> tempRenkoBricks = new ArrayList<Double>();
		//When we have some gap in prices
		if(gap != 0) {
			//Forward any direction (up or down)
			if( (gap > 0 && lastRenkoDirection()>=0) || (gap < 0 && lastRenkoDirection() <= 0)) {
				numNewBricks = gap;
				isNewBrick = true;
				startBrick = 0;
			}
			// Backward direction (up -> down or down -> up)
			else if(Math.abs(gap) >= 2) {
				numNewBricks = gap;
				numNewBricks -= signOfDirection(gap);
				startBrick = 2;
				isNewBrick = true;
				
				this.renkoPrices.add(lastRenkoPrice() + (this.brickSize * signOfDirection(gap)));
				tempRenkoBricks.add(lastRenkoPrice() + (this.brickSize * signOfDirection(gap)));
				this.renkoDirections.add(signOfDirection(lastRenkoDirection()));
			}
			
			if(isNewBrick) {
				for(long count = startBrick; count < Math.abs(gap); count++) {
					this.renkoPrices.add(lastRenkoPrice() + (this.brickSize * signOfDirection(gap)));
					tempRenkoBricks.add(lastRenkoPrice() + (this.brickSize * signOfDirection(gap)));
					this.renkoDirections.add(signOfDirection(lastRenkoDirection()));
				}
			}
			return tempRenkoBricks;
			
		}
		
		return null;
	}
	
	public ArrayList<Double> getLastRenkoBricks(int n){
		List<Double>renkoPriceList = renkoPrices.subList(Math.max(renkoPrices.size() - n, 0), renkoPrices.size());
		ArrayList<Double> renkoPriceArrayList = new ArrayList<Double>();
		renkoPriceArrayList.addAll(renkoPriceList);
		return renkoPriceArrayList;
	}
	
	
	
	private ArrayList<Double> getPriceArrayByPriceType(ArrayList<Ticker> tickerArray, String priceType) {
		
		if(tickerArray.size()<1)return null;
		ArrayList<Double> priceArray = new ArrayList<Double>();
		double priceValue;
		for(int count = 0; count < tickerArray.size() ; count++) {
			if(priceType.equalsIgnoreCase("close")) {
				priceValue = tickerArray.get(count).getData().getClose();
			}
			else if(priceType.equalsIgnoreCase("open")) {
				priceValue = tickerArray.get(count).getData().getOpen();
			}
			else {
				priceValue = tickerArray.get(count).getData().getOpen();
			}
			priceArray.add(priceValue);
		}
		return priceArray;
	}
	
	
	private double lastRenkoPrice() {
		if(this.renkoPrices.size()>0)return this.renkoPrices.get(this.renkoPrices.size() - 1);
		return 0;
	}
	
	private int lastRenkoDirection() {
		if(this.renkoDirections.size()>0)return this.renkoDirections.get(this.renkoDirections.size() - 1);
		return 0;
	}
	
	private int signOfDirection(long direction) {
		if (direction == 0) return 0;
		if (direction < 0) return -1;
		return 1;
	}
	
	public void reset() {
		this.brickSize=10;
		this.renkoDirections = null;
		this.renkoPrices = null;
		this.sourcePrices = null;
		sourcePrices = new ArrayList<Double>();
		renkoPrices = new ArrayList<Double>();
		renkoDirections = new ArrayList<Integer>();
	}
}
