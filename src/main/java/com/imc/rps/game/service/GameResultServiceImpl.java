package com.imc.rps.game.service;

import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import org.springframework.stereotype.Service;

/**
 * Service class implementing logic to determine the game result from the player and computer input symbols.
 *
 * @see GameResultService
 */
@Service
public class GameResultServiceImpl implements GameResultService {

    /**
     * Determines the result of the game according to {@code player} and {@code computer} symbols.
     *
     * @param player   Player symbol
     * @param computer Computer symbol
     * @return Game result
     */
    @Override
    public GameResultEnum computeResult(GameSymbolEnum player, GameSymbolEnum computer) {

        if (player == computer) {
            return GameResultEnum.DRAW;
        }

        GameResultEnum result = GameResultEnum.UNKNOWN;

        if (player == GameSymbolEnum.PAPER) {

            result = playerHasPaper(computer);

        } else if (player == GameSymbolEnum.ROCK) {

            result = playerHasRock(computer);

        } else if (player == GameSymbolEnum.SCISSORS) {

            result = playerHasScissors(computer);

        }

        return result;
    }


    private GameResultEnum playerHasPaper(GameSymbolEnum computer) {

        return computer == GameSymbolEnum.SCISSORS ? GameResultEnum.LOSE : GameResultEnum.WIN;

    }

    private GameResultEnum playerHasRock(GameSymbolEnum computer) {

        return computer == GameSymbolEnum.PAPER ? GameResultEnum.LOSE : GameResultEnum.WIN;

    }

    private GameResultEnum playerHasScissors(GameSymbolEnum computer) {

        return computer == GameSymbolEnum.ROCK ? GameResultEnum.LOSE : GameResultEnum.WIN;

    }
}