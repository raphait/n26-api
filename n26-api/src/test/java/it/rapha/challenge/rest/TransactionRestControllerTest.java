package it.rapha.challenge.rest;

import java.time.Instant;

import org.springframework.web.client.RestTemplate;

import it.rapha.challenge.transactions.model.Transaction;

public class TransactionRestControllerTest {

	public static void main(String... strings) throws InterruptedException {

		RestTemplate template;
		template = new RestTemplate();
		
		Instant start = Instant.now();
		
		String stats0 = template.getForObject("http://localhost:8080/statistics", String.class);
		System.out.println("Statistics before:" + stats0);
		
		for(int i = 0; i< 60_000; i++) {
			Transaction t1 = new Transaction(1.0, Instant.now().toEpochMilli());
			Transaction t2 = new Transaction(10.0, Instant.now().toEpochMilli()+10);
	
			template.postForObject("http://localhost:8080/transactions", t1, String.class);
			template.postForObject("http://localhost:8080/transactions", t2, String.class);
			
			String stats1 = template.getForObject("http://localhost:8080/statistics", String.class);
			System.out.println("Statistics during:" + stats1);
		}

		Instant finish = Instant.now();
		System.out.println("Duration of tests: " + (finish.toEpochMilli() - start.toEpochMilli()) + " milliseconds.");
		
		System.out.println("Waiting 60 seconds.");
		Thread.sleep(60_000);

		String stats2 = template.getForObject("http://localhost:8080/statistics", String.class);
		System.out.println("Statistics after:" + stats2);

	}

}
