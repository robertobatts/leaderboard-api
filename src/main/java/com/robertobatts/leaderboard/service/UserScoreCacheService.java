package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;

import java.util.List;
import java.util.Optional;

public interface UserScoreCacheService {

    Optional<UserScore> getUserScore(String userId);

    Optional<Long> getScore(String userId);

    void update(UserScoreModel userScoreModel);

    List<UserScore> getFromRankRange(long fromRank, long toRank);

    List<UserScore> getFromAboveBelowRange(String userId, long above, long below);

    void evict(String userId);

    void evictAll();

}
