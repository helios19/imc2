package com.imc.rps.game.handler;

import com.imc.rps.common.handler.GlobalExceptionHandler;
import com.imc.rps.game.exception.GameNotFoundException;
import com.imc.rps.game.model.Game;
import com.imc.rps.game.exception.InvalidGameException;
import com.imc.rps.game.exception.InvalidParameterException;
import org.springframework.hateoas.VndErrors;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception handler class used for catching any {@link Game} related exceptions
 * and transforming them into HATEOAS JSON message.
 *
 * @see VndErrors
 * @see Game
 * @see InvalidGameException
 * @see InvalidParameterException
 * @see GameNotFoundException
 */
@ControllerAdvice
public class GameExceptionHandler extends GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(InvalidGameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors handleInvalidGameException(InvalidGameException ex) {
        return getVndErrors(ex);
    }

    @ResponseBody
    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    VndErrors handleInvalidParameterException(InvalidParameterException ex) {
        return getVndErrors(ex);
    }

    @ResponseBody
    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    VndErrors handleGameNotFoundException(GameNotFoundException ex) {
        return getVndErrors(ex);
    }

}
