package it.rapha.challenge.rest;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.DoubleSummaryStatistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.rapha.challenge.statistics.model.StatistcsRepository;

@RestController
public class StatisticsRestController {
	
	private StatistcsRepository statistcs;

	@Autowired
	public StatisticsRestController(StatistcsRepository statistcs){
		this.statistcs = statistcs;
	}

	@RequestMapping(value = "/statistics", method = GET, produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<DoubleSummaryStatistics> summarizeStatistics() {
		return new ResponseEntity<DoubleSummaryStatistics>(statistcs.summaryStatistics(), OK);
	}
}
