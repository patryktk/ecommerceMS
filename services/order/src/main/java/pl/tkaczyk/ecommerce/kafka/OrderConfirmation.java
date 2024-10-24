package pl.tkaczyk.ecommerce.kafka;

import pl.tkaczyk.ecommerce.customer.CustomerResponse;
import pl.tkaczyk.ecommerce.order.PaymentMethod;
import pl.tkaczyk.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation(
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products
) {
}
