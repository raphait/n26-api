package it.rapha.challenge.statistics.model;

import static it.rapha.challenge.statistics.model.ImmutableBucket.EMPTY;
import static org.junit.Assert.assertEquals;

import java.util.DoubleSummaryStatistics;

import org.junit.Before;
import org.junit.Test;

public class StatistcsRepositoryTest {
	
	private static final double epsilon = 0.001;
	
	private StatistcsRepository statistcs;

	@Before
	public void setUp() {
		statistcs = new ExponentialHistogramsStatistcsRepository();
	}

	@Test
	public void shouldAddATransactionWithProperSummaryStatisticsResult() {
		double amount = 12.3;
		long timestamp = System.currentTimeMillis();
		statistcs.add(timestamp, amount);
		
		DoubleSummaryStatistics summaryStatistics = statistcs.summaryStatistics();
		
		assertEquals(amount, summaryStatistics.getSum(), epsilon);
		assertEquals(1l, summaryStatistics.getCount());
	}
	
	@Test
	public void shouldAddTwoTransactionWithProperSummaryStatisticsResult() {
		double amount = 12.3;
		long timestamp = System.currentTimeMillis();
		statistcs.add(timestamp, amount);
		statistcs.add(timestamp, amount);
		
		DoubleSummaryStatistics summaryStatistics = statistcs.summaryStatistics();
		
		assertEquals(amount*2, summaryStatistics.getSum(), epsilon);
		assertEquals(2l, summaryStatistics.getCount());
		
	}
	
	@Test
	public void shouldSkipTheExpiredTransactionAndSummaryOthers() throws InterruptedException {
		long now = System.currentTimeMillis();

		double skippedAmount1 = 1.0;
		long skippedTimestamp1 = now-60_000;
		statistcs.add(skippedTimestamp1, skippedAmount1);

		double addedAmount2 = 1.0;
		long addedTimestamp2 = now-58_000;
		statistcs.add(addedTimestamp2, addedAmount2);
		
		double addedAmount3 = 10.0;
		long addedTimestamp3 = now;
		statistcs.add(addedTimestamp3, addedAmount3);
		
		double addedAmount4 = 100.0;
		long addedTimestamp4 = System.currentTimeMillis();
		statistcs.add(addedTimestamp4, addedAmount4);
		
		double skippedAmount5 = 1.0;
		long skippedTimestamp5 = now+60_000;
		statistcs.add(skippedTimestamp5, skippedAmount5);
		
		DoubleSummaryStatistics summaryStatistics = statistcs.summaryStatistics();
		
		assertEquals(111.0, summaryStatistics.getSum(), epsilon);
		assertEquals(3l, summaryStatistics.getCount());
		
	}
	
	@Test
	public void shouldKeepEmptyForAAditionSkiped() throws InterruptedException {
		double amount1 = 1.0;
		long timestamp1 = System.currentTimeMillis()-(60*1000);
		
		ImmutableBucket emptyActual = statistcs.add(timestamp1, amount1);

		DoubleSummaryStatistics summaryStatistics = statistcs.summaryStatistics();
		
		assertEquals(EMPTY, emptyActual);
		assertEquals(.0, summaryStatistics.getSum(), epsilon);
		assertEquals(0l, summaryStatistics.getCount());
		
		System.out.println(System.currentTimeMillis());
		
	}
	
}
