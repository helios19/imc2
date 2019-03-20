package com.imc.rps.game.controller;

import com.imc.rps.game.dto.GameSummary;
import com.imc.rps.game.exception.GameNotFoundException;
import com.imc.rps.game.exception.InvalidParameterException;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import com.imc.rps.game.service.GameService;
import com.imc.rps.game.service.GameResultService;
import org.apache.commons.collections.CollectionUtils;
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

    private final GameService gameService;

    private final GameResultService gameResultService;

    @Autowired
    public GameController(GameService gameService,
                          GameResultService gameResultService) {
        this.gameService = gameService;
        this.gameResultService = gameResultService;
    }

    /**
     * Returns the game summary result for a given list of {@code playerSymbols} and {@code gameUUID} parameters.
     *
     * @param playerSymbols Player symbols
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    @RequestMapping(value = "/play", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameSummary> getGameMultiplePlayersResult(@RequestParam(value = "playerSymbol") List<String> playerSymbols,
                                                                    @RequestParam(value = "computerGenerated", required = false) boolean isComputerGenerated) {
        return getGameSummaryResponse(playerSymbols, null, isComputerGenerated);
    }

    /**
     * Returns the game summary result for a given {@code playerSymbol} and {@code gameUUID} parameters.
     *
     * @param playerSymbols Player symbols
     * @param gameUUID      Game UUID
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    @RequestMapping(value = "/play/{gameUUID}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<GameSummary> getGameMultiplePlayersResult(@RequestParam(value = "playerSymbol") List<String> playerSymbols,
                                                                    @PathVariable("gameUUID") String gameUUID,
                                                                    @RequestParam(value = "computerGenerated", required = false) boolean isComputerGenerated) {
        return getGameSummaryResponse(playerSymbols, gameUUID, isComputerGenerated);
    }

    /**
     * Generates game summary response object.
     *
     * @param playerSymbols Player symbols
     * @param gameUUID      Game UUID
     * @return Game summary result
     * @throws GameNotFoundException if no game for the given playerSymbol and gameUUID can be found
     */
    private ResponseEntity<GameSummary> getGameSummaryResponse(List<String> playerSymbols, String gameUUID,
                                                               boolean isComputerGenerated) {

        if (isInvalidateSymbols(playerSymbols)) {
            throw new InvalidParameterException("playerSymbols", playerSymbols.toString());
        }

        // single player against computer
        addComputerSymbolForSinglePlayer(playerSymbols, isComputerGenerated);

        // generate Game UUID
        gameUUID = generateUUID(gameUUID);

        // generate current game object
        Game currentGame = getGame(playerSymbols, gameUUID);

        // save game in db
        gameService.save(currentGame);

        // get list of game history
        List<Game> gameHistory = gameService.findByUuid(gameUUID);

        GameSummary gameSummary = GameSummary
                .builder()
                .uuid(currentGame.getUuid())
                .playerSymbols(playerSymbols.stream().map(s -> GameSymbolEnum.valueOf(s)).collect(Collectors.toList()))
                .result(currentGame.getResult())
                .history(convertToGameDtos(gameHistory))
                .build();

        return ResponseEntity
                .ok()
                .body(gameSummary);
    }

    private void addComputerSymbolForSinglePlayer(List<String> playerSymbols, boolean isComputerGenerated) {
        if (singlePlayerAgainstComputer(playerSymbols, isComputerGenerated)) {
            playerSymbols.add(generateRandomSymbol().name());
        }
    }

    private boolean singlePlayerAgainstComputer(List<String> playerSymbols, boolean isComputerGenerated) {
        return isComputerGenerated && CollectionUtils.size(playerSymbols) == 1;
    }

    private Game getGame(List<String> playerSymbols, String gameUUID) {

        List<GameSymbolEnum> players = playerSymbols
                .stream()
                .map(s -> GameSymbolEnum.valueOf(s))
                .collect(Collectors.toList());

        GameResultEnum gameResult = getGameResult(players);

        return Game
                .builder()
                .players(players.stream().map(p -> p.name()).collect(Collectors.toList()))
                .result(gameResult.linearize())
                .uuid(gameUUID)
                .date(asDate(LocalDateTime.now())).build();
    }

    /**
     * Returns the game result according to the input list of player symbols passed as argument.
     *
     * @param players Player symbols
     * @return GameResultEnum result
     */
    private GameResultEnum getGameResult(List<GameSymbolEnum> players) {
        return gameResultService.computeResult(players);
    }

}
