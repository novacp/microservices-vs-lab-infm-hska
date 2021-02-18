package de.hska.iwi.vslab.catalog.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

@RedisHash("Product")
@Data
public class Product implements Serializable {

    private static final long serialVersionUID = 6792049098778217689L;

    @Id
    protected Long id;

    protected Long categoryId;

    private Category category;

    @NotNull
    protected String name;
    protected Double price;
    protected String details;

    public Product() {
    }

    public Product(Long id) {
        this.id = id;
        this.categoryId = 0L;
        this.category = new Category();
        this.name = "fallback";
        this.price = 0.0;
        this.details = "fallback";
    }

    public Product(Long id, Long categoryId, String name, Double price, String details) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.details = details;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return this.price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDetails() {
        return this.details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

}
