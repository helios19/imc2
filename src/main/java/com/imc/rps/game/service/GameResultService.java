package com.imc.rps.game.service;

import com.imc.rps.game.model.GameResultEnum;
import com.imc.rps.game.model.GameSymbolEnum;

/**
 * Classification service interface.
 */
public interface GameResultService {

    GameResultEnum computeResult(GameSymbolEnum player, GameSymbolEnum computer);
}
