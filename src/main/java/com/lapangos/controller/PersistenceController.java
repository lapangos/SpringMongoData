package com.lapangos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lapangos.entity.Fruits;
import com.lapangos.repository.SpringMongoDataJpaRepository;
import com.lapangos.service.PersistenceService;

@RestController
public class PersistenceController {

	@Autowired
	PersistenceService service;

	@Autowired
	SpringMongoDataJpaRepository repository;

	@PostMapping("/run")
	public void run() {

		service.execute();
	}

	@PostMapping("/save")
	public void save() {

		Fruits fruit = new Fruits(1, "Mango", "Yellow");
		repository.save(fruit);
	}

	@PostMapping("/get/{name}")
	public void get(@PathVariable String name) {

		repository.findById(name);
	}
}
