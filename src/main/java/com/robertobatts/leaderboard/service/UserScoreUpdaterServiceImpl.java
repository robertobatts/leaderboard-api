package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.model.UserScoreModel;
import com.robertobatts.leaderboard.repository.UserScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
public final class UserScoreUpdaterServiceImpl implements UserScoreUpdaterService {

    private final UserScoreRepository userScoreRepository;

    private final UserScoreCacheService userScoreCacheService;

    @Autowired
    public UserScoreUpdaterServiceImpl(UserScoreRepository userScoreRepository,
                                       UserScoreCacheService userScoreCacheService) {
        this.userScoreRepository = userScoreRepository;
        this.userScoreCacheService = userScoreCacheService;
    }

    @PostConstruct
    private void populateCache() {
        //TODO: findAll with paging to avoid loading too many records into memory?
        List<UserScoreModel> userScoreModels = userScoreRepository.findAll();
        userScoreModels.forEach(userScoreCacheService::update);
    }

    @Override
    public void saveUserScore(String userId, long score) {
        UserScoreModel userScoreModel = new UserScoreModel(userId, score);
        userScoreRepository.save(userScoreModel);
        userScoreCacheService.update(userScoreModel);
    }

    @Override
    public void incrementScore(String userId, long increment) {
        long score = userScoreCacheService.getScore(userId);
        saveUserScore(userId, score + increment);
    }

    @Override
    public void deleteByUserId(String userId) {
        //TODO check if record exists
        userScoreRepository.deleteById(userId);
        userScoreCacheService.evict(userId);
    }

}
