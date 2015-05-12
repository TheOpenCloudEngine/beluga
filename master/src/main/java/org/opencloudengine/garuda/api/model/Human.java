package org.opencloudengine.garuda.api.model;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Human
 *
 * @author Sang Wook, Song
 *
 */
@XmlRootElement(name = "human")
public class Human {

	private int id;
	private String name;
	private int age;

	public Human(){ }

	public Human(int id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	@XmlAttribute
	public void setId(int id) {
		this.id = id;
	}

	@XmlElement
	public void setName(String name) {
		this.name = name;
	}

	@XmlElement
	public void setAge(int age) {
		this.age = age;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}
}
