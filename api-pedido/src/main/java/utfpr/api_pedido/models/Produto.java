package utfpr.api_pedido.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Produto {
    private Long id;
    private String description;
    private Integer quantity;
    private Double price;
    private String category;
}