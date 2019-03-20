package com.imc.rps.game.model;

import lombok.Getter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Game result enum.
 */
@Getter
public enum GameResultEnum {
    WIN("Won the game"),
    LOSE("Lost the game"),
    DRAW("Drew"),
    UNKNOWN("N/A");

    public static final String NO_WINNERS = "no winners";
    private String description;
    private List<Integer> playerNums;

    public GameResultEnum setPlayerWinners(List<Integer> playerNums) {
        this.playerNums = playerNums; return this;
    }

    GameResultEnum(String description) {
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
}
