package com.commerce.abm.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts")
@NoArgsConstructor @ToString @EqualsAndHashCode
@Schema(description = "Entity representing a Cart")
public class Cart {
    @Id
    @Getter @Setter
    @Schema(description = "ID of the cart", type = "integer", format = "int64", example = "1")
    private Long cartId;

    @Getter @Setter
    @Schema(description = "Indicates whether the cart has been delivered", example = "false")
    private boolean delivered;

    @Getter @Setter
    @Schema(description = "Timestamp of the last update", example = "2023-07-20T14:34:22")
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "client_id")
    @JsonIgnore
    @Getter @Setter
    @Schema(description = "The client associated with the cart")
    private Client client;

    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    @Getter @Setter
    @Schema(description = "List of items in the cart")
    private List<CartItem> items = new ArrayList<>();
}
