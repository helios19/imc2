package com.imc.rps.game.dto;


import lombok.Data;

/**
 * Game DTO class holding the game summary details.
 */
@Data
public class GameDto {
    private String uuid;
    private String player;
    private String computer;
    private String date;
    private String result;
}


