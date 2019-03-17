package com.imc.rps.game.repository;

import com.imc.rps.game.model.GameMultiPlayer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GameMultiPlayerRepository extends MongoRepository<GameMultiPlayer, String>, GameRepositoryCustom<GameMultiPlayer> {

    Optional<GameMultiPlayer> findById(String id);

}

