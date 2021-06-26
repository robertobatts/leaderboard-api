package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.exception.ValidationException;
import com.robertobatts.leaderboard.model.UserScoreModel;
import com.robertobatts.leaderboard.repository.UserScoreRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UserScoreUpdaterServiceImplTest {

    private UserScoreUpdaterService userScoreUpdaterService;

    @Mock
    private UserScoreRepository userScoreRepository;

    @Mock
    private UserScoreCacheService userScoreCacheService;

    @Before
    public void setUp() {
        this.userScoreUpdaterService = new UserScoreUpdaterServiceImpl(userScoreRepository, userScoreCacheService);
    }

    @Test
    public void givenValidData_thenUserScoreIsSaved() {
        String userId = "rob1996";
        long score = 900000000;
        UserScoreModel userScoreModel = new UserScoreModel(userId, score);

        userScoreUpdaterService.saveUserScore(userId, score);

        verify(userScoreRepository, times(1)).save(userScoreModel);
        verify(userScoreCacheService, times(1)).update(userScoreModel);
    }

    @Test
    public void givenValidData_thenUserScoreIsIncremented() {
        String userId = "rob1996";
        long increment = 100;
        long score = 1000;
        UserScoreModel incrementedUserScoreModel = new UserScoreModel(userId, score + increment);

        when(userScoreCacheService.getScore(userId)).thenReturn(score);
        userScoreUpdaterService.incrementScore(userId, increment);

        verify(userScoreRepository, times(1)).save(incrementedUserScoreModel);
        verify(userScoreCacheService, times(1)).update(incrementedUserScoreModel);
    }

    @Test
    public void givenInvalidData_whenIncrementIsInvalid_thenUserScoreIsNotIncremented() {
        String userId = "rob1996_5432";
        long increment = -3000;
        long score = 500;

        when(userScoreCacheService.getScore(userId)).thenReturn(score);

        Assertions.assertThrows(ValidationException.class, () -> userScoreUpdaterService.incrementScore(userId, increment));
    }

    @Test
    public void givenInvalidData_whenScoreIsNegative_thenExceptionIsThrown() {
        String userId = "fds.980best$player08";
        long score = -1;

        Assertions.assertThrows(ValidationException.class, () -> userScoreUpdaterService.saveUserScore(userId, score));
    }

    @Test
    public void givenInvalidData_whenUserIdIsBlank_thenExceptionIsThrown() {
        String userId = "     ";
        long score = 100;

        Assertions.assertThrows(ValidationException.class, () -> userScoreUpdaterService.saveUserScore(userId, score));
    }

}
