package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class UserScoreCacheServiceImpl implements UserScoreCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UserScoreCacheServiceImpl.class);

    private static final String JEDIS_CACHE_KEY = "leaderboard";

    private final Jedis jedis = new Jedis("redis", 6379);

    public void populate() {
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

    @Override
    public UserScore getUserScore(String userId) {
        //TODO throw exception if not found
        long rank = getRank(userId);
        long score = getScore(userId);
        return new UserScore(userId, score, rank);
    }

    @Override
    public long getScore(String userId) {
        return jedis.zscore(JEDIS_CACHE_KEY, userId).longValue();
    }

    private long getRank(String userId) {
        return jedis.zrevrank(JEDIS_CACHE_KEY, userId) + 1;
    }

    @Override
    public void update(UserScoreModel userScoreModel) {
        jedis.zadd("ranking", userScoreModel.getScore(), userScoreModel.getUserId());
    }

    @Override
    public List<UserScore> getFromRankRange(long fromRank, long toRank) {
        Set<Tuple> userIdWithScoreSet = jedis.zrevrangeWithScores("ranking", fromRank - 1, toRank - 1);
        return userIdWithScoreSet.stream().map(this::getUserScore).collect(Collectors.toList());
    }

    private UserScore getUserScore(Tuple tuple) {
        String userId = tuple.getElement();
        Double score = tuple.getScore();
        long rank = getRank(userId);
        return new UserScore(userId, score.longValue(), rank);
    }

    @Override
    public void evict(String userId) {
        jedis.zrem(JEDIS_CACHE_KEY, userId);
    }

    @Override
    public void evict(UserScoreModel userScoreModel) {
        evict(userScoreModel.getUserId());
    }
}
