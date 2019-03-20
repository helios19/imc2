package com.imc.rps.game.service;

import com.imc.rps.common.utils.ClassUtils;
import com.imc.rps.game.model.Game;
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
 * Service class providing CRUD operations and caching logic for {@link Game} resource.
 *
 * @see GameRepository
 */
@Service
@CacheConfig(cacheNames = ClassUtils.GAMES_COLLECTION_NAME)
public class GameServiceImpl implements GameService {

    private GameRepository repository;

    @Autowired
    public GameServiceImpl(GameRepository repository) {
        this.repository = repository;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @CacheEvict(allEntries = true)
    public void save(Game game) {
        repository.saveOrUpdate(game);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public Optional<Game> findById(String id) {
        return repository.findById(id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public List<Game> findByUuid(String uuid) {
        return repository.findByUuid(uuid);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public List<Game> findAll() {
        return repository.findAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Cacheable
    public List<Game> findAll(Pageable pageable) {
        Page<Game> games = repository.findAll(pageable);
        return games.getContent();
    }

    /**
     * Sets an {@link GameRepository} instance.
     *
     * @param repository Game repository instance
     */
    public void setRepository(GameRepository repository) {
        this.repository = repository;
    }
}
