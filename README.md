# Leaderboard-API

## Assumptions

- The leaderboard is supposed to be global and therefore needs to be persisted permanently
- No needs of specifying an order in case users have same score. In the code the order is decided directly by Redis,
so in case of same score the users are ranked with a reverse lexicographical order

## How it works
##### Database
I'm  using MongoDB for storing all the user scores. Every time the user score is updated/deleted in the database, 
the same operation is done afterward in the cache

This database is never used to retrieve the records, it's only used for insert/update/delete operations

##### Cache
I'm using Redis sorted sets. With sorted sets, the Redis algorithm automatically assign a ranking based on the score of two users.
In case two users have the same score, then they are ranked following reverse lexicographical order

The cache is instantiated at each request through `JedisPool` so it is _thread safe_

The cache is populated at the start of the application, and is updated every time an insert/update/delete operation is done in the db

## How to run it
Build jar file + run unit and integration tests:

```
mvn clean install
```
Run application:
```
docker-compose up --build
```

## Postman Documentation
https://documenter.getpostman.com/view/10941923/Tzef9Nhv