package com.robertobatts.leaderboard.controller;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.exception.ValidationException;
import com.robertobatts.leaderboard.service.UserScoreCacheService;
import com.robertobatts.leaderboard.service.UserScoreUpdaterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin")
public final class AdminLeaderboardController {

    private UserScoreUpdaterService userScoreUpdaterService;

    private UserScoreCacheService userScoreCacheService;

    @Autowired
    public AdminLeaderboardController(UserScoreUpdaterService userScoreUpdaterService,
                                      UserScoreCacheService userScoreCacheService) {
        this.userScoreUpdaterService = userScoreUpdaterService;
        this.userScoreCacheService = userScoreCacheService;
    }

    @PutMapping("/set-score")
    public ResponseEntity setScore(@RequestParam("userId") String userId, @RequestParam("score") long score) {
        userScoreUpdaterService.saveUserScore(userId, score);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/increment-score")
    public ResponseEntity incrementScore(@RequestParam("userId") String userId, @RequestParam("increment") long increment) {
        userScoreUpdaterService.incrementScore(userId, increment);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity getUser(@RequestParam("userId") String userId) {
        Optional<UserScore> userScoreOpt = userScoreCacheService.getUserScore(userId);
        if (userScoreOpt.isPresent()) {
            return ResponseEntity.ok(userScoreOpt.get());
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity getUsers(@RequestParam("fromRank") long fromRank, @RequestParam("toRank") long toRank) {
        if (fromRank <=0 || toRank <= 0) {
            throw new ValidationException("fromRank and toRank must be greater than zero :: " +
                    "fromRank=" + fromRank + ", toRank=" + toRank);
        }
        if (fromRank > toRank) {
            throw new ValidationException("fromRank must be smaller or equal than toRank :: " +
                    "fromRank=" + fromRank + ", toRank=" + toRank);
        }
        List<UserScore> userScores = userScoreCacheService.getFromRankRange(fromRank, toRank);
        if (!userScores.isEmpty()) {
            return ResponseEntity.ok(userScores);
        }
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/user")
    public ResponseEntity deleteUser(@RequestParam("userId") String userId) {
        userScoreUpdaterService.deleteByUserId(userId);
        return ResponseEntity.ok().build();
    }
}
