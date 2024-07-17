package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@NoArgsConstructor @ToString @EqualsAndHashCode
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter private Long id;

    @Getter @Setter private String product;

    @Getter @Setter private Double price;

    @Getter @Setter private Integer stock;

    @ManyToMany(mappedBy = "products")
    @JsonBackReference("cart-products")
    @Getter @Setter private List<Cart> carts = new ArrayList<>();
}
