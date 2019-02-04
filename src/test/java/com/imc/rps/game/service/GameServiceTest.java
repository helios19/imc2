package com.imc.rps.game.service;

import com.google.common.collect.Lists;
import com.imc.rps.common.utils.ClassUtils;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import com.imc.rps.game.repository.GameRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles({"test", "cacheDisabled"})
@SpringApplicationConfiguration(classes = GameServiceTest.TestAppConfig.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GameServiceTest {

    @InjectMocks
    private GameServiceImpl gameService;

    @Mock
    private GameRepository repository;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    private static final String GAME_UUID = UUID.randomUUID().toString();

    private Game sampleGame = Game
            .builder()
            .player(GameSymbolEnum.SCISSORS.name())
            .computer(GameSymbolEnum.PAPER.name())
            .result(GameResultEnum.WIN.name())
            .uuid(GAME_UUID)
            .date(ClassUtils.toDate("1/10/2016 2:51:23 AM"))
            .build();

    @Test
    @DirtiesContext
    public void shouldFindGameByuuid() {
        // given
        when(repository.findByUuid(any(String.class))).thenReturn(
                Lists.newArrayList(sampleGame));

        // when
        List<Game> games = gameService.findByUuid(GAME_UUID);

        // then
        assertFalse(games.isEmpty());
        assertNotNull(games.get(0));
        assertEquals(GameSymbolEnum.SCISSORS.name(), games.get(0).getPlayer());
        assertEquals(GameSymbolEnum.PAPER.name(), games.get(0).getComputer());
        assertEquals(GAME_UUID, games.get(0).getUuid());
        assertEquals(ClassUtils.toDate("1/10/2016 2:51:23 AM"), games.get(0).getDate());
        assertEquals(GameResultEnum.WIN.name(), games.get(0).getResult());
        verify(repository, times(1)).findByUuid(any(String.class));
        verifyNoMoreInteractions(repository);
    }

    @Test
    @DirtiesContext
    public void shouldSaveGame() {
        // when
        gameService.save(sampleGame);

        // then
        verify(repository, times(1)).saveOrUpdate(sampleGame);
        verifyNoMoreInteractions(repository);
    }

    @Configuration
    @ComponentScan({
            "com.imc.rps.game.service"
    })
    public static class TestAppConfig {

        @Bean
        public GameRepository gameRepository() {
            return null;
        }
    }
}

