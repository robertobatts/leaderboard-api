package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;

import java.util.List;

public interface UserScoreCacheService {

    UserScore getUserScore(String userId);

    long getScore(String userId);

    void update(UserScoreModel userScoreModel);

    List<UserScore> getFromRankRange(long fromRank, long toRank);

    List<UserScore> getFromAboveBelowRange(String userId, long above, long below);

    void evict(String userId);

    void evictAll();

}
