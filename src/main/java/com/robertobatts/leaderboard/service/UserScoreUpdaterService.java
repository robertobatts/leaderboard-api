package com.robertobatts.leaderboard.service;

public interface UserScoreUpdaterService {

    void saveUserScore(String userId, long score);

    void incrementScore(String userId, long increment);

    void deleteByUserId(String userId);
}
