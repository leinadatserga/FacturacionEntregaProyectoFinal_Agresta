package com.commerce.abm.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clients")
@NoArgsConstructor @ToString @EqualsAndHashCode
@Schema(description = "Entity representing a Client")
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    @Schema(description = "ID of the client", type = "integer", format = "int64", example = "1")
    private Long id;

    @Getter @Setter
    @Schema(description = "Name of the client", example = "Daniel")
    private String name;

    @Getter @Setter
    @Schema(description = "Last name of the client", example = "Agresta")
    private String lastname;

    @Getter @Setter
    @Schema(description = "Document number of the client", example = "12345678")
    private Integer docnumber;

    @Getter @Setter
    @Schema(description = "Age of the client", example = "96")
    private Integer age;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Getter @Setter
    @Schema(description = "List of carts associated with the client")
    private List<Cart> carts = new ArrayList<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @Getter @Setter
    @Schema(description = "List of invoices associated with the client")
    private List<Invoice> invoices = new ArrayList<>();
}
