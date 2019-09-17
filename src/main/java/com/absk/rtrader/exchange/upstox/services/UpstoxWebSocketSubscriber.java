package com.absk.rtrader.exchange.upstox.services;


import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.indicators.Renko;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.models.TickerData;
import com.absk.rtrader.core.repositories.TickerRepository;
import com.absk.rtrader.core.services.TradingSession;
import com.absk.rtrader.exchange.upstox.constants.UpstoxSymbolNames;
import com.absk.rtrader.exchange.upstox.exceptions.WebSocketError;
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
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class UpstoxWebSocketSubscriber implements MessageSubscriber {

    private Flow.Subscription subscription;
    
    
    @Autowired
    private TradingSession tradingSession;
    
    @Autowired
    private Renko renko;
    
    private ArrayList<Ticker> tickArr;
    
    @Autowired
    private SimpMessagingTemplate webSocketTemplate;
    
    @Autowired
    private TickerRepository tickerRepo;

    public void onSubscribe(final Flow.Subscription subscription) {
        log.info("Subscribed! Ready to receive messages!");
        this.subscription = subscription;
        this.subscription.request(1);
        instantiateTradingSession(UpstoxSymbolNames.BANK_NIFTY, 4.0F);//Default
    }

    public void onNext(WebSocketMessage item) {
        if (item instanceof BinaryMessage) {
            
            String itemAsString = ((BinaryMessage) item).getMessageAsString();
            Ticker tick = parseTicker(itemAsString);
            log.info("Binary Message: {}", itemAsString);
            log.info("Parsed TickerData: {}", tick.getData().toString());
            
            tickerRepo.save(tick);
            
            tickArr = renko.getInstance().drawRenko(tick, tradingSession.getBrickSize());
            tradingSession.processData(tickArr);
            
            
            //send ohlc to ohlc_stream
            webSocketTemplate.convertAndSend("/topic/ohlc_stream", tick);
            
            //send renko brick to /topic/renko_stream
            for(int i=0;i<tickArr.size();i++){
                //log.info("The tick is now {}", tick);
                webSocketTemplate.convertAndSend("/topic/ticker_stream", tickArr.get(i));
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

    //TODO:move this to core utils
    private Ticker parseTicker(String tickerAsString){
        String[] tickerItems =  tickerAsString.split(",");
        Long timestamp = Long.parseLong(tickerItems[0]);
        String exchange = tickerItems[1];
        String symbol = tickerItems[2];
        double ltp = Double.parseDouble(tickerItems[3]);
       // double open = Double.parseDouble(tickerItems[4]);
        //double high = Double.parseDouble(tickerItems[5]);
        //double low = Double.parseDouble(tickerItems[6]);
        //double close = Double.parseDouble(tickerItems[7]);
        double yHigh = Double.parseDouble(tickerItems[8]);
        double yLow = Double.parseDouble(tickerItems[9]);
        
        TickerData td = new TickerData(ltp,ltp,ltp,ltp,0.0,timestamp,exchange,symbol,yHigh,yLow);
        return new Ticker("Realtime Feed",td,new Date(timestamp));
        
        
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
    
    public void setParams(String tickerName,float brickSize){
        instantiateTradingSession(tickerName,brickSize);
    }
    
    private void instantiateTradingSession(String tickerName,float brickSize) {
        log.debug("Instantiated trading sessions with TickerName:"+tickerName+" BrickSize: "+brickSize);
        tradingSession.setBrickSize(brickSize);
        tradingSession.setSessionType(1);
        tradingSession.setTickerName(tickerName);
    }
}