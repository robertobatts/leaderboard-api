package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import com.robertobatts.leaderboard.exception.ValidationException;
import com.robertobatts.leaderboard.model.UserScoreModel;
import com.robertobatts.leaderboard.repository.UserScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public final class UserScoreServiceImpl implements UserScoreService {

    private final UserScoreRepository userScoreRepository;

    private final UserScoreCacheService userScoreCacheService;

    @Autowired
    public UserScoreServiceImpl(UserScoreRepository userScoreRepository,
                                UserScoreCacheService userScoreCacheService) {
        this.userScoreRepository = userScoreRepository;
        this.userScoreCacheService = userScoreCacheService;
    }

    @Override
    public UserScore findByIdOrThrowException(String id) {
        Optional<UserScoreModel> userScoreModelOpt = userScoreRepository.findById(id);
        return userScoreModelOpt.map(UserScore::new)
                .orElseThrow(() -> new ValidationException("userId does not exist :: userId=" + id));
    }

    @Override
    public void saveUserScore(String userId, long score) {
        //TODO update cache
        UserScoreModel userScoreModel = new UserScoreModel(userId, score);
        userScoreRepository.save(userScoreModel);
    }

    @Override
    public void deleteByUserId(String userId) {
        //TODO update cache
        //TODO check if record exists
        userScoreRepository.deleteById(userId);
    }

    //TODO use Redis sorted sets to retrieve user ranking
}
