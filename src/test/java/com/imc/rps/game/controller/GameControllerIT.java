package com.imc.rps.game.controller;

import com.imc.rps.Application;
import com.imc.rps.common.utils.ClassUtils;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.repository.GameRepository;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import com.mongodb.MongoClient;
import de.flapdoodle.embed.mongo.MongodExecutable;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;


@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0"})
@Ignore
public class GameControllerIT {

    private MockMvc mockMvc;

    private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private MongodExecutable mongodExec;

    @Autowired
    private MongoClient mongoClient;

    private static MongodExecutable staticMongodExec;
    private static MongoClient staticMongoClient;
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

        mockMvc = webAppContextSetup(webApplicationContext).build();

        // init mongodb
        staticMongodExec = mongodExec;
        staticMongoClient = mongoClient;

        // add game sample to db
        gameRepository.saveOrUpdate(sampleGame);
    }

    @After
    public void tearDown() throws Exception {
        // reset game collection
        gameRepository.getMongoTemplate().dropCollection(Game.class);
        // reset sequence collection
        gameRepository.getMongoTemplate().dropCollection(ClassUtils.COUNTERS_COLLECTION_NAME);
    }

    @AfterClass
    public static void postContruct() {
        // stop mongodb
        staticMongoClient.close();
        staticMongodExec.stop();
    }


    @Test
    public void shouldReturnGameSummary() throws Exception {
        mockMvc.perform(get("/rock-paper-scissors/play/" + GameSymbolEnum.SCISSORS.name() + "/" + GAME_UUID))
                .andDo(print())
                .andExpect(status().isOk()).andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$.playerSymbol", is(GameSymbolEnum.SCISSORS.name())))
                .andExpect(jsonPath("$.computerSymbol", notNullValue()))
                .andExpect(jsonPath("$.result", notNullValue()))
                .andExpect(jsonPath("$.uuid", is(GAME_UUID)))
                .andExpect(jsonPath("$.history", notNullValue()));
    }


    @Test
    public void shouldThrowExceptionWhenPlaySymbolIsInvalid() throws Exception {
        String unknownPlayerSymbol = "unknown-player-symbol";

        mockMvc.perform(get("/rock-paper-scissors/play/" + unknownPlayerSymbol + "/" + GAME_UUID))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(contentType))
                .andExpect(jsonPath("$[0].logref", is("error")))
                .andExpect(jsonPath("$[0].message", is("Invalid parameter value [param:playerSymbol, value:" + unknownPlayerSymbol + "]")));
    }


    protected String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        new MappingJackson2HttpMessageConverter().write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

}
