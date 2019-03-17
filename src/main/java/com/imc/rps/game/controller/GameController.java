package com.imc.rps.game.controller;

import com.imc.rps.game.dto.GameMultiPlayerSummary;
import com.imc.rps.game.dto.GameSummary;
import com.imc.rps.game.exception.GameNotFoundException;
import com.imc.rps.game.exception.InvalidParameterException;
import com.imc.rps.game.model.*;
import com.imc.rps.game.repository.GameMultiPlayerRepository;
import com.imc.rps.game.repository.GameRepository;
import com.imc.rps.game.service.GameResultService;
import com.imc.rps.game.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
@RequestMapping("/rock-paper-scissors")
public class GameController {

    private final GameService<Game, GameRepository> gameService;

    private final GameService<GameMultiPlayer, GameMultiPlayerRepository> gameMultiPlayerService;

    private final GameResultService gameResultService;

    @Autowired
    public GameController(GameService<Game, GameRepository> gameService,
                          GameService<GameMultiPlayer, GameMultiPlayerRepository> gameMultiPlayerService,
                          GameResultService gameResultService) {
        this.gameService = gameService;
        this.gameMultiPlayerService = gameMultiPlayerService;
        this.gameResultService = gameResultService;
    }

    /**
     * Returns the game multiplayer summary result for a given list of {@code playerSymbols} and {@code gameUUID} parameters.
     *
     * @param playerSymbols Player symbols
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    @RequestMapping(value = "/multiplayer", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameMultiPlayerSummary> getGameMultiplePlayersResult(@RequestParam(value = "playerSymbol") List<String> playerSymbols) {
        return getGameMultiPlayerSummaryResponse(playerSymbols, null);
    }

    /**
     * Returns the game summary result for a given {@code playerSymbol} and {@code gameUUID} parameters.
     *
     * @param playerSymbols Player symbols
     * @param gameUUID      Game UUID
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    @RequestMapping(value = "/multiplayer/{gameUUID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameMultiPlayerSummary> getGameMultiplePlayersResult(@RequestParam(value = "playerSymbol") List<String> playerSymbols,
                                                                               @PathVariable("gameUUID") String gameUUID) {
        return getGameMultiPlayerSummaryResponse(playerSymbols, gameUUID);
    }

    /**
     * Generates game multiplayer summary response object.
     *
     * @param playerSymbols Player symbols
     * @param gameUUID      Game UUID
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    private ResponseEntity<GameMultiPlayerSummary> getGameMultiPlayerSummaryResponse(
            @RequestParam(value = "playerSymbol") List<String> playerSymbols, @PathVariable("gameUUID") String gameUUID) {

        if (isInvalidateSymbols(playerSymbols)) {
            throw new InvalidParameterException("playerSymbols", playerSymbols.toString());
        }

        // generate Game UUID
        gameUUID = generateUUID(gameUUID);

        // generate current game object
        GameMultiPlayer currentGame = getGameMultiPlayer(playerSymbols, gameUUID);

        // save game in db
        gameMultiPlayerService.save(currentGame);

        // get list of game multiplayer history
        List<GameMultiPlayer> gameHistory = gameMultiPlayerService.findByUuid(gameUUID);

        GameMultiPlayerSummary gameSummary = GameMultiPlayerSummary
                .builder()
                .uuid(currentGame.getUuid())
                .playerSymbols(playerSymbols.stream().map(s -> GameSymbolEnum.valueOf(s)).collect(Collectors.toList()))
                .result(currentGame.getResult())
                .history(convertToGameMultiPlayerDtos(gameHistory))
                .build();

        return ResponseEntity
                .ok()
                .body(gameSummary);
    }

    /**
     * Returns the game summary result for a given {@code playerSymbol} parameter.
     *
     * @param playerSymbol Player symbol
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    @RequestMapping(value = "/play/{playerSymbol}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
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
    @RequestMapping(value = "/play/{playerSymbol}/{gameUUID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameSummary> getGameResult(@PathVariable("playerSymbol") String playerSymbol, @PathVariable("gameUUID") String gameUUID) {
        return getGameSummaryResponse(playerSymbol, gameUUID);
    }

    /**
     * Generates game summary response object.
     *
     * @param playerSymbol Player symbol
     * @param gameUUID     Game UUID
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
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

    private GameMultiPlayer getGameMultiPlayer(List<String> playerSymbols, String gameUUID) {

        List<GameSymbolEnum> players = playerSymbols
                .stream()
                .map(s -> GameSymbolEnum.valueOf(s))
                .collect(Collectors.toList());

        GameMultiPlayerResultEnum gameResult = getGameMultiPlayerResult(players);

        return GameMultiPlayer
                .builder()
                .players(players.stream().map(p -> p.name()).collect(Collectors.toList()))
                .result(gameResult.linearize())
                .uuid(gameUUID)
                .date(asDate(LocalDateTime.now())).build();
    }

    /**
     * Returns the game result according to the player and computer symbols passed as argument.
     *
     * @param player   symbol
     * @param computer symbol
     * @return GameResultEnum result
     */
    private GameResultEnum getGameResult(GameSymbolEnum player, GameSymbolEnum computer) {
        return gameResultService.computeResult(player, computer);
    }

    /**
     * Returns the game multiplayer result according to the input list of player symbols passed as argument.
     *
     * @param players Player symbols
     * @return GameMultiPlayerResultEnum result
     */
    private GameMultiPlayerResultEnum getGameMultiPlayerResult(List<GameSymbolEnum> players) {
        return gameResultService.computeResult(players);
    }

}
