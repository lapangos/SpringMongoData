package com.lapangos.entity;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Fruits {

	private int id;
	private String name;
	private String color;

}
