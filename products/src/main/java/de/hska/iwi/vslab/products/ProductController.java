package de.hska.iwi.vslab.products;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import de.hska.iwi.vslab.products.model.Product;
import de.hska.iwi.vslab.products.repository.ProductRepo;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductRepo repo;


    Logger logger = LoggerFactory.getLogger(ProductController.class);

    // get all Products
    @RequestMapping(value = "", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Iterable<Product>> getProducts() {

        return new ResponseEntity<>(repo.findAll(), HttpStatus.OK);
    }

    // get Product by Product ID
    @RequestMapping(value = "/{productId}", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Product> getProduct(@PathVariable Long productId) {

        Optional<Product> optionalProduct = repo.findById(productId);
        return optionalProduct.map(product -> new ResponseEntity<>(product, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // add Product
    @RequestMapping(value = "", method = RequestMethod.POST)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {

        Product p = new Product();
        p.setCategoryId(product.getCategoryId());
        p.setDetails(product.getDetails());
        p.setName(product.getName());
        p.setPrice(product.getPrice());

        return new ResponseEntity<>(repo.save(p), HttpStatus.CREATED);
    }

    // delete Product by Product ID
    @RequestMapping(value = "/{productId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProduct(@PathVariable Long productId) {

        Optional<Product> optionalProduct = repo.findById(productId);
        if(optionalProduct.isPresent())
        {
            repo.delete(optionalProduct.get());
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
        }else{
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // delete Products by CategoryId
    @RequestMapping(value = "/category/{categoryId}", method = RequestMethod.DELETE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteProductByCategory(@PathVariable Long categoryId) {

        repo.deleteByCategoryId(categoryId);
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    // get all Products by Search Terms
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<Product>> searchProducts(

            @RequestParam(value = "details", required = false) String details,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice) {

        Iterable<Product> products = repo.search(details, maxPrice, minPrice);
        List<Product> productList = new ArrayList<Product>();
        products.forEach(productList::add);
        return new ResponseEntity<>(productList, HttpStatus.OK);
    }
}