package it.rapha.challenge.rest;

import java.time.Instant;

import org.springframework.web.client.RestTemplate;

import it.rapha.challenge.transactions.model.Transaction;

public class TransactionRestControllerTest {

	public static void main(String... strings) {
		
		RestTemplate template;
		template = new RestTemplate();
		
		String stats = template.getForObject("http://localhost:8080/statistics", String.class);
		System.out.println(stats);
		
		for(int i = 0; i< 60*1000; i++) {
			Transaction t1 = new Transaction(1.0, Instant.now().toEpochMilli());
			Transaction t2 = new Transaction(10.0, Instant.now().toEpochMilli()+10);
	
			String resp1 = template.postForObject("http://localhost:8080/transactions", t1, String.class);
			String resp2 = template.postForObject("http://localhost:8080/transactions", t2, String.class);
			
			System.out.println(resp1);
			System.out.println(resp2);
		}
		
		String stats2 = template.getForObject("http://localhost:8080/statistics", String.class);
		
		System.out.println(stats2);

	}

}
