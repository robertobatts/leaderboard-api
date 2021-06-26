package com.robertobatts.leaderboard.controller;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.service.UserScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public final class AdminLeaderboardController {

    //TODO set up logging

    private UserScoreService userScoreService;

    @Autowired
    public AdminLeaderboardController(UserScoreService userScoreService) {
        this.userScoreService = userScoreService;
    }

    @GetMapping("/")
    public String home() {
        return "Hello Docker World";
    }

    @PutMapping("/set-score")
    public ResponseEntity setScore(@RequestParam("userId") String userId, @RequestParam("score") long score) {
        userScoreService.saveUserScore(userId, score);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-user")
    public ResponseEntity getUser(@RequestParam("userId") String userId) {
        UserScore userScore = userScoreService.findByIdOrThrowException(userId);
        return ResponseEntity.ok(userScore);
    }
}
