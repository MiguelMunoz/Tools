package com.neptunedreams.heroes.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import com.neptunedreams.framework.PojoUtility;

/**
 * <p>Created by IntelliJ IDEA.
 * <p>Date: 10/7/18
 * <p>Time: 10:35 AM
 *
 * @author Miguel Mu\u00f1oz
 */
@SuppressWarnings("unused")
@Entity
public class HeroDb {
	private Integer id;
	private String name;

//	public HeroDb(String name) {
//		this.name = name;
//	}

	@Id
	@GeneratedValue
	public Integer getId() {
		return id;
	}

	public void setId(final Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return PojoUtility.toString(this);
	}
}
