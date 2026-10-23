package utfpr.api_pedido.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime date;
    private Double amount;
    private String zipCode;
    private Double shippingValue;
    private Integer deliveryDays;
    private String status;

    @OneToMany(mappedBy = "pedido")
    private List<ItemPedido> items;
}
