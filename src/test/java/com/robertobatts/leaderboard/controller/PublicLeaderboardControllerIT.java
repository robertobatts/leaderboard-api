package com.robertobatts.leaderboard.controller;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.model.UserScoreModel;
import com.robertobatts.leaderboard.repository.UserScoreRepository;
import com.robertobatts.leaderboard.service.UserScoreCacheService;
import com.robertobatts.leaderboard.service.UserScoreUpdaterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
public class PublicLeaderboardControllerIT {

    private static final String CONTROLLER_PREFIX = "/public";

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
    public void givenGetScoreRequest_whenDataIsPresent_thenDataIsRetrieved() throws Exception {
        userScoreUpdaterService.saveUserScore("john_fasd", 4321);

        mockMvc.perform(get(CONTROLLER_PREFIX + "/get-score")
                .param("userId", "john_fasd"))
                .andExpect(status().isOk())
                .andExpect(content().string("4321"));
    }

    @Test
    public void givenGetScoreRequest_whenDataIsNotPresent_thenIsNoContent() throws Exception {
        mockMvc.perform(get(CONTROLLER_PREFIX + "/get-score")
                .param("userId", "john_fasd"))
                .andExpect(status().isNoContent());
    }

    @Test
    public void givenIncrementScoreRequest_whenIncrementIsPositive_thenDataIsIncremented() throws Exception {
        userScoreUpdaterService.saveUserScore("john_doe", 200);

        mockMvc.perform(put(CONTROLLER_PREFIX + "/increment-score")
                .param("userId", "john_doe")
                .param("increment", "50"))
                .andExpect(status().isOk());

        assertDataIsInsertedInCacheAndDb("john_doe", 250, 1);
    }

    @Test
    public void givenIncrementScoreRequest_whenIncrementIsNegative_thenIsBadRequest() throws Exception {
        userScoreUpdaterService.saveUserScore("john_doe", 200);

        mockMvc.perform(put(CONTROLLER_PREFIX + "/increment-score")
                .param("userId", "john_doe")
                .param("increment", "-50"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void givenIncrementScoreRequest_whenUserDoesNotExist_thenIsNotFound() throws Exception {
        mockMvc.perform(put(CONTROLLER_PREFIX + "/increment-score")
                .param("userId", "john_doe")
                .param("increment", "50"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenGetUsersRequest_whenAboveIsGteUserRank_thenDataIsRetrieved() throws Exception {
        userScoreUpdaterService.saveUserScore("a", 20); //4
        userScoreUpdaterService.saveUserScore("b", 10); //5
        userScoreUpdaterService.saveUserScore("c", 30); //3
        userScoreUpdaterService.saveUserScore("d", 50); //1
        userScoreUpdaterService.saveUserScore("e", 35); //2
        String expectedJson = "[ { 'userId': 'd', 'score': 50, 'rank': 1}, { 'userId': 'e', 'score': 35, 'rank': 2}, " +
                "{ 'userId': 'c', 'score': 30, 'rank': 3} ]";

        mockMvc.perform(get(CONTROLLER_PREFIX + "/users")
                .param("userId", "e")
                .param("above", "10")
                .param("below", "1"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void givenGetUsersRequest_whenAboveAndBelowAreValid_thenDataIsRetrieved() throws Exception {
        userScoreUpdaterService.saveUserScore("a", 20); //4
        userScoreUpdaterService.saveUserScore("b", 10); //5
        userScoreUpdaterService.saveUserScore("c", 30); //3
        userScoreUpdaterService.saveUserScore("d", 50); //1
        userScoreUpdaterService.saveUserScore("e", 35); //2
        String expectedJson = "[ { 'userId': 'e', 'score': 35, 'rank': 2}, { 'userId': 'c', 'score': 30, 'rank': 3}," +
                "{ 'userId': 'a', 'score': 20, 'rank': 4} ]";

        mockMvc.perform(get(CONTROLLER_PREFIX + "/users")
                .param("userId", "e")
                .param("above", "0")
                .param("below", "2"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedJson));
    }

    @Test
    public void givenGetUsersRequest_whenAboveOrBelowAreNegative_thenIsBadRequest() throws Exception {
        mockMvc.perform(get(CONTROLLER_PREFIX + "/users")
                .param("userId", "e")
                .param("above", "-2")
                .param("below", "4"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get(CONTROLLER_PREFIX + "/users")
                .param("userId", "e")
                .param("above", "10")
                .param("below", "-2"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    public void givenGetUsersRequest_whenUserIdDoesNotExist_thenIsNotFound() throws Exception {
        mockMvc.perform(get(CONTROLLER_PREFIX + "/users")
                .param("userId", "edsadsa")
                .param("above", "1")
                .param("below", "4"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    private void assertDataIsInsertedInCacheAndDb(String userId, long expectedScore, long expectedRank) {
        Optional<UserScoreModel> userScoreModelOpt = userScoreRepository.findById(userId);
        assertThat(userScoreModelOpt).isPresent();
        UserScoreModel userScoreModel = userScoreModelOpt.get();
        assertThat(userScoreModel.getScore()).isEqualTo(expectedScore);
        Optional<UserScore> userScoreOpt = userScoreCacheService.getUserScore(userId);
        assertThat(userScoreOpt).isPresent();
        UserScore userScore = userScoreOpt.get();
        assertThat(userScore.getRank()).isEqualTo(expectedRank);
        assertThat(userScore.getScore()).isEqualTo(expectedScore);
    }
}
