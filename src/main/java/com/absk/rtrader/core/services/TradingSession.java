package com.absk.rtrader.core.services;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.indicators.NRenko;
import com.absk.rtrader.core.models.OHLC;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.utils.ConfigUtil;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.constants.UpstoxExchangeTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;
import com.absk.rtrader.exchange.upstox.constants.UpstoxStrikeTypeConstants;
import com.absk.rtrader.exchange.upstox.services.UpstoxFeedServiceImpl;
import com.absk.rtrader.exchange.upstox.services.UpstoxSLAgent;
import com.absk.rtrader.exchange.upstox.services.UpstoxSLService;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TradingSession {

	private static final Logger log = LoggerFactory.getLogger(TradingSession.class);

	@Autowired
	private NRenko renko;

	@Autowired
	private TickerUtil tickerUtil;

	@Autowired
	private UpstoxSLService slService;

	@Autowired
	private TimeframeTransformationService tss;

	@Autowired
	private ConfigUtil configUtil;

	@Autowired
	private UpstoxFeedServiceImpl feedService;

	String tickerName;
	String exchange;
	int sessionType;// 0 -->realtime 1 --> optimization TODO: change to enum
	int timeFrame;// no of ticks per candle
	float brickSize;
	Table<String, Integer, BigDecimal> orders;
	double profit;
	int orderCount;
	int last_signal_type;
	ArrayList<Ticker> rb;
	int buffer_signal_type;
	int lastCalculatedTrade;
	double tempProfit;
	int ignoreSignalCount;

	public int getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(int timeFrame) {
		tss.setDestinationTimeframe(timeFrame);
		this.timeFrame = timeFrame;
	}

	public TradingSession(String tickerName, int sessionType, float brickSize) {
		this.tickerName = tickerName;
		this.sessionType = sessionType;
		this.brickSize = brickSize;
		this.orderCount = 0;
		orders = HashBasedTable.create();
		rb = new ArrayList<Ticker>();
		this.last_signal_type = -1;
		this.buffer_signal_type = -1;
		this.lastCalculatedTrade = 0;
		this.tempProfit = 0.0;
		this.timeFrame = 1;
	}

	public TradingSession(String tickerName, String exchange, int ignoreSignalCount, int sessionType, float brickSize) {
		this.tickerName = tickerName;
		this.sessionType = sessionType;
		this.brickSize = brickSize;
		this.orderCount = 0;
		orders = HashBasedTable.create();
		rb = new ArrayList<Ticker>();
		this.last_signal_type = -1;
		this.buffer_signal_type = -1;
		this.lastCalculatedTrade = 0;
		this.tempProfit = 0.0;
		this.timeFrame = 1;
		this.exchange = exchange;
		this.ignoreSignalCount = ignoreSignalCount;
	}

	public TradingSession() {
		this.brickSize = 10;// make it configurable
		this.orderCount = 0;
		orders = HashBasedTable.create();
		rb = new ArrayList<Ticker>();
		this.last_signal_type = -1;
		this.buffer_signal_type = -1;
		this.lastCalculatedTrade = 0;
		this.tempProfit = 0.0;
		this.timeFrame = 1;
	}

	void registerBuyOpt(double price, Date date) {
		// if orderCount < ignore signal count
		if (orderCount <= this.ignoreSignalCount) {
			log.info("BUY SIGNAL IGNORED! Current Signal Count: " + orderCount);
			orderCount++;
			return;
		}
		orderCount++;
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss");
		orders.put("Buy at " + dateFormat.format(date), orderCount, new BigDecimal(price));
		if (!configUtil.isTradingEnabled()) { // if trading disabled --> paper trading enabled
			if (slService.isAnyAgentFree("CALL")) {

				UpstoxSLAgent slAgent = slService.getFreeAgent("CALL");
				String strikeSymbol = tickerUtil.getClosestStrikePrice(price, 100, UpstoxStrikeTypeConstants.CALL);
				log.info("SL Agent free. Executing Paper trade to buy CALL :" + strikeSymbol);
				BigDecimal initPriceBD = feedService.getLTPofInstrument(strikeSymbol,
						UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS);
				slAgent.setParams(strikeSymbol, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS, initPriceBD, 20);
				feedService.subscribeToTicker(strikeSymbol, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS,
						UpstoxFeedTypeConstants.FEEDTYPE_FULL);

				slAgent.start();
			} else {
				log.info(" No SL Agent free. Ignoring BUY signal at :" + price);

			}
		} else {
			// real trade
		}
	}

	void registerSellOpt(double price, Date date) {

		// ignore signal
		if (orderCount <= this.ignoreSignalCount) {
			log.info("SELL SIGNAL IGNORED! Current Signal Count: " + orderCount);
			orderCount++;
			return;
		}

		orderCount++;
		DateFormat dateFormat = new SimpleDateFormat("dd-MM-YYYY hh:mm:ss");
		orders.put("Sell at " + dateFormat.format(date), orderCount, new BigDecimal(price));
		if (!configUtil.isTradingEnabled()) { // if trading disabled --> paper trading enabled
			if (slService.isAnyAgentFree("PUT")) {

				UpstoxSLAgent slAgent = slService.getFreeAgent("PUT");
				String strikeSymbol = tickerUtil.getClosestStrikePrice(price, 100, UpstoxStrikeTypeConstants.PUT);
				log.info("SL Agent free. Executing Paper trade to buy PUT :" + strikeSymbol);
				BigDecimal initPriceBD = feedService.getLTPofInstrument(strikeSymbol,
						UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS);
				slAgent.setParams(strikeSymbol, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS, initPriceBD, 20);
				feedService.subscribeToTicker(strikeSymbol, UpstoxExchangeTypeConstants.NSE_FUTURE_AND_OPTIONS,
						UpstoxFeedTypeConstants.FEEDTYPE_FULL);
				slAgent.start();
			} else {
				log.info(" No SL Agent free. Ignoring SELL signal at :" + price);
			}
		} else {
			// real trade
		}
	}

	double getProfit() {
		return this.tempProfit;
	}

	public void instantiateSLAgents() {
		slService.instantiateAgents();
	}

	public void setRenkoBrickSize(float bs) {
		renko.setBrickSize(bs);
	}

	public ArrayList<Double> processAllData(OHLC[] data, double bs) {

		ArrayList<Ticker> sourceTickArr = tickerUtil.toTickerArray(data);
		renko.setBrickSize(bs);
		renko.buildHistory(sourceTickArr, "open");
		ArrayList<Double> out = renko.getRenkoPrices();
		renko.reset();
		return out;

	}

	/*
	 * public void processData(ArrayList<Ticker> ohlc) {//TODO: change this to
	 * ticker int brickCount = ohlc.size(); if(ohlc.size()<1)return;
	 * 
	 * for(int i=0;i<brickCount;i++) {
	 * //System.out.println("BrickCount:"+i+"Close: "+ohlc.get(i).getData().getClose
	 * ()); }
	 * 
	 * int current_signal_type = getBrickType(ohlc.get(0));//1 --> positive brick 0
	 * --> negetive brick if(brickCount ==1) { if(current_signal_type == 1)
	 * {//positive brick if(this.buffer_signal_type == 1){ //buffer is positive
	 * if(this.last_signal_type != 1) { //last signal not buy(1) --> last signal
	 * sell(0) or null(-1) //buy Signal
	 * registerBuyOpt(ohlc.get(brickCount-1).getData().getClose(),new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp()));
	 * System.out.println("Signal:Buy :: at"+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +":: Single Brick generated"+brickCount
	 * +" at price:"+ohlc.get(brickCount-1).getData().getClose());
	 * log.info("Signal:Buy :: at"+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +":: Single Brick generated"+brickCount
	 * +" at price:"+ohlc.get(brickCount-1).getData().getClose());
	 * this.last_signal_type = 1;//set last signal as sell(0) }
	 * this.buffer_signal_type = -1;//reset buffer }else { this.buffer_signal_type =
	 * 1;// set buffer as positive brick }
	 * 
	 * }else {//negetive brick if(this.buffer_signal_type == 0){ //buffer is
	 * negative if(this.last_signal_type != 0) { //last signal not sell(0) --> last
	 * signal buy(1) or null(-1) //sell Signal
	 * registerSellOpt(ohlc.get(brickCount-1).getData().getClose(),new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp()));
	 * log.info("Signal:Sell :: last signal is not sell at"+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +" :: Single Brick generated"+brickCount
	 * +" at price:"+ohlc.get(brickCount-1).getData().getClose());
	 * 
	 * System.out.println("Signal:Sell :: last signal is not sell at"+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +" :: Single Brick generated"+brickCount
	 * +" at price:"+ohlc.get(brickCount-1).getData().getClose());
	 * this.last_signal_type = 0;//set last signal as sell(0) }
	 * this.buffer_signal_type = -1;//reset buffer }else { this.buffer_signal_type =
	 * 0;// set buffer as negative brick } } }//end of brick count 1 if(brickCount
	 * >1) { //fetch last brick type current_signal_type =
	 * getBrickType(ohlc.get(brickCount-1));//1 --> positive brick 0 --> negetive
	 * brick
	 * 
	 * if(current_signal_type == 1) {//current is positive brick
	 * if(this.last_signal_type != 1) { //last signal not buy(1) --> last signal
	 * sell(0) or null(-1) //Buy Signal
	 * registerBuyOpt(ohlc.get(brickCount-1).getData().getClose(),new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp()));
	 * System.out.println("Signal:Buy at"+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +":: More than 1 Brick generated"+brickCount
	 * +" at price"+ohlc.get(brickCount-1).getData().getClose());
	 * log.info("Signal:Buy at"+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +":: More than 1 Brick generated"+brickCount
	 * +" at price"+ohlc.get(brickCount-1).getData().getClose());
	 * 
	 * this.last_signal_type = 1;//set last signal as Buy(1) } }else {//negetive
	 * brick if(this.last_signal_type != 0) { //last signal not sell(0) last signal
	 * buy(1) or null(-1) //Sell Signal
	 * registerSellOpt(ohlc.get(brickCount-1).getData().getClose(),new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp()));
	 * System.out.println("Signal:Sell at "+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +":: More than 1 Brick generated "+brickCount
	 * +"at price:"+ohlc.get(brickCount-1).getData().getClose());
	 * log.info("Signal:Sell at "+new
	 * Date(ohlc.get(brickCount-1).getData().getTimestamp())
	 * +":: More than 1 Brick generated "+brickCount
	 * +"at price:"+ohlc.get(brickCount-1).getData().getClose());
	 * 
	 * this.last_signal_type = 0;//set last signal as Sell(0) } }
	 * this.buffer_signal_type = -1;//reset buffer } //no brick do nothing }
	 */

	public void processData(ArrayList<Ticker> ohlc) {// TODO: change this to ticker
		int brickCount = ohlc.size();
		if (ohlc.size() < 1)
			return;
		
		int current_signal_type = getBrickType(ohlc.get(0));// 1 --> positive brick 0 --> negetive brick
		if (brickCount == 1) {
			if (current_signal_type == 1) {// positive brick
					if (this.last_signal_type != 1) { // last signal not buy(1) --> last signal sell(0) or null(-1)
						// buy Signal
						registerBuyOpt(ohlc.get(brickCount - 1).getData().getClose(),
								new Date(ohlc.get(brickCount - 1).getData().getTimestamp()));
						System.out.println(
								"Signal:Buy :: at" + new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
										+ ":: Single Brick generated" + brickCount + " at price:"
										+ ohlc.get(brickCount - 1).getData().getClose());
						log.info("Signal:Buy :: at" + new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
								+ ":: Single Brick generated" + brickCount + " at price:"
								+ ohlc.get(brickCount - 1).getData().getClose());
						this.last_signal_type = 1;// set last signal as sell(0)
					}

			} else {// negetive brick
					if (this.last_signal_type != 0) { // last signal not sell(0) --> last signal buy(1) or null(-1)
						// sell Signal
						registerSellOpt(ohlc.get(brickCount - 1).getData().getClose(),
								new Date(ohlc.get(brickCount - 1).getData().getTimestamp()));
						log.info("Signal:Sell :: last signal is not sell at"
								+ new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
								+ " :: Single Brick generated" + brickCount + " at price:"
								+ ohlc.get(brickCount - 1).getData().getClose());

						System.out.println("Signal:Sell :: last signal is not sell at"
								+ new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
								+ " :: Single Brick generated" + brickCount + " at price:"
								+ ohlc.get(brickCount - 1).getData().getClose());
						this.last_signal_type = 0;// set last signal as sell(0)
					}
			}
		} // end of brick count 1
		if (brickCount > 1) {
			// fetch last brick type
			current_signal_type = getBrickType(ohlc.get(brickCount - 1));// 1 --> positive brick 0 --> negetive brick

			if (current_signal_type == 1) {// current is positive brick
				if (this.last_signal_type != 1) { // last signal not buy(1) --> last signal sell(0) or null(-1)
					// Buy Signal
					registerBuyOpt(ohlc.get(brickCount - 1).getData().getClose(),
							new Date(ohlc.get(brickCount - 1).getData().getTimestamp()));
					System.out.println("Signal:Buy at" + new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
							+ ":: More than 1 Brick generated" + brickCount + " at price"
							+ ohlc.get(brickCount - 1).getData().getClose());
					log.info("Signal:Buy at" + new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
							+ ":: More than 1 Brick generated" + brickCount + " at price"
							+ ohlc.get(brickCount - 1).getData().getClose());

					this.last_signal_type = 1;// set last signal as Buy(1)
				}
			} else {// negetive brick
				if (this.last_signal_type != 0) { // last signal not sell(0) last signal buy(1) or null(-1)
					// Sell Signal
					registerSellOpt(ohlc.get(brickCount - 1).getData().getClose(),
							new Date(ohlc.get(brickCount - 1).getData().getTimestamp()));
					System.out.println("Signal:Sell at " + new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
							+ ":: More than 1 Brick generated " + brickCount + "at price:"
							+ ohlc.get(brickCount - 1).getData().getClose());
					log.info("Signal:Sell at " + new Date(ohlc.get(brickCount - 1).getData().getTimestamp())
							+ ":: More than 1 Brick generated " + brickCount + "at price:"
							+ ohlc.get(brickCount - 1).getData().getClose());

					this.last_signal_type = 0;// set last signal as Sell(0)
				}
			}
		}
		// no brick do nothing
	}

	public void ExecuteAlternate(ArrayList<Ticker> ohlc) {
		int brickCount = ohlc.size();
		if (brickCount < 1)
			return;

		int current_signal_type = getBrickType(ohlc.get(0));// 1 --> positive brick 0 --> negetive brick

	}

	private int getBrickType(Ticker brick) {
		// return 1 for positive brick -1 for negetive
		if (brick.getData().getClose() > brick.getData().getOpen())
			return 1;
		return 0;
	}

	public double calculateProfit() {
		if (this.orderCount > 1 && orderCount > lastCalculatedTrade) {
			String firstOrderSet = (String) (orders.column(1).keySet().toArray())[0];
			boolean isBuy = firstOrderSet.startsWith("Buy");

			if (isBuy) {
				if (this.orderCount % 2 == 0) {
					this.tempProfit += (Double) (orders.column(this.orderCount).values().toArray()[0])
							- (Double) (orders.column(this.orderCount - 1).values().toArray()[0]);
				} else {
					this.tempProfit += (Double) (orders.column(this.orderCount - 1).values().toArray()[0])
							- (Double) (orders.column(this.orderCount).values().toArray()[0]);

				}
			} else {
				if (this.orderCount % 2 == 0) {
					this.tempProfit += (Double) (orders.column(this.orderCount - 1).values().toArray()[0])
							- (Double) (orders.column(this.orderCount).values().toArray()[0]);
				} else {
					this.tempProfit += (Double) (orders.column(this.orderCount).values().toArray()[0])
							- (Double) (orders.column(this.orderCount - 1).values().toArray()[0]);

				}
				this.lastCalculatedTrade = this.orderCount;
			}
		}
		System.out.println("Profit till now:" + tempProfit + ":: oc:" + this.orderCount);
		return this.tempProfit;
	}

	public ArrayList<Ticker> getRenkoBricks() {
		return this.rb;
	}

	public Set<Cell<String, Integer, BigDecimal>> getTransactions() {
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
		this.tempProfit = 0;
	}

	public void reset() {
		this.brickSize = 10;// make it configurable
		this.orderCount = 0;
		orders = HashBasedTable.create();
		rb = new ArrayList<Ticker>();
		this.last_signal_type = -1;
		this.buffer_signal_type = -1;
		this.lastCalculatedTrade = 0;
		this.tempProfit = 0.0;
		this.timeFrame = 1;
	}

}