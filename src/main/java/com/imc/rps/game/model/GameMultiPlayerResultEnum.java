package com.imc.rps.game.model;

import com.google.common.collect.Lists;
import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GameMultiPlayer result enum.
 */
@Getter
public enum GameMultiPlayerResultEnum {
    WIN("Won the game"),
    LOSE("Lost the game"),
    DRAW("Drew"),
    UNKNOWN("N/A");

    public static final String NO_WINNERS = "no winners";
    private String description;
    private List<Integer> playerNums;

    public GameMultiPlayerResultEnum setPlayerWinners(List<Integer> playerNums) {
        this.playerNums = playerNums; return this;
    }

    GameMultiPlayerResultEnum(String description) {
        this.description = description;
    }

    public String linearize() {

        String winners = NO_WINNERS;

        if (!CollectionUtils.isEmpty(playerNums)) {
            winners = playerNums
                    .stream()
                    .map(i -> i.toString())
                    .collect(Collectors.joining(","));
        }

        return "players " + winners + " - " + name();
    }

    public static GameMultiPlayerResultEnum valueOf(GameResultEnum gameResultEnum) {

        GameMultiPlayerResultEnum gameMultiPlayerResultEnum;

        if (gameResultEnum == GameResultEnum.WIN) {

            gameMultiPlayerResultEnum = GameMultiPlayerResultEnum.WIN.setPlayerWinners(Lists.newArrayList(1));

        } else if (gameResultEnum == GameResultEnum.LOSE) {

            gameMultiPlayerResultEnum = GameMultiPlayerResultEnum.WIN.setPlayerWinners(Lists.newArrayList(2));

        } else {
            gameMultiPlayerResultEnum = GameMultiPlayerResultEnum.DRAW;
        }

        return gameMultiPlayerResultEnum;

    }

}
