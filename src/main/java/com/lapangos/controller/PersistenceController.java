package com.lapangos.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;

import com.lapangos.service.PersistenceService;

public class PersistenceController {

	@Autowired
	PersistenceService service;
	
	@PostMapping("/run")
	public void run(){
		
		service.execute();
	}
}
