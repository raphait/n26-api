package it.rapha.challenge.statistics.model;

import java.util.DoubleSummaryStatistics;

public interface StatistcsRepository {
	
	ImmutableBucket add(long timestamp, double amount);
	
	DoubleSummaryStatistics summaryStatistics();
	
}
