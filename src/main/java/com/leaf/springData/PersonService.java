package com.leaf.springData;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PersonService {

	@Autowired
	private PersonRepository repository;
	
	public void savePersons(List<Person> persons) {
		repository.save(persons);
	}
	
	public void updatePersonEmail(Integer id, String email){
		repository.updatePersonEmail(id, email);
	}
}
