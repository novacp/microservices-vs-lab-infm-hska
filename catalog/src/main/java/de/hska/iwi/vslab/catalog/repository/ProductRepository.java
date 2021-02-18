package de.hska.iwi.vslab.catalog.repository;

import de.hska.iwi.vslab.catalog.model.Category;
import de.hska.iwi.vslab.catalog.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends CrudRepository<Product, Long>  {

}