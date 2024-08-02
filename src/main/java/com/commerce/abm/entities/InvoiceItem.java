package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "invoice_items")
@NoArgsConstructor @ToString @EqualsAndHashCode
@Schema(description = "Entity representing an Invoice Item")
public class InvoiceItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    @Schema(description = "ID of the invoice item", type = "integer", format = "int64", example = "1")
    private Long invoiceItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id")
    @JsonIgnore
    @Getter @Setter
    @Schema(description = "The invoice associated with the invoice item")
    private Invoice invoice;

    @Getter @Setter
    @Schema(description = "Id of the product", example = "2")
    private Long productId;

    @Getter @Setter
    @Schema(description = "Quantity of the product", example = "2")
    private Integer quantity;

    @Getter @Setter
    @Schema(description = "Price of the product", example = "29.99")
    private Double price;
}
