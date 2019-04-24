package com.soni.entity;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import com.soni.mybatis.annotations.KeyIgnore;


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
	@KeyIgnore
	Long id;
	@NotEmpty(message = "{msgs.GWNCDT0092L-3011}")
	String name;
	@Pattern(message = "{msgs.GWNCDT0100L-3006}", regexp = "[0-9]+")
	String age;
	
	String nation;
	@Size(max=6,message = "{msgs.GWNCDT0103L-3009}")
	@Pattern(message = "{msgs.GWNCDT0095L-3002}", regexp = "[a-zA-Z0-9]+")
	String address;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
