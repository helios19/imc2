package com.imc.rps.game.controller;

import com.google.common.collect.Lists;
import com.imc.rps.common.utils.ClassUtils;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import com.imc.rps.game.service.GameService;
import com.imc.rps.game.service.GameResultService;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

import static com.jayway.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = MockServletContext.class)
public class GameControllerTest {

    @InjectMocks
    GameController controller;

    @Mock
    private GameService gameService;

    @Mock
    private GameResultService gameResultService;

    private MockMvc mvc;

    private Game game = Game
            .builder()
            .players(Lists.newArrayList(GameSymbolEnum.SCISSORS.name(), GameSymbolEnum.PAPER.name()))
            .result("players 1 - " + GameResultEnum.WIN.name())
            .uuid(UUID.randomUUID().toString())
            .date(ClassUtils.toDate("1/10/2016 2:51:23 AM"))
            .build();



    @Before
    public void setUp() throws Exception {
        initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new MappingJackson2HttpMessageConverter()).build();
        RestAssuredMockMvc.mockMvc(mvc);
    }

    @Test
    public void shouldReturnGameSummary() throws IOException {

        when(gameService.findByUuid(any(String.class)))
                .thenReturn(Lists.newArrayList(game));

        when(gameResultService.computeResult(anyListOf(GameSymbolEnum.class)))
                .thenReturn(GameResultEnum.WIN);

        given().
                when().
                get("/rock-paper-scissors/play/"  + game.getUuid() + "?computerGenerated=true&playerSymbol=" + GameSymbolEnum.SCISSORS.name()).
                then().
                statusCode(HttpServletResponse.SC_OK).
                contentType(ContentType.JSON).
                body("playerSymbols", hasItem(GameSymbolEnum.SCISSORS.name())).
                body("result", notNullValue()).
                body("uuid", equalTo(game.getUuid())).
                body("history", notNullValue()).
                log().all();

        verify(gameService, times(1)).findByUuid(any(String.class));
        verify(gameService, times(1)).save(any(Game.class));
        verifyNoMoreInteractions(gameService);
        verify(gameResultService, times(1)).computeResult(anyListOf(GameSymbolEnum.class));
        verifyNoMoreInteractions(gameResultService);
    }

}
