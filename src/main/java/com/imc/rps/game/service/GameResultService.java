package com.imc.rps.game.service;

import com.imc.rps.game.model.GameMultiPlayerResultEnum;
import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;

import java.util.List;

/**
 * Classification service interface.
 */
public interface GameResultService {

    GameResultEnum computeResult(GameSymbolEnum player, GameSymbolEnum computer);

    GameMultiPlayerResultEnum computeResult(List<GameSymbolEnum> players);
}
