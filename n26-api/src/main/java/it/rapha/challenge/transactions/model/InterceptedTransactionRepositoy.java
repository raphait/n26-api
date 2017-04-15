package it.rapha.challenge.transactions.model;

import org.springframework.stereotype.Repository;
import org.springframework.web.context.annotation.RequestScope;

@Repository
@RequestScope
public class InterceptedTransactionRepositoy implements TransactionRespository {

	@Override
	public Long add(Transaction transaction) {
		return transaction.getTimestamp();
	}

}