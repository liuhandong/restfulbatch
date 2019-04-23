package com.soni.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

public class Person {
	/*
CREATE TABLE person(
id int PRIMARY KEY AUTO_INCREMENT,
name VARCHAR(20),
age int,
nation VARCHAR(20),
address VARCHAR(20)
);



	 */
	@NotEmpty
	String name;
	@Size(max=2,min=1)
	String age;
	@Size(max=6,min=2)
	String nation;
	String address;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAge() {
		return age;
	}
	public void setAge(String age) {
		this.age = age;
	}
	public String getNation() {
		return nation;
	}
	public void setNation(String nation) {
		this.nation = nation;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	

}
