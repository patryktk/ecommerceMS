package pl.tkaczyk.ecommerce.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tkaczyk.ecommerce.customer.CustomerClient;
import pl.tkaczyk.ecommerce.exception.BusinessException;
import pl.tkaczyk.ecommerce.orderline.OrderLineRequest;
import pl.tkaczyk.ecommerce.orderline.OrderLineService;
import pl.tkaczyk.ecommerce.product.ProductClient;
import pl.tkaczyk.ecommerce.product.PurchaseRequest;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;

    public Integer createOrder(OrderRequest request) {
        //Feign
        var customer = customerClient.findById(request.customerId()).orElseThrow(() -> new BusinessException("Cannot create order:: No customer found"));

        //RestTemplate
        productClient.purchaseProducts(request.products());

        var order = repository.save(mapper.toOrder(request));

        for(PurchaseRequest purchaseRequest: request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(null, order.getId(), purchaseRequest.productId(), purchaseRequest.quantity())
            );
        }
        return null;
    }
}
