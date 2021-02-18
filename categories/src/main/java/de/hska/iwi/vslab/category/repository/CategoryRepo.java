package de.hska.iwi.vslab.category.repository;

import de.hska.iwi.vslab.category.model.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepo extends CrudRepository<Category, Long> {
	
}
