package de.hska.iwi.vslab.category;

import de.hska.iwi.vslab.category.model.Category;
import de.hska.iwi.vslab.category.repository.CategoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class CategoriesApplication {

	@Autowired
	private CategoryRepo categoryRepo;

	@PostConstruct
	public void generateTestData() {

		Category c1 = new Category();
		c1.setId(1L);
		c1.setName("Auto");

		Category c2 = new Category();
		c2.setId(2L);
		c2.setName("Fahrrad");

		Category c3 = new Category();
		c3.setId(3L);
		c3.setName("Elektronik");

		categoryRepo.save(c1);
		categoryRepo.save(c2);
		categoryRepo.save(c3);
	}

	public static void main(String[] args) {
		SpringApplication.run(CategoriesApplication.class, args);
	}

}
