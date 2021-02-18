package de.hska.iwi.vslab.category;

import java.net.URI;
import java.util.Optional;

import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.hska.iwi.vslab.category.repository.CategoryRepo;
import de.hska.iwi.vslab.category.model.Category;

import javax.validation.Valid;

@RestController
@RequestMapping("/categories")
public class CategoryController {
	
	@Autowired
	private CategoryRepo categoryRepo;

    Logger logger = LoggerFactory.getLogger(CategoryController.class);

	// add category
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Category> addCategory(@RequestBody Category category) {

        Category c = new Category();
        c.setName(category.getName());
        return new ResponseEntity<>(categoryRepo.save(c), HttpStatus.CREATED);
    }
    
    //delete category by id
    //
    @RequestMapping(value = "/{categoryId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteCategory(@PathVariable Long categoryId) {

        Optional<Category> optionalProduct = categoryRepo.findById(categoryId);
        if(optionalProduct.isPresent())
        {
            categoryRepo.delete(optionalProduct.get());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
	
	//get all categories
    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')") // required for search and thus every user should have access!
    public ResponseEntity<Iterable<Category>> getCategories() {

        return new ResponseEntity<>(categoryRepo.findAll(), HttpStatus.OK);
    }

    //get category by Id
    @RequestMapping(value = "/{categoryId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Category> getCategory(@PathVariable Long categoryId) {

        Optional<Category> optionalProduct = categoryRepo.findById(categoryId);
        return optionalProduct.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }
}
