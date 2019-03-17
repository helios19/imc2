package com.imc.rps.game.service;

import com.imc.rps.game.model.Game;
import com.imc.rps.game.repository.GameRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Service interface providing method declarations for CRUD operations for the {@link Game} resource.
 *
 * @see Game
 * @see GameServiceImpl
 */
public interface GameService<T, U> {
    /**
     * Saves an {@link Optional<Game>} instance.
     *
     * @param game Game object to save
     */
    void save(T game);

    /**
     * Returns an {@link Optional<Game>} instance given {@code id} argument.
     *
     * @param id Game's identifier
     * @return Optional game
     */
    Optional<T> findById(String id);

    /**
     * Return a list of {@link Game} given {@code uuid} argument.
     *
     * @param uuid Game uuid
     * @return List of game history found for a given uuid
     */
    List<T> findByUuid(String uuid);

    /**
     * Returns a list of all {@link Game}.
     *
     * @return List of games found
     */
    List<T> findAll();

    /**
     * Return a list of {@link Game} given {@link Pageable} argument.
     *
     * @param pageable Pageable argument
     * @return List of games found
     */
    List<T> findAll(Pageable pageable);

    /**
     * Sets a {@link GameRepository} instance.
     *
     * @param repository Game repository instance
     */
    void setRepository(U repository);
}
