package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "Invoices")
@NoArgsConstructor @ToString @EqualsAndHashCode
@Schema(description = "Entity representing an Invoice")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    @Schema(description = "ID of the invoice", type = "integer", format = "int64", example = "1")
    private Long invoiceId;

    @Getter @Setter
    @Schema(description = "Timestamp when the invoice was created", example = "2023-07-20T14:34:22")
    private LocalDateTime createdAt;

    @Getter @Setter
    @Schema(description = "Total amount of the invoice", example = "123.45")
    private Double total;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    @JsonIgnore
    @Getter @Setter
    @Schema(description = "The client associated with the invoice")
    private Client client;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    @Schema(description = "List of items in the invoice")
    private List<InvoiceItem> items;
}
