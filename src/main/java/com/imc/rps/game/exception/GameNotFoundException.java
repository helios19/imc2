package com.imc.rps.game.exception;

import com.imc.rps.game.model.Game;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when no {@link Game} resource cannot be found.
 *
 * @see Game
 * @see HttpStatus
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String uuid) {
        super("No game found for uuid:" + uuid);
    }
}
