package it.rapha.challenge.statistics.model;


import static java.util.concurrent.TimeUnit.*;
import static org.junit.Assert.*;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;

public class ImmutableBucketTest {
	
	private static final double epsilon = 0.001;
	private static final long SECONDS_WINDOW = SECONDS.toMillis(1l);
	private static final long MINUTES_WINDOW = MINUTES.toMillis(1l);
	private static ImmutableBucket nowTenBucket, nowTweentyBucket, nowNegativeTenBucket, nowNegativeElevenBucket, differentBucket;
	
	@Before
    public void setUp() {
		Instant now = Instant.now();
        nowTenBucket = ImmutableBucket.of(now.toEpochMilli(), 10.0);
        nowTweentyBucket = ImmutableBucket.of(now.toEpochMilli(), 20.0);
        nowNegativeTenBucket = ImmutableBucket.of(now.toEpochMilli(), -10.0);
        nowNegativeElevenBucket = ImmutableBucket.of(now.toEpochMilli(), -11.0);
        differentBucket = ImmutableBucket.of(now.plusSeconds(12l).toEpochMilli(), 10l);
    }
	
	@Test
	public void shouldCreateNewValidBucket() {
		double amount = 12.3;
		long timestamp = 1478192204000l;
		long expectedRounded = 1478192204l;
		
		ImmutableBucket bucket = ImmutableBucket.of(timestamp, amount);
		
		assertNotNull(bucket);
		assertEquals(expectedRounded, bucket.getTimestamp());
		assertEquals(amount, bucket.getAverage(), 0.001);
		assertEquals(amount, bucket.getMax(), 0.001);
		assertEquals(amount, bucket.getMin(), 0.001);
		assertEquals(amount, bucket.getSum(), 0.001);
		assertEquals(bucket.getCount(), 1l);
	}
	
	@Test
	public void shouldTimestampBeSlidedBySecondsBucket() {
		long timestamp = 1478192204321l;
		long expectedRounded = timestamp/1000;
		
		ImmutableBucket bucket = ImmutableBucket.of(timestamp, 3.21, SECONDS_WINDOW);
		
		assertEquals(expectedRounded, bucket.getTimestamp());
	}
	
	@Test
	public void shouldTimestampBeSlidedByMinutesBucket() {
		long timestamp = 1478192204321l;
		long expectedRounded = timestamp/60000;
		ImmutableBucket bucket = ImmutableBucket.of(timestamp, 3.21, MINUTES_WINDOW);
		
		assertEquals(expectedRounded, bucket.getTimestamp());
	}
	
	@Test
	public void shouldCombineTwoBucketst() {
		
		ImmutableBucket combinedBucket = nowTenBucket.combine(nowTweentyBucket);
		double sum = nowTenBucket.getSum() + nowTweentyBucket.getSum();
		
		assertEquals(nowTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(nowTweentyBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(sum/2, combinedBucket.getAverage(), epsilon);
		assertEquals(nowTweentyBucket.getSum(), combinedBucket.getMax(), epsilon);
		assertEquals(nowTenBucket.getSum(), combinedBucket.getMin(), epsilon);
		assertEquals(sum, combinedBucket.getSum(), epsilon);
		assertEquals(combinedBucket.getCount(), 2l);
	}
	
	@Test
	public void shouldCombinePositiveAndNegativeBucketst() {
		
		ImmutableBucket combinedBucket = nowTenBucket.combine(nowNegativeTenBucket);
		double sum = nowTenBucket.getSum() + nowNegativeTenBucket.getSum();
		
		assertEquals(nowTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(nowNegativeTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(0, combinedBucket.getAverage(), epsilon);
		assertEquals(nowTenBucket.getSum(), combinedBucket.getMax(), epsilon);
		assertEquals(nowNegativeTenBucket.getSum(), combinedBucket.getMin(), epsilon);
		assertEquals(sum, combinedBucket.getSum(), epsilon);
		assertEquals(combinedBucket.getCount(), 2l);
	}
	
	@Test
	public void shouldCombineTwoNegativeBucketst() {
		
		ImmutableBucket combinedBucket = nowNegativeTenBucket.combine(nowNegativeElevenBucket);
		double sum = nowNegativeTenBucket.getSum() + nowNegativeElevenBucket.getSum();
		
		assertEquals(nowNegativeElevenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(nowNegativeTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(sum/2, combinedBucket.getAverage(), epsilon);
		assertEquals(nowNegativeTenBucket.getSum(), combinedBucket.getMax(), epsilon);
		assertEquals(nowNegativeElevenBucket.getSum(), combinedBucket.getMin(), epsilon);
		assertEquals(sum, combinedBucket.getSum(), epsilon);
		assertEquals(combinedBucket.getCount(), 2l);
	}
	
	@Test
	public void shouldCombinedlBucketstStatisticsBeTheSameAsDoubleSummaryStatistics() {
		DoubleSummaryStatistics statistics = DoubleStream.of(10.0, 20.0, -10.0, -11.0).summaryStatistics();
		
		ImmutableBucket combinedBucket = Stream.of(nowTenBucket, nowTweentyBucket, nowNegativeTenBucket, nowNegativeElevenBucket)
											   .reduce(ImmutableBucket::combine)
											   .get();

		assertEquals(4, combinedBucket.getCount());
		
		assertEquals(statistics.getCount(), combinedBucket.getCount());
		assertEquals(statistics.getAverage(), combinedBucket.getAverage(), epsilon);
		assertEquals(statistics.getMax(), combinedBucket.getMax(), epsilon);
		assertEquals(statistics.getMin(), combinedBucket.getMin(), epsilon);
		assertEquals(statistics.getSum(), combinedBucket.getSum(), epsilon);
		
		assertEquals(nowNegativeElevenBucket.getTimestamp(), combinedBucket.getTimestamp());
		
	}
	
	@Test(expected = NullPointerException.class)
	public void shouldThrowNullPointerExceptionTest() {
		nowTenBucket.combine(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrowIllegalArgumentExceptionTest() {
		nowTenBucket.combine(differentBucket);
	}
	
}