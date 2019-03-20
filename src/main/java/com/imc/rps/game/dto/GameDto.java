package com.imc.rps.game.dto;


import lombok.Data;

import java.util.List;

/**
 * Game DTO class holding the game summary details.
 */
@Data
public class GameDto {
    private String uuid;
    private List<String> players;
    private String date;
    private String result;
}


