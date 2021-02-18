package de.hska.iwi.vslab.products.repository;

import de.hska.iwi.vslab.products.model.Product;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepo extends CrudRepository<Product, Long> {

    @Transactional
    @Modifying
    @Query("DELETE FROM Product WHERE categoryId = :categoryId")
    void deleteByCategoryId(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE (:details IS NULL OR p.details LIKE CONCAT('%',:details,'%'))"
            + "AND (:maxPrice IS NULL OR p.price <= :maxPrice)" + "AND (:minPrice IS NULL OR p.price >= :minPrice)")
    Iterable<Product> search(@Param("details") String details, @Param("maxPrice") Double maxPrice,
            @Param("minPrice") Double minPrice);
}