package it.rapha.challenge.statistics.model;

import static java.util.Objects.requireNonNull;
import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

import java.util.DoubleSummaryStatistics;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * 
 * Bucket is used to keep the timestamp and the summarized statistics of transactions in a slided window.
 * The values are immutable, although it's using {@link java.util.DoubleSummaryStatistics} (non thread safe)
 *
 * @author Raphael Almeida
 */
@ToString
@EqualsAndHashCode(of = "timestamp")
@RequiredArgsConstructor(access = PRIVATE)
final class ImmutableBucket {
	
	private static final String THE_BUCKETS_SHOULD_BE_IN_THE_SAME_SLIDED_WINDOW = "The buckets should be in the same slided window.";

	private static final DoubleSummaryStatistics EMPTY_STATISTICS = new DoubleSummaryStatistics();
	protected static final ImmutableBucket EMPTY = new ImmutableBucket(-1, EMPTY_STATISTICS);

	@Getter(PACKAGE) private final long timestamp;
	@Getter(PACKAGE) private final DoubleSummaryStatistics statistics;

	protected static ImmutableBucket of(final long timestamp, final double amount) {
		final DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
		statistics.accept(amount);
		
		return new ImmutableBucket(timestamp, statistics);
	}

	double getAverage() {
		return statistics.getAverage();
	}

	double getMax() {
		return statistics.getMax();
	}

	double getMin() {
		return statistics.getMin();
	}
	
	double getSum() {
		return statistics.getSum();
	}
	
	long getCount() {
		return statistics.getCount();
	}
	
	/**
	 * Combines two {@code Bucket}s into new one.
     *
     * @param other another {@code ImmutableBucket} to be combined to this
	 * @return a new {@code ImmutableBucket}, {@code this + other}
	 * @throws NullPointerException if {@code other} is null
	 * @throws IllegalArgumentException if {@code other} differ from this one.
	 */
	ImmutableBucket combine(ImmutableBucket other) {
		requireNonNull(other);
		
		if (isEmpty()) {
			return other;
		}
		
		requireEqual(other, THE_BUCKETS_SHOULD_BE_IN_THE_SAME_SLIDED_WINDOW);
		
		final DoubleSummaryStatistics combinedStatistics = new DoubleSummaryStatistics();
		combinedStatistics.combine(this.getStatistics());
		combinedStatistics.combine(other.getStatistics());
		
		return new ImmutableBucket(timestamp, combinedStatistics);
	}

	private void requireEqual(ImmutableBucket other, String message) throws IllegalArgumentException{
		if (!this.equals(other)) {
			throw new IllegalArgumentException(message);
		}
	}
	
	protected boolean isEmpty() {
		return EMPTY.equals(this);
	}

}