package com.imc.rps.game.repository;

import com.imc.rps.game.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameRepository extends MongoRepository<Game, String>, GameRepositoryCustom<Game> {

    Optional<Game> findById(String id);

}

