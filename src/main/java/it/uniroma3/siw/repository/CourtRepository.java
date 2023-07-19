package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Court;

public interface CourtRepository extends CrudRepository<Court, Long> {
	
	
}
