package com.commerce.abm.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "products")
@NoArgsConstructor @ToString @EqualsAndHashCode
@Schema(description = "Entity representing a Product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    @Schema(description = "ID of the product", type = "integer", format = "int64", example = "1")
    private Long id;

    @Getter @Setter
    @Schema(description = "Name of the product", example = "Dome Camera")
    private String product;

    @Getter @Setter
    @Schema(description = "Price of the product", example = "999.99")
    private Double price;

    @Getter @Setter
    @Schema(description = "Stock of the product", example = "99")
    private Integer stock;
}
