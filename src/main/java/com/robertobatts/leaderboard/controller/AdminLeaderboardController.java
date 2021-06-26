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
@RequestMapping("/admin")
public final class AdminLeaderboardController {

    //TODO add logs
    //TODO add comments

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
        UserScore userScore = userScoreCacheService.getUserScore(userId);
        return ResponseEntity.ok(userScore);
    }

    @GetMapping("/users")
    public ResponseEntity getUsers(@RequestParam("fromRank") long fromRank, @RequestParam("toRank") long toRank) {
        if (fromRank > toRank) {
            throw new ValidationException("fromRank must be smaller or equal than toRank :: " +
                    "fromRank=" + fromRank + ", toRank=" + toRank);
        }
        List<UserScore> userScores = userScoreCacheService.getFromRankRange(fromRank, toRank);
        return ResponseEntity.ok(userScores);
    }
}
