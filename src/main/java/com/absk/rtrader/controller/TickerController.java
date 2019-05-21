package com.absk.rtrader.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.repository.TickerRepository;
import com.absk.rtrader.scheduler.CentralScheduler;
import com.absk.rtrader.scheduler.tasks.MainTask;

@RestController
public class TickerController {
	

	private static final Logger log = LoggerFactory.getLogger(TickerController.class);
	@Autowired
	private TickerRepository tickerRepository;
	
	@Autowired
	private CentralScheduler centralScheduler;
	
	@PostMapping("/ticker")
    public Ticker createTicker(@Valid @RequestBody Ticker ticker) {
        return tickerRepository.save(ticker);
    }
	
	@GetMapping("/tickers")
    public Page<Ticker> getTickers(Pageable pageable) {
        return tickerRepository.findAll(pageable);
    }
	@GetMapping("/get")
    public String getOHLC() {
		return "Site is up";
    }
	
	@GetMapping("/start")
	public void startScheduler() {
		Runnable task = new MainTask(0, null);
		try {
			CentralScheduler.getInstance().start(task,"* * * ? * *");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@GetMapping("/stop")
	public void stopScheduler() {
		CentralScheduler.getInstance().stopAll();
	}
	
}
