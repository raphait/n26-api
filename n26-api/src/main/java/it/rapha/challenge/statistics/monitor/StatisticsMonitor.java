package it.rapha.challenge.statistics.monitor;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import it.rapha.challenge.statistics.model.StatistcsRepository;
import it.rapha.challenge.transactions.model.Transaction;

@Aspect
@Component
public class StatisticsMonitor {
	
	private StatistcsRepository statistcs;

	@Autowired
	public StatisticsMonitor(StatistcsRepository statistcs) {
		this.statistcs = statistcs;
	}
	
	@AfterReturning("execution(* it.rapha.challenge..**TransactionRespository.*(..))")
	public void logServiceAccess(JoinPoint joinPoint) {
		Transaction t = (Transaction) joinPoint.getArgs()[0];
		statistcs.add(t.getTimestamp(), t.getAmount());
	}

}
