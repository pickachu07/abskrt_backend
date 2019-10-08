package com.absk.rtrader.exchange.upstox.services;


import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Flow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import com.absk.rtrader.core.indicators.NRenko;
import com.absk.rtrader.core.interfaces.TickerDataListner;
import com.absk.rtrader.core.models.Ticker;
import com.absk.rtrader.core.repositories.TickerRepository;
import com.absk.rtrader.core.services.TimeframeTransformationService;
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


@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UpstoxWebSocketSubscriber implements MessageSubscriber {

	private static final Logger log = LoggerFactory.getLogger(UpstoxWebSocketSubscriber.class);

    private Flow.Subscription subscription;
    
    private CopyOnWriteArrayList<UpstoxSLAgent> Listners;
    
    @Autowired
    private TradingSession tradingSession;
    
    @Autowired
    private NRenko renko;
    
    private ArrayList<Ticker> tickArr;
    
    private int lastRenkoArrayLength;
    
    @Autowired
    private SimpMessagingTemplate webSocketTemplate;
    
    @Autowired
    private TimeframeTransformationService tss;
    
	
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
        Listners = new CopyOnWriteArrayList<UpstoxSLAgent>();
        lastRenkoArrayLength = 0;
    }
    
    public void subscribeListner(UpstoxSLAgent agent) {
    	if(Listners.size() > config.getSLAgentPoolSize()) {
    		//something wrong as number of listners cannot be more than no of agents in pool
    		log.error("Listner list size is greater than SL Agent pool size.");
    		return;
    	}
    	Listners.add(agent);
    }
    
    public void unsubscribeListner(UpstoxSLAgent agent) {
    	String id = agent.getId();
    	Iterator<UpstoxSLAgent> iter = Listners.iterator();
    	ArrayList<UpstoxSLAgent> temp = new ArrayList<UpstoxSLAgent>();
    	while (iter.hasNext()) {
    	    UpstoxSLAgent agnt = iter.next();

    	    if (agnt.getId().equalsIgnoreCase(id))
    	    	
    	        temp.add(agnt);
    		}
    	if(!temp.isEmpty()) {
    		Listners.removeAll(temp);
    		log.info("Removing Agent: "+id+"from Listner list.");
    	}else {
    		log.error("Agent: "+id+"not found in Listner list.");
    	}
    	
    }

    public void onNext(WebSocketMessage item) {
        if (item instanceof BinaryMessage) {
            
            String itemAsString = ((BinaryMessage) item).getMessageAsString();
            log.debug("Binary Message: {}", itemAsString);
            
            UpstoxTickerUtils utils = new UpstoxTickerUtils();
            Ticker tick = utils.filterTickerBySymbol(itemAsString, tradingSession.getTickerName());
            
            
            if(tick != null) {
            	//transform tick to candle
            	Ticker transformedTick =  tss.transform(tick);
            	
            	if(transformedTick != null) {
            	 	
            		tickerRepo.save(transformedTick);
                	double currentClose = transformedTick.getData().getClose();
                    renko.setBrickSize(tradingSession.getBrickSize());
                    renko.doNext(currentClose);
                    
                    tickArr = tickerUtil.renkoPricesToTickerArray(renko.getRenkoPrices(),UpstoxExchangeTypeConstants.NSE_INDEX,tradingSession.getTickerName());
                    
                    //send ohlc to ohlc_stream
                    webSocketTemplate.convertAndSend("/topic/ohlc_stream", transformedTick);
                    
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
            	}     
            }
            for(Iterator<UpstoxSLAgent> itr = Listners.iterator(); itr.hasNext();){
            	
            	try {
            		UpstoxSLAgent listner = itr.next();
                	log.info("Adding data to agent:"+listner.getId());//convert to debug
                	listner.onNext(itemAsString);
            	}catch(ConcurrentModificationException e) {
            		log.error("Comodification error"+e.getMessage());//convert to debug
            	}
      	
            }
              
            tickArr = null;
            
        } else if (item instanceof ConnectedMessage) {
            final ConnectedMessage message = (ConnectedMessage) item;
            log.info("Connected to Upstox: {}", message.getMessage());
        } else if (item instanceof DisconnectedMessage) {
            final DisconnectedMessage message = (DisconnectedMessage) item;
            log.info("Disconnected from Upstox:  Code: {}, Reason: {}", message.getCode(), message.getReason());
        } else if (item instanceof ClosingMessage) {
            final ClosingMessage message = (ClosingMessage) item;
            log.info("Closing the web-socket connection:  Code: {}, Reason: {}", message.getCode(), message.getReason());
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
    	if(tickerArr != null && tickerArr.size() > 0) {
    		ArrayList<Ticker> out = new ArrayList<Ticker>();
    		for(int i=lastRenkoArrayLength; i<tickArr.size() ; i++ ) {
    			out.add(tickerArr.get(i));
    		}
    		lastRenkoArrayLength = tickArr.size();
    		return out;
    	}
    	return null;
    }
    
    public void onError(final Throwable throwable) {
    	log.error("Error occurred: {}", throwable);
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
}