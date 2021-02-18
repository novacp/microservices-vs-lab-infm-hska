package de.hska.iwi.vslab.products.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Data
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Long id;

    @Column(name = "categoryId", nullable = false)
    protected Long categoryId;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "price", nullable = false)
    protected Double price;

    @Column(name = "details", nullable = false)
    protected String details;

    public Product() {
    }

    public Product(Long id, Long categoryId, String name, Double price, String details) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.details = details;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return this.categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    @Override
    public String toString() {
        return ("{" + " id='" + getId() + "'" + ", categoryID='" + getCategoryId() + "'" + ", name='" + getName() + "'"
                + ", price='" + getPrice() + "'" + ", details='" + getDetails() + "'" + "}");
    }
}
