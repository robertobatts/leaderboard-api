package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;

public interface UserScoreService {

    UserScore findByIdOrThrowException(String id);

    void saveUserScore(String userId, long score);

    void deleteByUserId(String userId);
}
