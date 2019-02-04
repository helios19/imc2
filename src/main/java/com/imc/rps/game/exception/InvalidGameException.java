package com.imc.rps.game.exception;

import com.imc.rps.game.model.Game;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thrown when an {@link Game} instance contains invalid field values
 * (e.g {@link Game#player} is null, etc.)
 *
 * @see Game
 * @see HttpStatus
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidGameException extends RuntimeException {
    public InvalidGameException(Game game) {
        super("Invalid Game field values [" + game + "]");
    }
}
