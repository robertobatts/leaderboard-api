package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class UserScoreCacheServiceImpl implements UserScoreCacheService {

    private static final String JEDIS_CACHE_KEY = "leaderboard";

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    private JedisPool jedisPool;

    @PostConstruct
    public void init() {
        jedisPool = new JedisPool(host, port); //JedisPool is thread safe
    }

    @Override
    public Optional<UserScore> getUserScore(String userId) {
        Optional<Long> score = getScore(userId);
        if (score.isPresent()) {
            Optional<Long> rankOpt = getRank(userId);
            return Optional.of(new UserScore(userId, score.get(), rankOpt.get()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Long> getScore(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Optional<Double> scoreOpt = Optional.ofNullable(jedis.zscore(JEDIS_CACHE_KEY, userId));
            return scoreOpt.map(Double::longValue);
        }
    }

    private Optional<Long> getRank(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            Long rank = jedis.zrevrank(JEDIS_CACHE_KEY, userId);
            if (rank == null) {
                return Optional.empty();
            }
            return Optional.of(rank + 1);
        }
    }

    @Override
    public void upsert(String userId, long score) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zadd(JEDIS_CACHE_KEY, score, userId);
        }
    }

    @Override
    public List<UserScore> getFromRankRange(long fromRank, long toRank) {
        try (Jedis jedis = jedisPool.getResource()) {
            Set<Tuple> userIdWithScoreSet = jedis.zrevrangeWithScores(JEDIS_CACHE_KEY, fromRank - 1, toRank - 1);
            return userIdWithScoreSet.stream().map(this::getUserScore).collect(Collectors.toList());
        }
    }

    @Override
    public List<UserScore> getFromAboveBelowRange(String userId, long above, long below) {
        Optional<Long> userRankOpt = getRank(userId);
        if (userRankOpt.isPresent()) {
            return getFromRankRange(userRankOpt.get() - above, userRankOpt.get() + below);
        }
        return Collections.emptyList();
    }

    private UserScore getUserScore(Tuple tuple) {
        String userId = tuple.getElement();
        Double score = tuple.getScore();
        Optional<Long> rankOpt = getRank(userId);
        //not calling rankOpt.isPresent() because it must exist, the tuple is in fact been retrieved from the cache by userId
        return new UserScore(userId, score.longValue(), rankOpt.get());
    }

    @Override
    public void evict(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.zrem(JEDIS_CACHE_KEY, userId);
        }
    }

    @Override
    public void evictAll() {
        try (Jedis jedis = jedisPool.getResource()) {
            jedis.flushAll();
        }
    }
}
