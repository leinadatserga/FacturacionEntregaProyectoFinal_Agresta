package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cart_items")
@NoArgsConstructor @ToString @EqualsAndHashCode
@Schema(description = "Entity representing a Cart Item")
public class CartItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    @Schema(description = "ID of the cart item", type = "integer", format = "int64", example = "1")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cart_id")
    @JsonIgnore
    @Getter @Setter
    @Schema(description = "The cart associated with the cart item")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonIgnore
    @Getter @Setter
    @Schema(description = "The product associated with the cart item")
    private Product product;

    @Getter @Setter
    @Schema(description = "Quantity of the product", example = "2")
    private Integer quantity;

    @Getter @Setter
    @Schema(description = "Price of the product, or total if quantity > 1", example = "29.99")
    private Double price;

    @JsonProperty("product_id")
    @Schema(description = "ID of the product", type = "integer", format = "int64", example = "1")
    public Long getProductId() {
        return product.getId();
    }
}
