package com.imc.rps.game.dto;

import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Game Summary DTO class gathering game details including {@link #uuid}, {@link #playerSymbols}
 * {@link #result} and {@link #history}.
 *
 * @see GameMultiPlayerDto
 * @see GameResultEnum
 */
@Data
@Builder
public class GameMultiPlayerSummary {
    private String uuid;
    private List<GameSymbolEnum> playerSymbols;
    private String result;
    private List<GameMultiPlayerDto> history;
}
