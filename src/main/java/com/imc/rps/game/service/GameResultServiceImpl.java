package com.imc.rps.game.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.Tuple3;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;

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

    @Override
    public GameResultEnum computeResult(List<GameSymbolEnum> players) {

        if (CollectionUtils.size(players) == 1) {
            return GameResultEnum.WIN.setPlayerWinners(Lists.newArrayList(1));
        }

        if (CollectionUtils.size(players) == 2) {
            return computeResult(players.get(0), players.get(1));
        }

        Map<GameSymbolEnum, List<GameSymbolEnum>> symbolsPerTypeList = players.stream()
                .collect(groupingBy(gameSymbolEnum -> gameSymbolEnum));

        ListMultimap<GameSymbolEnum, Integer> playerIndexesPerSymbol = getPlayerIndexesPerSymbol(players);


        return tryGetMultiPlayerResult(GameSymbolEnum.PAPER, symbolsPerTypeList, playerIndexesPerSymbol)

                .orElseGet(() -> tryGetMultiPlayerResult(GameSymbolEnum.SCISSORS, symbolsPerTypeList, playerIndexesPerSymbol)

                        .orElseGet(() -> tryGetMultiPlayerResult(GameSymbolEnum.ROCK, symbolsPerTypeList, playerIndexesPerSymbol)

                                .orElse(GameResultEnum.DRAW)));

    }

    /**
     * Returns a {@code MultiMap} of player indexes per symbol.
     *
     * @param players Player list
     * @return MultiMap of player indexes per symbol
     */
    private ListMultimap<GameSymbolEnum, Integer> getPlayerIndexesPerSymbol(List<GameSymbolEnum> players) {
        // MultiMap storing the list of player numbers by game symbol
        ListMultimap<GameSymbolEnum, Integer> multimap = ArrayListMultimap.create();

        IntStream.range(0, players.size())
                .forEach(idx ->
                        multimap.put(players.get(idx), idx)
                );
        return multimap;
    }

    private Optional<GameResultEnum> tryGetMultiPlayerResult(
            GameSymbolEnum gameSymbolEnum,
            Map<GameSymbolEnum, List<GameSymbolEnum>> symbolsPerTypeList,
            ListMultimap<GameSymbolEnum, Integer> playerIndexesPerSymbol) {

        List playerWinnerNums = Lists.newArrayList();

        Tuple3<Integer, Integer, Integer> totalNumberPerSymbol = countTotalNumberPerSymbol(symbolsPerTypeList, gameSymbolEnum);

        List<GameSymbolEnum> otherSymbols = getOtherSymbolList(gameSymbolEnum);

        // players with the greatest symbol in total, are eliminated
        if (totalNumberPerSymbol._1 > totalNumberPerSymbol._2
                && totalNumberPerSymbol._1 > totalNumberPerSymbol._3) {

            Optional<Tuple2<GameSymbolEnum, GameSymbolEnum>> onlyTwoPlayer = checkIfOnlyTwoPlayersLeft(gameSymbolEnum, symbolsPerTypeList);

            // when only two players left default to the rule engine with two players
            if (onlyTwoPlayer.isPresent()) {
                return Optional.of(computeResult(onlyTwoPlayer.get()._1, onlyTwoPlayer.get()._2));
            }

            if (totalNumberPerSymbol._2 == totalNumberPerSymbol._3) {
                return Optional.of(GameResultEnum.DRAW);
            }

            playerWinnerNums = totalNumberPerSymbol._2 >= totalNumberPerSymbol._3 ?
                    playerIndexesPerSymbol.get(otherSymbols.get(0))
                    : playerIndexesPerSymbol.get(otherSymbols.get(1));

        }

        return !CollectionUtils.isEmpty(playerWinnerNums) ?
                Optional.ofNullable(GameResultEnum.WIN.setPlayerWinners(playerWinnerNums))
                : Optional.empty();

    }

    /**
     * Counts total number of each symbol.
     *
     * @param symbolsPerTypeList
     * @param gameSymbolEnum
     * @return
     */
    private Tuple3<Integer, Integer, Integer> countTotalNumberPerSymbol(
            Map<GameSymbolEnum, List<GameSymbolEnum>> symbolsPerTypeList, GameSymbolEnum gameSymbolEnum) {

        List<GameSymbolEnum> otherSymbols = getOtherSymbolList(gameSymbolEnum);

        // count total number of each symbol
        Optional<List> totalSymbol1 = Optional.ofNullable(symbolsPerTypeList.get(gameSymbolEnum));
        Optional<List> totalSymbol2 = Optional.ofNullable(symbolsPerTypeList.get(otherSymbols.get(0)));
        Optional<List> totalSymbol3 = Optional.ofNullable(symbolsPerTypeList.get(otherSymbols.get(1)));

        return Tuple.of(totalSymbol1.isPresent() ? totalSymbol1.get().size() : 0,
                totalSymbol2.isPresent() ? totalSymbol2.get().size() : 0,
                totalSymbol3.isPresent() ? totalSymbol3.get().size() : 0);
    }

    private List<GameSymbolEnum> getOtherSymbolList(GameSymbolEnum gameSymbolEnum) {
        return Arrays.stream(GameSymbolEnum.values())
                .filter(gameSymbol -> gameSymbol != gameSymbolEnum)
                .collect(Collectors.toList());
    }

    private Optional<Tuple2<GameSymbolEnum, GameSymbolEnum>> checkIfOnlyTwoPlayersLeft(GameSymbolEnum gameSymbolEnum, Map<GameSymbolEnum, List<GameSymbolEnum>> symbolsPerTypeList) {
        List<GameSymbolEnum> subSymbolList = symbolsPerTypeList
                .values()
                .stream()
                .flatMap(gameSymbolEna -> gameSymbolEna.stream())
                .filter(gameSymbolEna -> !symbolsPerTypeList.get(gameSymbolEnum).contains(gameSymbolEna))
                .collect(Collectors.toList());

        return CollectionUtils.size(subSymbolList) == 2 ?
                Optional.ofNullable(Tuple.of(subSymbolList.get(0), subSymbolList.get(1))) :
                Optional.empty();
    }


    private GameResultEnum playerHasPaper(GameSymbolEnum computer) {

        return computer == GameSymbolEnum.SCISSORS ?
                GameResultEnum.WIN.setPlayerWinners(Lists.newArrayList(2))
                : GameResultEnum.WIN.setPlayerWinners(Lists.newArrayList(1));

    }

    private GameResultEnum playerHasRock(GameSymbolEnum computer) {

        return computer == GameSymbolEnum.PAPER ?
                GameResultEnum.WIN.setPlayerWinners(Lists.newArrayList(2))
                : GameResultEnum.WIN.setPlayerWinners(Lists.newArrayList(1));

    }

    private GameResultEnum playerHasScissors(GameSymbolEnum computer) {

        return computer == GameSymbolEnum.ROCK ?
                GameResultEnum.WIN.setPlayerWinners(Lists.newArrayList(2))
                : GameResultEnum.WIN.setPlayerWinners(Lists.newArrayList(1));

    }
}