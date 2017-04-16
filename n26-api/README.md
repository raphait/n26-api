# Code challenge
This is a rest project for a code challenge from N26.
The project consists in summarize all transactions processed in the last 60 seconds.

## Rest API
I have implemented two API's to process transactions and summarize transaction's statistics.

The ```/transactions``` API is responsible to process data in a fake way, there is no database to save data.
But I created a transaction's repository to show a proper design.

The ```/statistics``` API provides sumarized data of transactions.
The summarized data is collected by a monitor that it observes when a success transaction is recorded.
After that, the transaction is added to the statistics repositoy to keep in memory the slide window of the last 60 seconds.

## Big-O
Retreving statistics in constant time and memory, O(1), need a proper algorihtm.
I've been studying algorithms with ability of processing data streams over sliding window. 
I've found this paper: ["STREAM: The Stanford Data Stream Management System" by Arasu](http://ilpubs.stanford.edu:8090/641/), that it explains a model in which the stream is infinite but only the last N elements or time units are suitable.
In this other paper, ["Maintaining Stream Statistics Over Sliding Windows" by Datar](www-cs.stanford.edu/~datar/papers/sicomp_streams.pdf), was described a exponential histogram that records the timestamp of selected 1’s that are active in that they belong to the last N elements. 

## Solution
Inspired by those papers and Algebird library, more precisely, [ExpHist](https://twitter.github.io/algebird/datatypes/approx/exponential_histogram.html). And being able to provide threadsafe and concurrent, also according to the requiremts, I developed the ```ExponentialHistogramsStatistcsRepository```.
It uses ```AtomicReferenceArray``` (An array of object references in which elements may be updated atomically) as a circular buffer of```ImmutableBucket```'s. The buffer has 60 positions as seconds, each position represents one second window which contains a bucket formed by a timestamp and statistics of that window. 
The bucket is immutable to avoid dirty reads in concurrent state changes.

The  milliseconds of the given ```timestamp``` are ignored, this propriety is used to check if the statistics of this 1s window still within the last 60s time window.

The ```statistics``` propriety combine all the transaction's amount of that 1s window, The ```statistics```'s type is ```DoubleSummaryStatistics``` that it provides sum, avg, max, min and count. 

When a request for ```/statistics``` is received all the buffer is traversed and summarized to give the proper stastics of that moment, always checking if the bucket still valid. 

The size of the structure, 60,  and the time of reads, ```AtomicReferenceArray::get``` is O(1), are known and so it's a O(1) solution.

## Stack
1. Java 8
2. Spring: Boot, MVC, IoC.
3. AOP/Aspectj
4. Jetty
5. Lombok
6. Maven

## Building and running
Just use: ```mvn clean install spring-boot:run```

Testing with cURL.

```curl -H "Content-Type: application/json" -X POST "localhost:8080/transactions" -d '{"amount": 12.3, "timestamp": 20170415204803}'```

```curl -i “localhost:8080/statistics”```
