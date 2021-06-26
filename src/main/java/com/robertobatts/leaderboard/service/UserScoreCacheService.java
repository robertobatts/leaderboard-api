package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;

import java.util.List;

public interface UserScoreCacheService {

    UserScore getUserScore(String userId);

    long getScore(String userId);

    void update(UserScoreModel userScoreModel);

    List<UserScore> getFromRankRange(long fromRank, long toRank);

    void evict(String userId);

    void evict(UserScoreModel userScoreModel);
}
