# spring-boot-rest-service
This service retrieve data from third party server sending http request through RestTemplate. 
Then parse data from JSON to DTO, calculate some needed attribute (average vote) and pass data as response to client.
There is implmented ThreadPoolTaskExecutor for asynch calculation work limited one thread which return Future.
Service has three REST endpoints:
1. Get result by id (GET method)
2. Get status of result (GET method)
3. Stop calculation (POST method)
