package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.exception.NotFoundException;
import com.robertobatts.leaderboard.model.UserScoreModel;
import com.robertobatts.leaderboard.repository.UserScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;

@Service
public final class UserScoreUpdaterServiceImpl implements UserScoreUpdaterService {

    private static final String USER_ID_NOT_FOUND_MESSAGE = "[USER_SCORE_EXC-001] - userId does not exist :: userId=";

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
        Optional<Long> scoreOpt = userScoreCacheService.getScore(userId);
        if (scoreOpt.isPresent()) {
            saveUserScore(userId, scoreOpt.get() + increment);
        } else {
            throw new NotFoundException(USER_ID_NOT_FOUND_MESSAGE + userId);
        }
    }

    @Override
    public void deleteByUserId(String userId) {
        boolean exists = userScoreRepository.existsById(userId);
        if (!exists) {
            throw new NotFoundException(USER_ID_NOT_FOUND_MESSAGE + userId);
        }
        userScoreRepository.deleteById(userId);
        userScoreCacheService.evict(userId);
    }

}
