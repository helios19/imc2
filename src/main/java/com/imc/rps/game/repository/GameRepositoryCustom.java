package com.imc.rps.game.repository;

import com.imc.rps.game.model.Game;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepositoryCustom {

    void saveOrUpdate(Game... game);

    List<Game> findByUuid(String uuid);

    MongoTemplate getMongoTemplate();

    void setMongoTemplate(MongoTemplate template);

}
