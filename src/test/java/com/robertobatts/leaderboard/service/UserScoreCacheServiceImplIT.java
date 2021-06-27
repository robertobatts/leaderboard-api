package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Testcontainers
public class UserScoreCacheServiceImplIT {

    @Autowired
    private UserScoreCacheService userScoreCacheService;

    @AfterEach
    public void afterEach() {
        userScoreCacheService.evictAll();
    }

    @Test
    public void givenThousandsOfRandomUserScores_whenGetFromRankRangeIsCalled_thenRankingIsCorrect() {
        int listSize = 10000;
        Random random = new Random();
        for (int i = 0; i < listSize; i++) {
            long score = random.nextLong();
            if (score < 0) {
                score = (score + 1) * (-1);
            }
            String userId = String.valueOf(i);
            userScoreCacheService.upsert(userId, score);
        }

        List<UserScore> userScoreList = userScoreCacheService.getFromRankRange(1, listSize);
        assertThat(userScoreList).hasSize(listSize);
        UserScore previous = userScoreList.get(0);
        for (int i = 1; i < userScoreList.size(); i++) {
            UserScore current = userScoreList.get(i);
            assertThat(current.getRank()).isGreaterThan(previous.getRank());
            assertThat(current.getScore()).isLessThanOrEqualTo(previous.getScore());
        }
    }

    @Test
    public void givenUserScores_whenGetFromAboveBelowRange_thenDataIsRetrievedCorrectly() {
        List<UserScoreModel> models = new ArrayList<>();
        models.add(new UserScoreModel("a", 50));
        models.add(new UserScoreModel("b", 30));
        models.add(new UserScoreModel("c", 100));
        models.add(new UserScoreModel("d", 10));
        models.add(new UserScoreModel("ddd", 10));
        models.add(new UserScoreModel("efg", 50));
        models.add(new UserScoreModel("gfd", 7));
        models.add(new UserScoreModel("ccc", 150));
        models.forEach(model -> userScoreCacheService.upsert(model.getUserId(), model.getScore()));

        int above = 3;
        int below = 2;
        List<UserScore> userScores = userScoreCacheService.getFromAboveBelowRange("b", above, below);

        assertThat(userScores).hasSize(above + below + 1);
        models.sort(Comparator.comparing(UserScoreModel::getScore, Comparator.reverseOrder()));
        int userIdIdx = models.indexOf(new UserScoreModel("b", 30));
        for (int i = 0; i < userScores.size(); i++) {
            UserScore userScore = userScores.get(i);
            UserScoreModel expected = models.get(userIdIdx - above + i);
            assertThat(userScore.getScore()).isEqualTo(expected.getScore());
        }

    }

    @Test
    public void givenValidData_thenIsUpserted() {
        String userId = "abc";
        userScoreCacheService.upsert(userId, 120);

        Optional<UserScore> userScoreOpt = userScoreCacheService.getUserScore(userId);
        assertThat(userScoreOpt).isPresent();
        assertThat(userScoreOpt.get().getScore()).isEqualTo(120);

        userScoreCacheService.upsert(userId, 50);

        userScoreOpt = userScoreCacheService.getUserScore(userId);
        assertThat(userScoreOpt).isPresent();
        assertThat(userScoreOpt.get().getScore()).isEqualTo(50);
    }

    @Test
    public void givenPresentData_thenIsEvicted() {
        String userId = "abc";
        userScoreCacheService.upsert(userId, 120);

        userScoreCacheService.evict(userId);

        Optional<UserScore> userScoreOpt = userScoreCacheService.getUserScore(userId);
        assertThat(userScoreOpt).isNotPresent();
    }

}
