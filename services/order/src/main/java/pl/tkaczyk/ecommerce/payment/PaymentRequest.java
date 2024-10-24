package pl.tkaczyk.ecommerce.payment;

import pl.tkaczyk.ecommerce.customer.CustomerResponse;
import pl.tkaczyk.ecommerce.order.PaymentMethod;

import java.math.BigDecimal;

public record PaymentRequest(
        BigDecimal amount,
        PaymentMethod paymentMethod,
        Integer orderId,
        String orderReference,
        CustomerResponse customer
) {
}
