package com.lapangos.serviceImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lapangos.entity.Fruits;
import com.lapangos.service.PersistenceService;

public class PersistenceServiceImpl implements PersistenceService {
	
	private static final Logger logger = LoggerFactory.getLogger(PersistenceServiceImpl.class);

	public void execute() {

		Fruits fruit = new Fruits(1,"Mango","Yellow");
		
		fruit.toString();
	}
}
