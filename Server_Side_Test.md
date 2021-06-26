Introduction
============

Playtika are launching a new game which needs a leaderboard feature.

The leaderboard should rank all players in the game according to their score in realtime. A player total score increments every time they play a level.

The task is to produce a Spring Boot server application which exposes a REST API for the leaderboard feature.

Example Leaderboard
===================

For example the initial leaderboard is empty:

| User | Rank | Score |
| ---- | ---- | ----- |

User A then submits a level score of 200:

| User | Rank | Score |
| ---- | ---- | ----- |
| A    | 1    | 200   |

User B then submits a level score of 300 and
User C then submits a level score of 50:

| User | Rank | Score |
| ---- | ---- | ----- |
| B    | 1    | 300   |
| A    | 2    | 200   |
| C    | 3    | 50    |

User A then submits another level score of 300 which increments their total to 500:

| User | Rank | Score |
| ---- | ---- | ----- |
| A    | 1    | 500   |
| B    | 2    | 300   |
| C    | 3    | 50    |

APIs
====

There needs to be a public API for the game itself to use, and an admin API for our internal back office tools.

The exact requests and responses are unspecified; use your judgement about what will be required.

Public API
----------

1. Get my current score
2. Increment my current score
3. Get the score and rank of the players 10 ranks above me and 10 ranks below me (e.g. /public/players?above=10&below=10).

Admin API
---------
1. Get the score and rank for a user
2. Increment (or decrement) the score for a user
3. Set the absolute score for a user
4. Delete a user
5. Get the score and rank of a section of the leaderboard (e.g. GET /admin/players?fromRank=10&toRank=20)

Goals
=====

We are looking for (in this order):

- Correctness.
- Clean code.
- Well tested code. Consider the [Test Pyramid](https://martinfowler.com/articles/practical-test-pyramid.html) and what different kind of tests you can have.
- Performance. Is this the most performant solution? Are there any compromises you have made at the cost of performance, and why?

Notes
=====

- You should make your own interpretations on anything ambiguous and document any assumptions you have made.
- For simplicity, you do not need to consider authentication for the public or admin APIs.
- Imagine this will be used in a production environment, potentially receiving multiple requests at the same time.
- You should consider how it could be scaled at a later date when the feature becomes wildly successful.
- You can use whatever database(s) you like.
- You can use whatever libraries, databases and tools you like. Some of our favourites are
[Test Containers](https://github.com/Playtika/testcontainers-spring-boot) and [Rest Assured](https://github.com/rest-assured/rest-assured).