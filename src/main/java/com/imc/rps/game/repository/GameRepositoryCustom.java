package com.imc.rps.game.repository;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepositoryCustom<T> {

    void saveOrUpdate(T... t);

    List<T> findByUuid(String uuid);

    MongoTemplate getMongoTemplate();

    void setMongoTemplate(MongoTemplate template);

}
