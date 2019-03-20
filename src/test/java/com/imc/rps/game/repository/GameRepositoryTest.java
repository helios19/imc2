package com.imc.rps.game.repository;

import com.google.common.collect.Lists;
import com.imc.rps.common.utils.ClassUtils;
import com.imc.rps.game.model.*;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.ConfigFileApplicationContextInitializer;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@ActiveProfiles({"test", "cacheDisabled"})
@SpringApplicationConfiguration(classes = GameRepositoryTest.TestAppConfig.class,
        initializers = ConfigFileApplicationContextInitializer.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class GameRepositoryTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private GameRepository repository;

    @Autowired
    private MongodExecutable mongodExec;

    @Autowired
    private MongoClient mongoClient;

    private static MongodExecutable staticMongodExec;
    private static MongoClient staticMongoClient;

    private static final String GAME_UUID = UUID.randomUUID().toString();

    private Game sampleGame = Game
            .builder()
            .players(Lists.newArrayList(GameSymbolEnum.SCISSORS.name(),GameSymbolEnum.PAPER.name()))
            .result("players 1 - " + GameResultEnum.WIN.name())
            .uuid(GAME_UUID)
            .date(ClassUtils.toDate("1/10/2016 2:51:23 AM"))
            .build();


    @Before
    public void setUp() throws Exception {

        // init mongodb
        staticMongodExec = mongodExec;
        staticMongoClient = mongoClient;
    }


    @After
    public void tearDown() throws Exception {
        mongoTemplate.dropCollection(Game.class);
    }

    @AfterClass
    public static void postContruct() {
        // stop mongodb
        staticMongoClient.close();
        staticMongodExec.stop();
    }

    @Test
    public void shouldFindGameById() throws Exception {
        // given
        mongoTemplate.insert(sampleGame);

        // when
        List<Game> games = repository.findByUuid(GAME_UUID);

        // then
        assertFalse(games.isEmpty());
        assertNotNull(games.get(0));
        assertEquals(Lists.newArrayList(GameSymbolEnum.SCISSORS.name(), GameSymbolEnum.PAPER.name()), games.get(0).getPlayers());
        assertEquals(GAME_UUID, games.get(0).getUuid());
        assertEquals(ClassUtils.toDate("1/10/2016 2:51:23 AM"), games.get(0).getDate());
        assertEquals("players 1 - " + GameResultEnum.WIN.name(), games.get(0).getResult());
    }

    @Configuration
    @EnableAutoConfiguration
    @EnableMongoRepositories(basePackages = "com.imc.rps.game.repository")
    @ComponentScan({
            "com.imc.rps.game.service",
            "com.imc.rps.common.service"
    })
    public static class TestAppConfig {
    }

}