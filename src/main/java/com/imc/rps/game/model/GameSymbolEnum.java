package com.imc.rps.game.model;

import lombok.Getter;

/**
 * Game symbol enum.
 */
@Getter
public enum GameSymbolEnum {
    ROCK("Rock"),
    PAPER("Paper"),
    SCISSORS("Scissors");

    private String description;

    GameSymbolEnum(String description) {
        this.description = description;
    }
}
