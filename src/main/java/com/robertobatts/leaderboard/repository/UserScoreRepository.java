package com.robertobatts.leaderboard.repository;

import com.robertobatts.leaderboard.model.UserScoreModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserScoreRepository extends MongoRepository<UserScoreModel, String> {
}
