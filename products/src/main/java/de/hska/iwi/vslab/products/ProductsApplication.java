package de.hska.iwi.vslab.products;

import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.hska.iwi.vslab.products.model.Product;
import de.hska.iwi.vslab.products.repository.ProductRepo;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
public class ProductsApplication {

	@Autowired
	private ProductRepo repo;

	@PostConstruct
	public void generateTestData() {

		repo.save(new Product(1L, 2L, "Produkt1", 12.0, "produkt 1 details"));
		repo.save(new Product(2L, 2L, "Produkt2", 42.0, "produkt 2 details"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ProductsApplication.class, args);
	}

}
