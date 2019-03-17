package com.imc.rps.cache;

import com.google.common.collect.Lists;
import com.imc.rps.common.utils.ClassUtils;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import com.imc.rps.game.repository.GameRepository;
import com.imc.rps.game.service.GameService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles({"test"})
@SpringApplicationConfiguration(classes = GameCacheIT.TestCacheConfig.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GameCacheIT {

    @Autowired
    @InjectMocks
    private GameService<Game, GameRepository> gameService;

    @Mock
    private GameRepository repository;

    @Autowired
    private CacheManager cacheManager;

    private static final String GAME_UUID = UUID.randomUUID().toString();

    private Game sampleGame = Game
            .builder()
            .player(GameSymbolEnum.SCISSORS.name())
            .computer(GameSymbolEnum.PAPER.name())
            .result(GameResultEnum.WIN.name())
            .uuid(GAME_UUID)
            .date(ClassUtils.toDate("1/10/2016 2:51:23 AM"))
            .build();


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        gameService.save(sampleGame);
        gameService.setRepository(repository);
    }

    @After
    public void tearDown() throws Exception {
        // reset game cache
        cacheManager.getCache(ClassUtils.GAMES_COLLECTION_NAME).clear();
    }

    @Test
    @DirtiesContext
    public void shouldFindGameByIdFromCache() {

        // given
        when(repository.findById(any(String.class)))
                .thenReturn(Optional.of(sampleGame));

        String id = "1";
        Cache gameCache = cacheManager.getCache(ClassUtils.GAMES_COLLECTION_NAME);
        Cache.ValueWrapper beforeFillingCache = gameCache.get(id);


        // when
        Optional<Game> gameFromRepo = gameService.findById(id);
        Optional<Game> gameFromCache = gameService.findById(id);

        // then
        Cache.ValueWrapper afterFillingCache = gameCache.get(id);
        assertNull(beforeFillingCache);
        assertNotNull(afterFillingCache);
        assertTrue(gameFromRepo.isPresent());
        assertTrue(gameFromCache.isPresent());
        assertEquals(gameFromCache, gameFromRepo);

        // verify that repository object is only called once which means that the second invocation to gameService
        // will be redirected to the cache
        verify(repository, times(1)).findById(any(String.class));
    }

    @Test
    @DirtiesContext
    public void shouldFindAllGamesFromCache() {

        // given
        when(repository.findAll()).thenReturn(
                Lists.newArrayList(sampleGame));

        // when
        List<Game> gamesFromRepo = gameService.findAll();
        List<Game> gamesFromCache = gameService.findAll();

        // then
        assertFalse(gamesFromRepo.isEmpty());
        assertFalse(gamesFromCache.isEmpty());
        assertEquals(gamesFromCache, gamesFromRepo);

        verify(repository, times(1)).findAll();

    }

    @Configuration
    @ComponentScan({"com.imc.rps.game.service", "com.imc.rps.game.model"})
    @EnableAutoConfiguration
    @EnableCaching
    public static class TestCacheConfig {
        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager(ClassUtils.GAMES_COLLECTION_NAME, ClassUtils.GAME_MULTIPLAYERS_COLLECTION_NAME);
        }
    }

}
