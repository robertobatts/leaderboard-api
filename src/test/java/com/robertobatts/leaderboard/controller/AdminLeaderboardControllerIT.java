package com.robertobatts.leaderboard.controller;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;
import com.robertobatts.leaderboard.repository.UserScoreRepository;
import com.robertobatts.leaderboard.service.UserScoreCacheService;
import com.robertobatts.leaderboard.service.UserScoreUpdaterService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
public class AdminLeaderboardControllerIT {

    private static final String CONTROLLER_PREFIX = "/admin";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserScoreRepository userScoreRepository;

    @Autowired
    private UserScoreUpdaterService userScoreUpdaterService;

    @Autowired
    private UserScoreCacheService userScoreCacheService;

    @BeforeEach
    public void afterEach() {
        userScoreRepository.deleteAll();
        userScoreCacheService.evictAll();
    }

    @Test
    public void givenInsertOrUpdateRequest_thenDataIsInserted() throws Exception {
        mockMvc.perform(put(CONTROLLER_PREFIX + "/set-score")
                .param("userId", "jack125")
                .param("score", "500"))
                .andExpect(status().isOk());

        assertDataIsInsertedInCacheAndDb("jack125", 500, 1);
    }

    @Test
    public void givenUpdateRequest_whenIncrementIsPositive_thenDataIsIncremented() throws Exception {
        userScoreUpdaterService.saveUserScore("john_doe", 200);

        mockMvc.perform(put(CONTROLLER_PREFIX + "/increment-score")
                .param("userId", "john_doe")
                .param("increment", "50"))
                .andExpect(status().isOk());

        assertDataIsInsertedInCacheAndDb("john_doe", 250, 1);
    }

    @Test
    public void givenUpdateRequest_whenIncrementIsPositive_thenDataIsDecremented() throws Exception {
        userScoreUpdaterService.saveUserScore("john_doe", 200);

        mockMvc.perform(put(CONTROLLER_PREFIX + "/increment-score")
                .param("userId", "john_doe")
                .param("increment", "-50"))
                .andExpect(status().isOk());

        assertDataIsInsertedInCacheAndDb("john_doe", 150, 1);
    }

    @Test
    public void givenGetRequest_whenDataIsPresent_thenDataIsRetrieved() throws Exception {
        userScoreUpdaterService.saveUserScore("john_fasd", 1234);

        mockMvc.perform(get(CONTROLLER_PREFIX + "/user")
                .param("userId", "john_fasd"))
                .andExpect(status().isOk())
                .andExpect(content().json("{ 'userId': 'john_fasd', 'score': 1234, 'rank': 1}"));
    }


    private void assertDataIsInsertedInCacheAndDb(String userId, long expectedScore, long expectedRank) {
        Optional<UserScoreModel> userScoreModelOpt = userScoreRepository.findById(userId);
        Assertions.assertThat(userScoreModelOpt).isPresent();
        UserScoreModel userScoreModel = userScoreModelOpt.get();
        Assertions.assertThat(userScoreModel.getScore()).isEqualTo(expectedScore);
        UserScore userScore = userScoreCacheService.getUserScore(userId);
        Assertions.assertThat(userScore.getRank()).isEqualTo(expectedRank);
        Assertions.assertThat(userScore.getScore()).isEqualTo(expectedScore);
    }
}
