package de.hska.iwi.vslab.catalog;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import javax.validation.Valid;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import de.hska.iwi.vslab.catalog.repository.CategoryRepository;
import de.hska.iwi.vslab.catalog.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.core.ParameterizedTypeReference;

import de.hska.iwi.vslab.catalog.model.Category;
import de.hska.iwi.vslab.catalog.model.Product;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/catalog")
public class CatalogController {

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ServiceConfiguration serviceConfiguration;

	// Caches for Fallback methods, better than empty objects!

	@Autowired
	CategoryRepository categoryRepository;

	@Autowired
	ProductRepository productRepository;



	Logger logger = LoggerFactory.getLogger(CatalogController.class);

	// -------------------- PRODUCT -------------------

	// get all products (+ category name)
	@RequestMapping(value = "/products", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "getProductsFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<List<Product>> getProducts(@RequestHeader("Authorization") String token) {

		List<Category> categories = this.requestCategories(token);
		return new ResponseEntity<>(
				this.getProducts(categories, token), HttpStatus.OK
		);
	}


	@RequestMapping(value = "/products/{id}", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "getProductFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")  })
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<Product> getProduct(@PathVariable Long id,
											  @RequestHeader("Authorization") String token) {

		List<Category> categories = this.requestCategories(token);
		return new ResponseEntity<>(
				this.getProduct(id, categories, token), HttpStatus.OK
		);
	}


	// get product by id (+ category name)
	// search product (+ category name)

	// post product
	@RequestMapping(value = "/products", method = RequestMethod.POST)
	@HystrixCommand(fallbackMethod = "createProductFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")  })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Product> createProduct(@Valid @RequestBody Product product, @RequestHeader("Authorization") String token) {

		// Get category
		Category category = this.requestCategory(product.getCategoryId(), token);

		if(category == null)
		{
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		// pass new product to core service
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(product, headers);

		ResponseEntity<Product> p = restTemplate.exchange(
			serviceConfiguration.getProductsCoreServiceUrl(),
			HttpMethod.POST,
			request,
			new ParameterizedTypeReference<>() {
			});

		product = p.getBody();

		// if not null
		if(product != null)
		{
			product.setCategory(category);
			this.productRepository.save(product);
			return new ResponseEntity<>(product, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Delete a product by id.
	 */
	@RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
	@HystrixCommand(fallbackMethod = "deleteProductFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")  })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deleteProduct(@PathVariable Long id, @RequestHeader("Authorization") String token) {

		Product product = this.getProduct(id, new ArrayList<>(), token);

		if(product == null)
		{
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}

		// pass new product to core service
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(headers);

		// delete product in core service
		// delete products first
		restTemplate.exchange(
				serviceConfiguration.getProductsCoreServiceUrl() + "/" + id,
				HttpMethod.DELETE,
				request,
				String.class);

		productRepository.delete(product);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	/**
	 * Search for a product.
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "searchProductsFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000") })
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<List<Product>> searchProducts(
			@RequestParam(value = "details", required = false) String details,
			@RequestParam(value = "minPrice", required = false) Double minPrice,
			@RequestParam(value = "maxPrice", required = false) Double maxPrice,
			@RequestHeader("Authorization") String token
	) {

		// at least one search parameter is required
		if (details == null
				&& minPrice == null
				&& maxPrice == null) {

			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}

		UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(this.serviceConfiguration.getProductsCoreServiceUrl() + "/search");

		if (details != null) {
			builder.queryParam("details", details);
		}

		if (minPrice != null) {
			builder.queryParam("minPrice", minPrice);
		}

		if (maxPrice != null) {
			builder.queryParam("maxPrice", maxPrice);
		}

		// pass new product to core service
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(headers);

		// search products using core service
		ResponseEntity<List<Product>> response = restTemplate.exchange(
				builder.toUriString(),
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});
		if (response.getStatusCode() != HttpStatus.OK) {
			return new ResponseEntity<>(null, response.getStatusCode());
		}

		List<Product> products = response.getBody();
		List<Category> categories = this.requestCategories(token);

		if(products != null)
		{
			for (Product product : products) {

				if(categories != null)
				{
					Category c = categories.stream().filter(c2 -> c2.getId().equals(product.getCategoryId())).findFirst()
							.orElse(new Category());

					if(c.getName() == null) continue;
					product.setCategory(c);
				}

			}
		}

		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	// delete product by productId
	// (delete product by categoryId)


	// -------------------- CATEGORY -------------------

	// get category

	// get all categories
	@RequestMapping(value = "/categories", method = RequestMethod.GET)
	@HystrixCommand(fallbackMethod = "getCategoriesFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")  })
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<List<Category>> getCategories(@RequestHeader("Authorization") String token) {

		return new ResponseEntity<>(
				this.requestCategories(token), HttpStatus.OK
		);
	}

	/**
	 * Add a new category.
	 */
	@RequestMapping(value = "/categories", method = RequestMethod.POST)
	@HystrixCommand(fallbackMethod = "createCategoryFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")  })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<Category> createCategory(@Valid@RequestBody Category category, @RequestHeader("Authorization") String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(category, headers);

		ResponseEntity<Category> c = restTemplate.exchange(
				serviceConfiguration.getCategoriesCoreServiceUrl(),
				HttpMethod.POST,
				request,
				new ParameterizedTypeReference<>() {
				});

		// pass new category to core service
		category = c.getBody();

		// if not null
		if(category != null)
		{
			this.categoryRepository.save(category);
			return new ResponseEntity<>(category, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * Delete a category by id. Also deletes all products associated with this category.
	 */
	@RequestMapping(value = "/categories/{id}", method = RequestMethod.DELETE)
	@HystrixCommand(fallbackMethod = "deleteCategoryFallback", commandProperties = {
			@HystrixProperty(name = "circuitBreaker.requestVolumeThreshold", value = "2"),
			@HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "10000")  })
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deleteCategory(@PathVariable Long id, @RequestHeader("Authorization") String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(headers);

		// delete products first
		ResponseEntity<String> s = restTemplate.exchange(
				serviceConfiguration.getProductsCoreServiceUrl() + "/category/" + id,
				HttpMethod.DELETE,
				request,
				String.class);

		// delete categories
		ResponseEntity<String> s2 = restTemplate.exchange(
				serviceConfiguration.getCategoriesCoreServiceUrl() + "/" + id,
				HttpMethod.DELETE,
				request,
				String.class);

		// if responseCode != 204
		if(s.getStatusCode() != HttpStatus.NO_CONTENT || s2.getStatusCode() != HttpStatus.NO_CONTENT)
		{
			throw new RuntimeException("Failed");
		}

		// update caches
		this.categoryRepository.deleteById(id);

		List<Product> productsToDelete = toList(this.productRepository.findAll()).stream().filter(p -> p.getCategoryId().equals(id)).collect(Collectors.toList());
		this.productRepository.deleteAll(productsToDelete);

		return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
	}

	private List<Category> requestCategories(String token)
	{
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(headers);

		// initialize categories
		ResponseEntity<List<Category>> cr = restTemplate.exchange(
				serviceConfiguration.getCategoriesCoreServiceUrl(),
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});

		// an empty list is not null
		List<Category> categories = cr.getBody();
		if(categories != null)
		{
			categoryRepository.saveAll(categories);
		}

		return categories;
	}

	private List<Product> getProducts(List<Category> categories, String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(headers);

		// initialize products
		ResponseEntity<List<Product>> pr = restTemplate.exchange(
				serviceConfiguration.getProductsCoreServiceUrl(),
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});

		// an empty list is not null
		List<Product> products = pr.getBody();
		if(products != null){

			for (Product product : products) {

				if(categories != null)
				{
					Category c = categories.stream().filter(c2 -> c2.getId().equals(product.getCategoryId())).findFirst()
							.orElse(new Category());

					if(c.getName() == null) continue;
					product.setCategory(c);
				}
			}
			this.productRepository.saveAll(products);
		}

		return products;
	}

	private Product getProduct(Long productId, List<Category> categories, String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(headers);

		// initialize products
		ResponseEntity<Product> pr = restTemplate.exchange(
				serviceConfiguration.getProductsCoreServiceUrl() + "/" + productId,
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});

		// an empty list is not null
		Product product = pr.getBody();
		if(product != null){

			if(categories != null && categories.size() > 0)
			{
				Category c = categories.stream().filter(c2 -> c2.getId().equals(product.getCategoryId())).findFirst()
						.orElse(new Category());

				if(c.getName() != null) product.setCategory(c);
			}
			this.productRepository.save(product);
		}

		return product;
	}

	private Category requestCategory(Long categoryId, String token) {

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", token);

		// create request
		HttpEntity request = new HttpEntity(headers);

		// initialize products
		ResponseEntity<Category> c = restTemplate.exchange(
				serviceConfiguration.getCategoriesCoreServiceUrl() + "/" + categoryId,
				HttpMethod.GET,
				request,
				new ParameterizedTypeReference<>() {
				});

		return c.getBody();
	}

	public ResponseEntity<Product> getProductFallback(Long id, String token) {

		Optional<Product> p = this.productRepository.findById(id);

		return p.map(product -> new ResponseEntity<>(product, HttpStatus.ALREADY_REPORTED)).orElseGet(() -> new ResponseEntity<>(new Product(id), HttpStatus.ALREADY_REPORTED));

	}

	public ResponseEntity<String> deleteProductFallback(Long id, String token) {
		// alert admin
		return new ResponseEntity<>(new Product(id).toString(), HttpStatus.ALREADY_REPORTED);
	}

	public ResponseEntity<Product> createProductFallback(Product product, String token) {
		// alert admin
		return new ResponseEntity<>(product, HttpStatus.ALREADY_REPORTED);
	}

	public ResponseEntity<List<Product>> getProductsFallback(String token) {

		return new ResponseEntity<List<Product>>(toList(this.productRepository.findAll()), HttpStatus.ALREADY_REPORTED);
	}


	public ResponseEntity<List<Product>> searchProductsFallback(String a, Double b, Double c, String token) {
		// alert admin
		return new ResponseEntity<List<Product>>(toList(this.productRepository.findAll()), HttpStatus.ALREADY_REPORTED);
	}

	public static <T> List<T> toList(final Iterable<T> iterable) {
		return StreamSupport.stream(iterable.spliterator(), false)
				.collect(Collectors.toList());
	}


	public ResponseEntity<String> deleteCategoryFallback(Long id, String token) {
		// alert admin
		return new ResponseEntity<>(id.toString(), HttpStatus.ALREADY_REPORTED);
	}

	public ResponseEntity<Category> createCategoryFallback(Category category, String token) {
		// alert admin
		return new ResponseEntity<>(category, HttpStatus.ALREADY_REPORTED);
	}


	public ResponseEntity<List<Category>> getCategoriesFallback(String token) {
		// alert admin
		return new ResponseEntity<>(toList(this.categoryRepository.findAll()), HttpStatus.ALREADY_REPORTED);
	}
}
