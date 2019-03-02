package com.leaf.springData;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;

public class PersonRepositoryImpl implements PersonDao {

	
	@PersistenceContext
	private EntityManager entityManager;
	public void test() {
		Person person = entityManager.find(Person.class, 11);
		System.out.println("-->"+person);
	}

}
