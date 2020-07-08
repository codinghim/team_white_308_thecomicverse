package com.white.thecomicverse.webapp.database.repositories;

import com.white.thecomicverse.webapp.database.model.Dislike;

import org.springframework.data.repository.CrudRepository;




// This will be AUTO IMPLEMENTED by Spring into a Bean called userRepository
// CRUD refers Create, Read, Update, Delete

public interface DislikeRepository extends CrudRepository<Dislike, Integer> {

}