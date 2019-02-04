package com.imc.rps.game.dto;


import lombok.Data;

/**
 * Game DTO class representing a customer game.
 */
@Data
public class GameDto {
    private String uuid;
    private String player;
    private String computer;
    private String date;
    private String result;
}


