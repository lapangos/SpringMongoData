package com.lapangos.repository;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.lapangos.config.SpringMongoConfig;
import com.lapangos.entity.Fruits;

@Repository
public class SpringMongoDataRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringMongoDataRepository.class);

	@Autowired
	SpringMongoConfig mongoConfig;

	public List<Fruits> getFruits() {

		List<Fruits> obj = new ArrayList<>();

		Query query = new Query();
		query.addCriteria(Criteria.where("name").in("Mango"));
		try {
			obj = mongoConfig.mongoTemplate().find(query, Fruits.class);
		} catch (Exception x) {
			LOGGER.error("MongoException : {}", x.toString());
		}
		return obj;
	}
}
