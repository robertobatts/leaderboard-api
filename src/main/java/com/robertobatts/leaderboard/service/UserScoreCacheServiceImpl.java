package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public final class UserScoreCacheServiceImpl implements UserScoreCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UserScoreCacheServiceImpl.class);

    private static final String JEDIS_CACHE_KEY = "leaderboard";

    private final Jedis jedis = new Jedis("redis", 6379);

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
        jedis.zadd(JEDIS_CACHE_KEY, userScoreModel.getScore(), userScoreModel.getUserId());
    }

    @Override
    public List<UserScore> getFromRankRange(long fromRank, long toRank) {
        Set<Tuple> userIdWithScoreSet = jedis.zrevrangeWithScores(JEDIS_CACHE_KEY, fromRank - 1, toRank - 1);
        return userIdWithScoreSet.stream().map(this::getUserScore).collect(Collectors.toList());
    }

    @Override
    public List<UserScore> getFromAboveBelowRange(String userId, long above, long below) {
        long userRank = getRank(userId);
        return getFromRankRange(userRank - above, userRank + below);
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

}
