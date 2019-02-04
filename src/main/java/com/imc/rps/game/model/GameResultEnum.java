package com.imc.rps.game.model;

import lombok.Getter;

/**
 * Game result enum.
 */
@Getter
public enum GameResultEnum {
    WIN("Player won the game"),
    LOSE("Player lost the game"),
    DRAW("Player drew with Computer"),
    UNKNOWN("N/A");

    private String description;

    GameResultEnum(String description) {
        this.description = description;
    }
}
