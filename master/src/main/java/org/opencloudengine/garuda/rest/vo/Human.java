package org.opencloudengine.garuda.rest.vo;

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

	public Human(int id, String name, int age) {
		this.id = id;
		this.name = name;
		this.age = age;
	}

	@XmlElement
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
}
