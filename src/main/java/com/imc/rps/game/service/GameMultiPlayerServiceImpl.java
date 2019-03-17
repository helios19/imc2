package com.imc.rps.game.service;

import com.imc.rps.common.utils.ClassUtils;
import com.imc.rps.game.model.GameMultiPlayer;
import com.imc.rps.game.repository.GameMultiPlayerRepository;
import com.imc.rps.game.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service class providing CRUD operations and caching logic for {@link GameMultiPlayer} resource.
 *
 * @see GameRepository
 */
@Service
@CacheConfig(cacheNames = ClassUtils.GAME_MULTIPLAYERS_COLLECTION_NAME)
public class GameMultiPlayerServiceImpl implements GameService<GameMultiPlayer, GameMultiPlayerRepository> {

    private GameMultiPlayerRepository repository;

    @Autowired
    public GameMultiPlayerServiceImpl(GameMultiPlayerRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(allEntries = true)
    public void save(GameMultiPlayer game) {
        repository.saveOrUpdate(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public Optional<GameMultiPlayer> findById(String id) {
        return repository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public List<GameMultiPlayer> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public List<GameMultiPlayer> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public List<GameMultiPlayer> findAll(Pageable pageable) {
        Page<GameMultiPlayer> games = repository.findAll(pageable);
        return games.getContent();
    }

    /**
     * Sets an {@link GameRepository} instance.
     *
     * @param repository GameMultiPlayer repository instance
     */
    public void setRepository(GameMultiPlayerRepository repository) {
        this.repository = repository;
    }
}
