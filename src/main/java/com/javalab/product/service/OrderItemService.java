package com.javalab.product.service;

import com.javalab.product.dto.OrderItemDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.OrderItem;
import com.javalab.product.entity.Product;

public interface OrderItemService {

    PageResultDTO<OrderItemDTO, OrderItem> getList(PageRequestDTO requestDTO);
    OrderItemDTO read(Integer orderItemId);
    OrderItem register(OrderItemDTO orderItemDTO);
    void modify(OrderItemDTO orderItemDTO);
    boolean remove(Integer orderItemId);

    default OrderItem dtoToEntity(OrderItemDTO orderItemDTO) {
    	
		// 화면에서 전달된 categoryId로 카테고리 객체 생성
		Product product = Product.builder()
						.productId(orderItemDTO.getProductId())
						.build();

    	return OrderItem.builder()
    			.orderItemId(orderItemDTO.getOrderItemId())
    			.product(product)
    			.quantity(orderItemDTO.getQuantity())
    			.totalAmt(orderItemDTO.getTotalAmt())
    			.build();
    }

    default OrderItemDTO entityToDto(OrderItem orderItem) {
        return OrderItemDTO.builder()
                .orderItemId(orderItem.getOrderItemId())
                .productId(orderItem.getProduct().getProductId())
                .quantity(orderItem.getQuantity())
                .totalAmt(orderItem.getTotalAmt())
                .build();
    }
}

