package com.robertobatts.leaderboard.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public final class UserScoreCacheServiceImpl implements UserScoreCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UserScoreCacheServiceImpl.class);

    private static final Jedis jedis = new Jedis("redis", 6379);

    @PostConstruct
    public void test() {
        Map<String, Double> scores = new HashMap<>();

        scores.put("PlayerOne", 3000.0);
        scores.put("PlayerTwo", 1500.0);
        scores.put("PlayerThree", 8200.0);

        scores.entrySet().forEach(playerScore -> {
            jedis.zadd("ranking", playerScore.getValue(), playerScore.getKey());
        });

        String player = jedis.zrevrange("ranking", 0, 1).iterator().next();
        logger.info("ranking bohhh " + player);
        long rank = jedis.zrevrank("ranking", "PlayerOne");
        logger.info("ranking bohhh " + rank);
    }

}
