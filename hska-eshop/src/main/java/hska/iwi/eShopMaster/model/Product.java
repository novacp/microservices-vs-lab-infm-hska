package hska.iwi.eShopMaster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    protected Long id;
    protected Long categoryId;
    protected String name;
    protected Double price;
    protected String details;

    private Category category;

    public Product(Long id, Long categoryId, String name, Double price, String details) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.price = price;
        this.details = details;
    }
}
