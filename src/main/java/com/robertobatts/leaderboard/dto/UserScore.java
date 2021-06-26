package com.robertobatts.leaderboard.dto;

import com.robertobatts.leaderboard.model.UserScoreModel;
import com.robertobatts.leaderboard.utils.ValidationUtils;

import java.util.Objects;

public final class UserScore {

    private final String userId;

    private long score;

    public UserScore(String userId, long score) {
        this.userId = ValidationUtils.checkIsNotNullOrEmpty(userId, "userId must not be null or empty");
        this.score = ValidationUtils.checkIsGte(score, 0, "score cannot be negative");
    }

    public UserScore(UserScoreModel userScoreModel) {
        this(userScoreModel.getUserId(), userScoreModel.getScore());
    }

    public String getUserId() {
        return userId;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "UserScore{" +
                "userId='" + userId + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserScore)) return false;
        UserScore userScore = (UserScore) o;
        return score == userScore.score &&
                Objects.equals(userId, userScore.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, score);
    }
}
