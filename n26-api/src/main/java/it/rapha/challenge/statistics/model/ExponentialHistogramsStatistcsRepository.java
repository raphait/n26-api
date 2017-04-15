package it.rapha.challenge.statistics.model;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Instant;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.concurrent.atomic.AtomicReferenceArray;
import java.util.function.BinaryOperator;
import java.util.stream.IntStream;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ExponentialHistogramsStatistcsRepository implements StatistcsRepository {
	
	private static final long DEFAULT_WINDOW = SECONDS.toMillis(1l);
	private static final int DEFAULT_WINDOW_SIZE = 60;
	private final AtomicReferenceArray<ImmutableBucket> buffer;
	private final int windowSize;
	private final long window;
	
	public ExponentialHistogramsStatistcsRepository() {
		this(DEFAULT_WINDOW_SIZE, DEFAULT_WINDOW);
		System.out.println("creating");
	}

	ExponentialHistogramsStatistcsRepository(int windowSize, long window) {
		this.windowSize = windowSize;
		this.window = window;
		ImmutableBucket emptyBuffer[] = new ImmutableBucket[windowSize];
		Arrays.fill(emptyBuffer, ImmutableBucket.EMPTY);
		this.buffer = new AtomicReferenceArray<>(emptyBuffer);
	}

	@Override
	public ImmutableBucket add(final long timestamp, final double amount) {
		final long timestampSlided = slide(timestamp);
		final long now = now();
		
		if (isExpired(timestampSlided, now)) {
			return ImmutableBucket.EMPTY;
		}
		
		final int delta = seconds(timestamp);
		
		final BinaryOperator<ImmutableBucket> skipExpiredBucketsAndCombine =  (bin1, bin2) -> isExpired(bin1.getTimestamp(), now)? bin2: bin1.combine(bin2);
		
		return buffer.accumulateAndGet(delta, ImmutableBucket.of(timestampSlided, amount), skipExpiredBucketsAndCombine);
	}

	private long now() {
		return slide(Instant.now().toEpochMilli());
	}

	private boolean isExpired(long timestamp, long now) {
		return !isWithinSlideWindow(timestamp, now);
	}
	
	private boolean isWithinSlideWindow(long timestamp, long now) {
		long startWindow = now - windowSize;
		return timestamp > startWindow && timestamp <= now;
	}
	
	private long slide(final long timestamp) {
		return timestamp / window;
	}
	
	private int seconds(final long timestamp) {
		return (int) ((timestamp / window) % windowSize);
	}
	

	@Override
	public DoubleSummaryStatistics summaryStatistics() {
		final DoubleSummaryStatistics summaryStatistics = new DoubleSummaryStatistics();

		long now = now();

		IntStream.range(0, windowSize).forEach(i -> {

			ImmutableBucket bin = buffer.get(i);

			if (bin.isEmpty()) {
				return;
			}

			if (isWithinSlideWindow(bin.getTimestamp(), now)) {
				summaryStatistics.combine(bin.getStatistics());
			}
		});

		return summaryStatistics;
	}

}
