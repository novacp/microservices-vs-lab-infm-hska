package de.hska.iwi.vslab.catalog.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@RedisHash("Category")
@Data
public class Category implements Serializable {

    private static final long serialVersionUID = -4893542110975926179L;

    @Id
    protected Long id;

    protected String name;

    public Category() {
    }

}
