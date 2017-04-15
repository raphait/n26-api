package it.rapha.challenge.transactions.model;

import javax.validation.constraints.Min;

import lombok.Value;

@Value
public class Transaction {

	private final double amount;
	
	@Min(0)
	private final long timestamp;
}
