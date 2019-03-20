package com.absk.rtrader.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.absk.rtrader.datafetcher.ScheduledDataFetcher;
import com.absk.rtrader.model.Ticker;
import com.absk.rtrader.repository.TickerRepository;

@RestController
public class TickerController {

	private static final Logger log = LoggerFactory.getLogger(TickerController.class);
	@Autowired
	private TickerRepository tickerRepository;
	
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
		return null;
    }
	
	
}
