package pl.tkaczyk.ecommerce.order;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pl.tkaczyk.ecommerce.customer.CustomerClient;
import pl.tkaczyk.ecommerce.exception.BusinessException;
import pl.tkaczyk.ecommerce.kafka.OrderConfirmation;
import pl.tkaczyk.ecommerce.kafka.OrderProducer;
import pl.tkaczyk.ecommerce.orderline.OrderLineRequest;
import pl.tkaczyk.ecommerce.orderline.OrderLineService;
import pl.tkaczyk.ecommerce.payment.PaymentClient;
import pl.tkaczyk.ecommerce.payment.PaymentRequest;
import pl.tkaczyk.ecommerce.product.ProductClient;
import pl.tkaczyk.ecommerce.product.PurchaseRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;
    private final CustomerClient customerClient;
    private final ProductClient productClient;
    private final OrderMapper mapper;
    private final OrderLineService orderLineService;
    private final OrderProducer orderProducer;
    private final PaymentClient paymentClient;

    public Integer createOrder(OrderRequest request) {
        //Feign
        var customer = customerClient.findById(request.customerId()).orElseThrow(() -> new BusinessException("Cannot create order:: No customer found"));

        //RestTemplate
        var purchaseProducts = productClient.purchaseProducts(request.products());

        var order = repository.save(mapper.toOrder(request));

        for(PurchaseRequest purchaseRequest: request.products()){
            orderLineService.saveOrderLine(
                    new OrderLineRequest(null, order.getId(), purchaseRequest.productId(), purchaseRequest.quantity())
            );
        }

        var paymentRequest = new PaymentRequest(
                request.amount(),
                request.paymentMethod(),
                order.getId(),
                order.getReference(),
                customer
        );
        paymentClient.requestOrderPayment(paymentRequest);

        orderProducer.sendOrderConfirmation(
                new OrderConfirmation(
                        request.reference(),
                        request.amount(),
                        request.paymentMethod(),
                        customer,
                        purchaseProducts
                )
        );

        return order.getId();
    }

    public List<OrderResponse> findAll() {
        return repository.findAll()
                .stream()
                .map(mapper::fromOrder)
                .collect(Collectors.toList());
    }

    public OrderResponse findById(Integer orderId) {
        return repository.findById(orderId)
                .map(mapper::fromOrder)
                .orElseThrow(() -> new EntityNotFoundException(String.format("No order found with the provided ID: %d", orderId)));
    }
}
