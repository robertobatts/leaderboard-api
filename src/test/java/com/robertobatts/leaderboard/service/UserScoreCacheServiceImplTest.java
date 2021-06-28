package com.robertobatts.leaderboard.service;

import com.robertobatts.leaderboard.dto.UserScore;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Tuple;

import java.lang.reflect.Field;
import java.util.*;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class UserScoreCacheServiceImplTest {

    private static final String JEDIS_CACHE_KEY = "leaderboard";

    @Mock
    private JedisPool jedisPool;

    @Mock
    private Jedis jedis;

    private UserScoreCacheService userScoreCacheService;

    @Before
    public void setUp() throws Exception {
        userScoreCacheService = new UserScoreCacheServiceImpl();
        //reflection is the only way to inject the jedis mock inside cache service
        Field jedisPoolField = userScoreCacheService.getClass().getDeclaredField("jedisPool");
        jedisPoolField.setAccessible(true);
        jedisPoolField.set(userScoreCacheService, jedisPool);
        when(jedisPool.getResource()).thenReturn(jedis);
    }

    @Test
    public void givenValidData_thenIsUpserted() {
        String userId = "aaa";
        long score = 1105;

        userScoreCacheService.upsert(userId, score);

        verify(jedis, times(1)).zadd(JEDIS_CACHE_KEY, score, userId);
    }

    @Test
    public void givenUserId_whenIsPresent_thenUserScoreIsRetrieved() {
        String userId = "user321";
        UserScore userScore = new UserScore(userId, 5400, 1);

        when(jedis.zscore(JEDIS_CACHE_KEY, userId)).thenReturn((double) userScore.getScore());
        when(jedis.zrevrank(JEDIS_CACHE_KEY, userId)).thenReturn(userScore.getRank() - 1);
        Optional<UserScore> resultOpt = userScoreCacheService.getUserScore(userScore.getUserId());

        assertThat(resultOpt).isPresent();
        assertThat(resultOpt.get()).isEqualTo(userScore);
    }

    @Test
    public void givenUserId_whenIsNotPresent_thenUserScoreIsEmpty() {
        String userId = "asddsa";

        when(jedis.zscore(JEDIS_CACHE_KEY, userId)).thenReturn(null);
        Optional<UserScore> resultOpt = userScoreCacheService.getUserScore(userId);

        assertThat(resultOpt).isNotPresent();
        verify(jedis, times(0)).zrevrank(JEDIS_CACHE_KEY, userId);
    }

    @Test
    public void givenFromAndToRank_whenIsPresent_thenUserScoresAreRetrieved() {
        List<Tuple> tupleList = new ArrayList<>();
        tupleList.add(new Tuple("a", 80.));
        tupleList.add(new Tuple("b", 60.));
        tupleList.add(new Tuple("c", 50.));
        tupleList.add(new Tuple("d", 20.));
        tupleList.add(new Tuple("e", 15.));
        tupleList.add(new Tuple("f", 10.));
        Set<Tuple> tupleSet = new TreeSet<>(Comparator.comparing(Tuple::getScore, Comparator.reverseOrder()));

        int fromRank = 2;
        int toRank = 5;
        for (int i = fromRank; i <= toRank; i++) {
            tupleSet.add(tupleList.get(i - 1));
        }
        when(jedis.zrevrangeWithScores(JEDIS_CACHE_KEY, fromRank - 1, toRank - 1)).thenReturn(tupleSet);
        for (int i = 0; i < tupleList.size(); i++) {
            when(jedis.zrevrank(JEDIS_CACHE_KEY, tupleList.get(i).getElement())).thenReturn((long) i);
        }
        List<UserScore> resultList = userScoreCacheService.getFromRankRange(fromRank, toRank);

        assertThat(resultList).hasSize(toRank - fromRank + 1);
        for (int i = fromRank; i <= toRank; i++) {
            Tuple expectedTuple = tupleList.get(i - 1);
            UserScore expectedResult = new UserScore(expectedTuple.getElement(), (long) expectedTuple.getScore(), i);
            UserScore result = resultList.get(i - fromRank);
            assertThat(result).isEqualTo(expectedResult);
        }
    }

}
