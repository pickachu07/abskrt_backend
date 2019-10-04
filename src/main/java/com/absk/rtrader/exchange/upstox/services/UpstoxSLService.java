package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.absk.rtrader.core.utils.ConfigUtil;
import com.absk.rtrader.exchange.upstox.constants.UpstoxFeedTypeConstants;


@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UpstoxSLService {

	int poolSize;//TODO: make it configurable
	ArrayList<UpstoxSLAgent> agentPool;
	
	@Autowired
	ConfigUtil config;
	
	@Autowired
	UpstoxWebSocketSubscriber upstoxWebSocketSubscriber;
	
	@Autowired
	UpstoxFeedServiceImpl feedService;
	
	private static final Logger log = LoggerFactory.getLogger(UpstoxSLService.class);

	
	UpstoxSLService(){
		//log: service instantiated
		//default poolsize from properties file ->2
		
		
	}
	
	public void instantiateAgents() {
		//create agents and assign to pool
		
		this.poolSize = config.getSLAgentPoolSize();
		this.agentPool = new ArrayList<UpstoxSLAgent>();
		log.info("Instantiating Stop Loss agents. PoolSize: "+poolSize);
		for(int count=0;count<poolSize;count++) {
			UpstoxSLAgent agent = new UpstoxSLAgent(this);
			agent.setSLServiceSupervisor(this);
			agentPool.add(agent);
		}
	}
	
	//get agent by id
	public UpstoxSLAgent getAgent(String id) {
		for(UpstoxSLAgent agent : agentPool ) {
			if(agent.getId().equalsIgnoreCase(id))return agent;
		}
		return null;
	}
	
	////get free agent and instantiate -> return null if no free agent
	public UpstoxSLAgent getFreeAgent() {
		for(UpstoxSLAgent agent : agentPool ) {
			if(!agent.isActive())return agent;
		}
		return null;
	}
	
	public boolean isAnyAgentFree() {
		for(UpstoxSLAgent agent : agentPool ) {
			if(!agent.isActive())return true;
		}
		return false;
	}
	
	//start agent(ticker,price,stoploss)
	public boolean startAgent(String ticker,String exchange,BigDecimal price,int stopLoss) {
		UpstoxSLAgent agent = getFreeAgent();
		if(agent == null) {
			log.error("No agent free!!");
			return false;
		}
		agent.setParams(ticker,exchange, price, stopLoss);
		log.info("Starting agent with details (Ticker Name | Price |  Stoploss) : "+ticker+" | "+price.toPlainString()+" | "+stopLoss);
		subscribeToTicker(ticker,exchange,UpstoxFeedTypeConstants.FEEDTYPE_FULL);
		subscribeAgentToTickerStream(agent.getId());
		return agent.start();
	}
	
	
	
	//register agent to Upstox Web Socket subscriber
	public void subscribeAgentToTickerStream(String agentId){
		log.info("Subcribing to ticker Stream: Agent ID: "+agentId);
		upstoxWebSocketSubscriber.subscribeListner(getAgent(agentId));
		
	}
	
	
	public boolean subscribeToTicker(String tickerName,String exchange, String feedType) {
		return feedService.subscribeToTicker(tickerName, exchange, feedType);
	}
	
	public boolean unsubscribeToTicker(String tickerName,String exchange, String feedType) {
		return feedService.unSubscribeToTicker(tickerName, exchange, feedType);
	}
	
	public String subscribeToTickerGetDetail(String tickerName,String exchange, String feedType) {
		return feedService.subscribeToTickerGetDetail(tickerName, exchange, feedType).toString();
	}
	
	public String unsubscribeToTickerGetDetail(String tickerName,String exchange, String feedType) {
		return feedService.unSubscribeToTickerGetDetail(tickerName, exchange, feedType).toString();
	}
	
	
	
	
	
}
