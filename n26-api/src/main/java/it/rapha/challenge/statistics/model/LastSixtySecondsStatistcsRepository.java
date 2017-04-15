package it.rapha.challenge.statistics.model;

import java.util.DoubleSummaryStatistics;

public class LastSixtySecondsStatistcsRepository implements StatistcsRepository {
	
	

	@Override
	public void add(long timestamp, double amount) {
		
	}

	@Override
	public DoubleSummaryStatistics summaryStatistics() {
		DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();
		summaryStatistics.accept(12.3);
		return summaryStatistics;
	}

}
