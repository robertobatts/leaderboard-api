package com.robertobatts.leaderboard.repository;

import com.robertobatts.leaderboard.model.UserScoreModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Testcontainers
public class UserScoreRepositoryIT {

    private UserScoreRepository userScoreRepository;

    @AfterEach
    public void afterEach() {
        userScoreRepository.deleteAll();
    }


    @Test
    public void givenUserScore_thenIsSaved() {
        UserScoreModel expected = new UserScoreModel("abc", 100);

        userScoreRepository.save(expected);

        Optional<UserScoreModel> userScoreModelOpt = userScoreRepository.findById(expected.getUserId());
        assertThat(userScoreModelOpt).isPresent();
        assertThat(userScoreModelOpt.get()).isEqualTo(expected);
    }

    @Test
    public void givenPresentUserScore_thenIsDeleted() {
        UserScoreModel userScoreModel = new UserScoreModel("abc", 100);
        userScoreRepository.save(userScoreModel);

       userScoreRepository.deleteById(userScoreModel.getUserId());

       boolean exists = userScoreRepository.existsById(userScoreModel.getUserId());
       assertThat(exists).isFalse();
    }

}
