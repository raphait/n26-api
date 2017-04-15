package it.rapha.challenge.statistics.model;


import static org.junit.Assert.*;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

import org.junit.BeforeClass;
import org.junit.Test;

public class BucketTest {
	
	private static final double epslon = 0.001;
	private static ImmutableBucket nowTenBucket, nowTweentyBucket, nowNegativeTenBucket, nowNegativeElevenBucket, differentBucket;
	
	@BeforeClass
    public static void setUp() {
		Instant now = Instant.now();
        nowTenBucket = ImmutableBucket.of(now.toEpochMilli(), 10.0);
        nowTweentyBucket = ImmutableBucket.of(now.toEpochMilli(), 20.0);
        nowNegativeTenBucket = ImmutableBucket.of(now.toEpochMilli(), -10.0);
        nowNegativeElevenBucket = ImmutableBucket.of(now.toEpochMilli(), -11.0);
        differentBucket = ImmutableBucket.of(now.plusSeconds(12l).toEpochMilli(), 10l);
    }
	
	@Test
	public void testNewBucket() {
		double amount1 = 12.3;
		long timestamp1 = 1478192204000l;
		long expectedRounded1 = timestamp1/1000l;
		
		ImmutableBucket bucket1 = ImmutableBucket.of(timestamp1, amount1);
		
		assertEquals(expectedRounded1, bucket1.getTimestamp());
		assertEquals(amount1, bucket1.getAverage(), 0.001);
		assertEquals(amount1, bucket1.getMax(), 0.001);
		assertEquals(amount1, bucket1.getMin(), 0.001);
		assertEquals(amount1, bucket1.getSum(), 0.001);
		assertEquals(bucket1.getCount(), 1l);
	}
	
	@Test
	public void testCombineTwoBucketst() {
		
		ImmutableBucket combinedBucket = nowTenBucket.combine(nowTweentyBucket);
		double sum = nowTenBucket.getSum() + nowTweentyBucket.getSum();
		
		assertEquals(nowTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(nowTweentyBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(sum/2, combinedBucket.getAverage(), epslon);
		assertEquals(nowTweentyBucket.getSum(), combinedBucket.getMax(), epslon);
		assertEquals(nowTenBucket.getSum(), combinedBucket.getMin(), epslon);
		assertEquals(sum, combinedBucket.getSum(), epslon);
		assertEquals(combinedBucket.getCount(), 2l);
	}
	
	@Test
	public void testCombinePositiveAndNegativeBucketst() {
		
		ImmutableBucket combinedBucket = nowTenBucket.combine(nowNegativeTenBucket);
		double sum = nowTenBucket.getSum() + nowNegativeTenBucket.getSum();
		
		assertEquals(nowTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(nowNegativeTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(0, combinedBucket.getAverage(), epslon);
		assertEquals(nowTenBucket.getSum(), combinedBucket.getMax(), epslon);
		assertEquals(nowNegativeTenBucket.getSum(), combinedBucket.getMin(), epslon);
		assertEquals(sum, combinedBucket.getSum(), epslon);
		assertEquals(combinedBucket.getCount(), 2l);
	}
	
	@Test
	public void testCombineTwoNegativeBucketst() {
		
		ImmutableBucket combinedBucket = nowNegativeTenBucket.combine(nowNegativeElevenBucket);
		double sum = nowNegativeTenBucket.getSum() + nowNegativeElevenBucket.getSum();
		
		assertEquals(nowNegativeElevenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(nowNegativeTenBucket.getTimestamp(), combinedBucket.getTimestamp());
		assertEquals(sum/2, combinedBucket.getAverage(), epslon);
		assertEquals(nowNegativeTenBucket.getSum(), combinedBucket.getMax(), epslon);
		assertEquals(nowNegativeElevenBucket.getSum(), combinedBucket.getMin(), epslon);
		assertEquals(sum, combinedBucket.getSum(), epslon);
		assertEquals(combinedBucket.getCount(), 2l);
	}
	
	@Test
	public void testCombineAllBucketst() {
		DoubleSummaryStatistics statistics = DoubleStream.of(10.0, 20.0, -10.0, -11.0).summaryStatistics();
		
		ImmutableBucket combinedBucket = Stream.of(nowTenBucket, nowTweentyBucket, nowNegativeTenBucket, nowNegativeElevenBucket)
											   .reduce(ImmutableBucket::combine)
											   .get();

		assertEquals(4, combinedBucket.getCount());
		
		assertEquals(statistics.getCount(), combinedBucket.getCount());
		assertEquals(statistics.getAverage(), combinedBucket.getAverage(), epslon);
		assertEquals(statistics.getMax(), combinedBucket.getMax(), epslon);
		assertEquals(statistics.getMin(), combinedBucket.getMin(), epslon);
		assertEquals(statistics.getSum(), combinedBucket.getSum(), epslon);
		
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