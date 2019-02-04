package com.imc.rps.game.repository;

import com.google.common.base.Strings;
import com.imc.rps.common.service.CounterService;
import com.imc.rps.game.model.Game;
import com.mongodb.MongoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Criteria.where;

/**
 * Repository implementation class methods to manipulate {@link Game} resource in database.
 * This class inherits from {@link GameRepositoryCustom}
 *
 * @see GameRepositoryCustom
 */
@Repository
public class GameRepositoryImpl implements GameRepositoryCustom {

    private static final Logger LOG = LoggerFactory.getLogger(GameRepositoryImpl.class);

    private MongoTemplate mongoTemplate;

    private CounterService counterService;

    @Autowired
    public GameRepositoryImpl(MongoTemplate mongoTemplate, CounterService counterService) {
        this.mongoTemplate = mongoTemplate;
        this.counterService = counterService;
    }

    @Override
    public List<Game> findByUuid(String uuid) {

        Query query = new Query(new Criteria().where("uuid").is(uuid));
        query.with(new Sort(Sort.Direction.DESC, "date"));

        return mongoTemplate.find(query, Game.class);

    }

    @Override
    public void saveOrUpdate(Game... games) {

        Arrays.asList(games).stream().forEach(game -> {
            Criteria criteria = getCriteria(game);

            Update update = getUpdate(game);

            Query query = new Query(criteria);

            try {
                // add an identifier only for new entry
                if (!mongoTemplate.exists(query, Game.class)) {
                    update.set("id", new Integer(counterService.getNextSequence("games")).toString());
                }

                // insert or update game
                mongoTemplate.upsert(query, update, Game.class);
            } catch (MongoException me) {
                LOG.error("An error occurred while upserting game[{},{}] ",
                        game.getId(), game.getDate(), me);
            }
        });

    }

    private Update getUpdate(Game game) {
        return new Update()
//                .set("id", new Integer(counterService.getNextSequence("games")).toString())
                .set("player", game.getPlayer())
                .set("computer", game.getComputer())
                .set("date", game.getDate())
                .set("result", game.getResult())
                .set("uuid", game.getUuid());
    }

    private Criteria getCriteria(Game game) {

        Criteria criteria;

        if (!Strings.isNullOrEmpty(game.getId())) {
            criteria = where("id").is(game.getId());
        } else {
            criteria = new Criteria()
                    .andOperator(
                            where("player").is(game.getPlayer()),
                            where("computer").is(game.getComputer()),
                            where("uuid").is(game.getUuid()),
                            where("date").is(game.getDate()),
                            where("result").is(game.getResult()));
        }

        return criteria;
    }

    @Override
    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }

    @Override
    public void setMongoTemplate(MongoTemplate template) {
        this.mongoTemplate = template;
    }

}