package com.absk.rtrader.exchange.upstox.services;


import java.util.ArrayList;
import java.util.concurrent.Flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.indicators.NRenko;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.repositories.TickerRepository;
import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.core.utils.ConfigUtil;
import com.absk.rtrader.core.utils.TickerUtil;
import com.absk.rtrader.exchange.upstox.constants.UpstoxExchangeTypeConstants;
import com.absk.rtrader.exchange.upstox.exceptions.WebSocketError;
import com.absk.rtrader.exchange.upstox.utils.UpstoxTickerUtils;
import com.github.rishabh9.riko.upstox.websockets.MessageSubscriber;
import com.github.rishabh9.riko.upstox.websockets.messages.BinaryMessage;
import com.github.rishabh9.riko.upstox.websockets.messages.ClosingMessage;
import com.github.rishabh9.riko.upstox.websockets.messages.ConnectedMessage;
import com.github.rishabh9.riko.upstox.websockets.messages.DisconnectedMessage;
import com.github.rishabh9.riko.upstox.websockets.messages.ErrorMessage;
import com.github.rishabh9.riko.upstox.websockets.messages.TextMessage;
import com.github.rishabh9.riko.upstox.websockets.messages.WebSocketMessage;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UpstoxWebSocketSubscriber implements MessageSubscriber {

    private Flow.Subscription subscription;
    
    private ArrayList<UpstoxSLAgent> Listners;
    
    @Autowired
    private TradingSession tradingSession;
    
    @Autowired
    private NRenko renko;
    
    private ArrayList<Ticker> tickArr;
    
    private int lastRenkoArrayLength;
    
    @Autowired
    private SimpMessagingTemplate webSocketTemplate;
    
    @Autowired
    private TickerRepository tickerRepo;

    @Autowired
    UpstoxSLService slService;
    
    @Autowired
	ConfigUtil config;
    
    @Autowired
    TickerUtil tickerUtil;
    
    
    public void onSubscribe(final Flow.Subscription subscription) {
        log.info("Subscribed! Ready to receive messages!");
        this.subscription = subscription;
        this.subscription.request(1);
        //instantiateTradingSession(UpstoxSymbolNames.BANK_NIFTY, 4.0F);//Default
        slService.instantiateAgents();
        Listners = new ArrayList<UpstoxSLAgent>();
        lastRenkoArrayLength = 0;
    }
    
    public void subscribeListner(UpstoxSLAgent agent) {
    	if(Listners.size() >= config.getSLAgentPoolSize()) {
    		//something wrong as number of listners cannot be more than no of agents in pool
    		return;
    	}
    	Listners.add(agent);
    }

    public void onNext(WebSocketMessage item) {
        if (item instanceof BinaryMessage) {
            
            String itemAsString = ((BinaryMessage) item).getMessageAsString();
            log.info("Binary Message: {}", itemAsString);
            /*
             * Old way of Renko calculation
             * 
             * Ticker tick = parseTicker(itemAsString);
            log.info("Binary Message: {}", itemAsString);
            log.info("Parsed TickerData: {}", tick.getData().toString());
            
            tickerRepo.save(tick);
            
            tickArr = renko.getInstance().drawRenko(tick, tradingSession.getBrickSize());
            tradingSession.processData(tickArr);
            
            */
            
            //new renko
            
            UpstoxTickerUtils utils = new UpstoxTickerUtils();
            Ticker tick = utils.filterTickerBySymbol(itemAsString, tradingSession.getTickerName());
            
            double currentClose = tick.getData().getClose();
            renko.setBrickSize(tradingSession.getBrickSize());
            renko.doNext(currentClose);
            
            tickArr = tickerUtil.renkoPricesToTickerArray(renko.getRenkoPrices(),UpstoxExchangeTypeConstants.NSE_INDEX,tradingSession.getTickerName());
            
            
            
            //send ohlc to ohlc_stream
            webSocketTemplate.convertAndSend("/topic/ohlc_stream", tick);
            
            ArrayList<Ticker> newBricks = getNewBricks(tickArr);
            
            
            
            
            if(newBricks != null && newBricks.size() > 0) {
            	
            	tradingSession.processData(newBricks);
            	
            	//send renko brick to /topic/renko_stream
                for(int i=0;i<newBricks.size();i++){
                    //log.info("The tick is now {}", tick);
                    webSocketTemplate.convertAndSend("/topic/ticker_stream", newBricks.get(i));
                }
                lastRenkoArrayLength = tickArr.size();
            }
            
            
            //System.out.println("Temp Profit:"+tradingSession.calculateProfit());
            tickArr = null;
            
        } else if (item instanceof ConnectedMessage) {
            final ConnectedMessage message = (ConnectedMessage) item;
            log.info("Connected to Upstox: {}", message.getMessage());
        } else if (item instanceof DisconnectedMessage) {
            final DisconnectedMessage message = (DisconnectedMessage) item;
            log.info("Disconnected from Upstox:  Code: {}, Reason: {}", message.getCode(), message.getReason());
        } else if (item instanceof ClosingMessage) {
            final ClosingMessage message = (ClosingMessage) item;
            log.warn("Closing the web-socket connection:  Code: {}, Reason: {}", message.getCode(), message.getReason());
        } else if (item instanceof ErrorMessage) {
            final ErrorMessage message = (ErrorMessage) item;
            // Reusing the 'onError()'
            onError(new WebSocketError("Error from Upstox: " + message.getReason(), message.getThrowable()));
        } else {
            // if (item instanceof TextMessage) {
            final TextMessage message = (TextMessage) item;
            log.info("Text message received: {}", message);
        }
        // Ask for the next message (do not miss this line)
        this.subscription.request(1);
    }
    
    private ArrayList<Ticker> getNewBricks(ArrayList<Ticker> tickerArr){
    	if(tickerArr != null && tickerArr.size() > lastRenkoArrayLength) {
    		ArrayList<Ticker> out = new ArrayList<Ticker>();
    		out.addAll(lastRenkoArrayLength, tickArr);
    		lastRenkoArrayLength = tickArr.size();
    		return out;
    	}
    	return null;
    }
    
    public void onError(final Throwable throwable) {
        log.fatal("Error occurred: {}", throwable);
    }

 
    public void onComplete() {
        log.info("Subscription is now complete - no more messages from Upstox.");
    }

  
    public String getName() {
        return "RikoWsSubscriber"; // Provide a unique name. Helps with debugging.
    }

    public void cleanUp() {
        if (null != subscription) {
            this.subscription.cancel();
        }
    }
    
	/*
	 * public void setParams(String tickerName,float brickSize){
	 * instantiateTradingSession(tickerName,brickSize); }
	 */
    
	/*
	 * private void instantiateTradingSession(String tickerName,float brickSize) {
	 * log.debug("Instantiated trading sessions with TickerName:"
	 * +tickerName+" BrickSize: "+brickSize);
	 * tradingSession.setBrickSize(brickSize); tradingSession.setSessionType(1);
	 * tradingSession.setTickerName(tickerName); }
	 */
}