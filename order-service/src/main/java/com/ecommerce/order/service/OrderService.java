package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderRequest;
import com.ecommerce.order.dto.OrderResponse;

public interface OrderService {
	OrderResponse PlaceOrder(OrderRequest request);
}
