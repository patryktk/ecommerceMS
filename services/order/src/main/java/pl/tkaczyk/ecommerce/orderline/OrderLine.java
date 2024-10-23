package pl.tkaczyk.ecommerce.orderline;

import jakarta.persistence.*;
import lombok.*;
import pl.tkaczyk.ecommerce.order.Order;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Entity
public class OrderLine {

    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
    private Integer productId;
    private double quantity;
}
