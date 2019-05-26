package com.absk.rtrader.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
	
	@GetMapping("/tickers")
    public Page<Ticker> getTickers(Pageable pageable) {
        return tickerRepository.findAll(pageable);
    }
	
	@GetMapping("/tickers/{dateInString}")
    public List<Ticker> getTickersByDate(@PathVariable String dateInString) {//format yyyy-mm-dd
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd");
		Date date;
		try {
			date = sdf.parse(dateInString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			date = new Date();
			e.printStackTrace();
		}
		
        return tickerRepository.findByTimestampGreaterThan(date);
    }
	
	
	@GetMapping("/get")
    public String getOHLC() {
		return "Site is up!";
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
