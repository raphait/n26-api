package it.rapha.challenge.statistics.model;

import static org.junit.Assert.*;

import java.util.DoubleSummaryStatistics;

import org.junit.Before;
import org.junit.Test;

public class StatistcsRepositoryTest {
	
	private static final double epsilon = 0.001;
	
	private StatistcsRepository statistcs;

	@Before
	public void setUp() {
		statistcs = new LastSixtySecondsStatistcsRepository();
	}

	@Test
	public void shouldAddATransactionWithProperSummaryStatisticsResult() {
		double amount = 12.3;
		long timestamp = 1478192204000l;
		statistcs.add(timestamp, amount);
		
		DoubleSummaryStatistics summaryStatistics = statistcs.summaryStatistics();
		
		assertEquals(amount, summaryStatistics.getSum(), epsilon);
		assertEquals(1l, summaryStatistics.getCount());
	}
	
}
