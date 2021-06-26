package com.robertobatts.leaderboard.model;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.utils.ValidationUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Objects;

@Document(collection = "user_scores")
public final class UserScoreModel {

    @Id
    private final String userId;

    private long score;

    public UserScoreModel(String userId, long score) {
        this.userId = ValidationUtils.checkIsNotNullOrEmpty(userId, "userId must not be null or empty");
        this.score = ValidationUtils.checkIsGte(score, 0, "score must not be negative");
    }

    public UserScoreModel(UserScore userScore) {
        this(userScore.getUserId(), userScore.getScore());
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
        return "UserScoreModel{" +
                "userId='" + userId + '\'' +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserScoreModel)) return false;
        UserScoreModel that = (UserScoreModel) o;
        return score == that.score &&
                Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, score);
    }
}
