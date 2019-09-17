package com.absk.rtrader.exchange.upstox.services;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import com.absk.rtrader.core.utils.ConfigUtil;

import lombok.extern.log4j.Log4j2;


@Log4j2
@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class UpstoxSLService {

	int poolSize;//TODO: make it configurable
	ArrayList<UpstoxSLAgent> agentPool;
	
	@Autowired
	ConfigUtil config;
	
	@Autowired
	UpstoxWebSocketSubscriber upstoxWebSocketSubscriber;
	
	
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
			UpstoxSLAgent agent = new UpstoxSLAgent();
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
	
	//start agent(ticker,price,stoploss)
	public boolean startAgent(String ticker,BigDecimal price,int stopLoss) {
		UpstoxSLAgent agent = getFreeAgent();
		if(agent == null) {
			log.error("No agent free!!");
			return false;
		}
		agent.setParams(ticker, price, stopLoss);
		log.info("Starting agent with details (Ticker Name | Price |  Stoploss) : "+ticker+" | "+price.toPlainString()+" | "+stopLoss);
		return agent.start();
	}
	
	
	
	//register agent to Upstox Web Socket subscriber
	public boolean subscribeAgentToTickerStream(String agentId){
		log.info("Subcribing to ticker Stream: Agent ID: "+agentId);
		upstoxWebSocketSubscriber.subscribeListner(getAgent(agentId));
		return false;
	}
	
	
	
	
}
