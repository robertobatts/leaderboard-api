package com.robertobatts.leaderboard.dto;

import com.robertobatts.leaderboard.utils.ValidationUtils;

import java.util.Objects;

public final class UserScore {

    private final String userId;

    private long score;

    private long rank;

    public UserScore(String userId, long score, long rank) {
        this.userId = ValidationUtils.checkIsNotBlank(userId, "userId must not be null or empty");
        this.score = ValidationUtils.checkIsGte(score, 0, "score cannot be negative");
        this.rank = ValidationUtils.checkIsGte(rank, 0, "rank cannot be negative");
    }

    public String getUserId() {
        return userId;
    }

    public long getScore() {
        return score;
    }

    public long getRank() {
        return rank;
    }

    @Override
    public String toString() {
        return "UserScore{" +
                "userId='" + userId + '\'' +
                ", score=" + score +
                ", rank=" + rank +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserScore)) return false;
        UserScore userScore = (UserScore) o;
        return score == userScore.score &&
                rank == userScore.rank &&
                Objects.equals(userId, userScore.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, score, rank);
    }
}
