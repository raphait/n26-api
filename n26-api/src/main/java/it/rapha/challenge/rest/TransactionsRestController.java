package it.rapha.challenge.rest;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import it.rapha.challenge.transactions.model.Transaction;
import it.rapha.challenge.transactions.model.TransactionRespository;

@RestController
public class TransactionsRestController {
	
	private TransactionRespository transactionRespository;

	@Autowired
	public TransactionsRestController(TransactionRespository transactionRespository) {
		this.transactionRespository = transactionRespository;
	}

	@RequestMapping(value =  "/transactions", method = POST, consumes =  APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> create(@RequestBody Transaction transaction, UriComponentsBuilder uriBuilder) {
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriBuilder.path("/user/{id}").buildAndExpand(transactionRespository.add(transaction)).toUri());
        return new ResponseEntity<Void>(headers, HttpStatus.CREATED);
	}
}
