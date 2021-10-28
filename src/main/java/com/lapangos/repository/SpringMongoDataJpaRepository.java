package com.lapangos.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.lapangos.entity.Fruits;

@Repository
public interface SpringMongoDataJpaRepository extends MongoRepository<Fruits, String> {

}
