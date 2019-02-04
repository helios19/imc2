package com.imc.rps.game.controller;

import com.imc.rps.game.dto.GameSummary;
import com.imc.rps.game.exception.GameNotFoundException;
import com.imc.rps.game.exception.InvalidParameterException;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import com.imc.rps.game.service.GameResultService;
import com.imc.rps.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.imc.rps.common.utils.ClassUtils.*;

/**
 * Game controller class defining the HTTP operations available for the {@link Game} resource. This controller
 * is mainly used to return a game summary including the result.
 *
 * @see Game
 * @see GameSummary
 * @see GameResultEnum
 * @see RestController
 */
@RestController
@RequestMapping("/rock-paper-scissors/play")
public class GameController {

    private final GameService gameService;

    private final GameResultService gameResultService;

    @Autowired
    public GameController(GameService gameService, GameResultService gameResultService) {
        this.gameService = gameService;
        this.gameResultService = gameResultService;
    }

    /**
     * Returns the game summary result for a given {@code playerSymbol} parameter.
     *
     * @param playerSymbol Player symbol
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    @RequestMapping(value = "/{playerSymbol}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameSummary> getGameResult(@PathVariable("playerSymbol") String playerSymbol) {
        return getGameSummaryResponse(playerSymbol, null);
    }

    /**
     * Returns the game summary result for a given {@code playerSymbol} and {@code gameUUID} parameters.
     *
     * @param playerSymbol Player symbol
     * @param gameUUID     Game UUID
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    @RequestMapping(value = "/{playerSymbol}/{gameUUID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameSummary> getGameResult(@PathVariable("playerSymbol") String playerSymbol, @PathVariable("gameUUID") String gameUUID) {
        return getGameSummaryResponse(playerSymbol, gameUUID);
    }

    private ResponseEntity<GameSummary> getGameSummaryResponse(String playerSymbol, String gameUUID) {

        if (!isValidateSymbol(playerSymbol)) {
            throw new InvalidParameterException("playerSymbol", playerSymbol);
        }

        // generate Game UUID
        gameUUID = generateUUID(gameUUID);

        // generate current game object
        Game currentGame = getGame(playerSymbol, gameUUID);

        // save game in db
        gameService.save(currentGame);

        // get list of game history
        List<Game> gameHistory = gameService.findByUuid(gameUUID);

        GameSummary gameSummary = GameSummary
                .builder()
                .uuid(currentGame.getUuid())
                .playerSymbol(GameSymbolEnum.valueOf(currentGame.getPlayer()))
                .computerSymbol(GameSymbolEnum.valueOf(currentGame.getComputer()))
                .result(GameResultEnum.valueOf(currentGame.getResult()))
                .history(convertToGameDtos(gameHistory))
                .build();

        return ResponseEntity
                .ok()
                .body(gameSummary);
    }

    private Game getGame(String playerSymbol, String gameUUID) {

        GameSymbolEnum player = GameSymbolEnum.valueOf(playerSymbol);
        GameSymbolEnum computer = generateRandomSymbol();
        GameResultEnum gameResult = getGameResult(player, computer);

        return Game
                .builder()
                .player(player.name())
                .computer(computer.name())
                .result(gameResult.name())
                .uuid(gameUUID)
                .date(asDate(LocalDateTime.now())).build();
    }

    /**
     * Returns the game result according to the player and computer symbols passed as argument.
     *
     * @param player   symbol
     * @param computer symbol
     * @return Game result
     */
    private GameResultEnum getGameResult(GameSymbolEnum player, GameSymbolEnum computer) {
        return gameResultService.computeResult(player, computer);
    }

}
