package it.rapha.challenge.transactions.model;

import javax.validation.constraints.Min;

import lombok.Value;

@Value
public class Transaction {

	private final double amount;
	
	@Min(value = 0, message = "timestamp must be greater than or equal to 0")
	private final long timestamp;
}
