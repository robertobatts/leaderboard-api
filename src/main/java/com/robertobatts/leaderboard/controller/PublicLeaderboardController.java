package com.robertobatts.leaderboard.controller;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.exception.ValidationException;
import com.robertobatts.leaderboard.service.UserScoreCacheService;
import com.robertobatts.leaderboard.service.UserScoreUpdaterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/public")
public final class PublicLeaderboardController {

    private final UserScoreCacheService userScoreCacheService;

    private final UserScoreUpdaterService userScoreUpdaterService;

    @Autowired
    public PublicLeaderboardController(UserScoreCacheService userScoreCacheService, UserScoreUpdaterService userScoreUpdaterService) {
        this.userScoreCacheService = userScoreCacheService;
        this.userScoreUpdaterService = userScoreUpdaterService;
    }

    @GetMapping("/get-score")
    public ResponseEntity getScore(@RequestParam("userId") String userId) {
        long score = userScoreCacheService.getScore(userId);
        return ResponseEntity.ok(score);
    }

    @PutMapping("/increment-score")
    public ResponseEntity incrementScore(@RequestParam("userId") String userId, @RequestParam("increment") long increment) {
        if (increment <= 0) {
            throw new ValidationException("increment must be positive :: increment=" + increment);
        }
        userScoreUpdaterService.incrementScore(userId, increment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users")
    public ResponseEntity getUsers(@RequestParam("userId") String userId,
                                   @RequestParam("above") long above, @RequestParam("below") long below) {
        if (above < 0 || below < 0) {
            throw new ValidationException("above and below must be positive :: " +
                    "above=" + above + ", below=" + below);
        }
        List<UserScore> userScores = userScoreCacheService.getFromAboveBelowRange(userId, above, below);
        return ResponseEntity.ok(userScores);
    }

}
