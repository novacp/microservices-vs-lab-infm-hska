package de.hska.iwi.vslab.catalog.repository;

import de.hska.iwi.vslab.catalog.model.Category;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends CrudRepository<Category, Long> {

}