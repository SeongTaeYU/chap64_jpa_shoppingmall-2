package com.javalab.product.service;

import java.util.stream.Collectors;

import javax.validation.Valid;

import com.javalab.product.dto.OrderItemDTO;
import com.javalab.product.dto.OrderMasterDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Member;
import com.javalab.product.entity.OrderItem;
import com.javalab.product.entity.OrderMaster;
import com.javalab.product.entity.Product;

public interface OrderMasterService {

	PageResultDTO<OrderMasterDTO, OrderMaster> getList(PageRequestDTO requestDTO);

	OrderMasterDTO read(Integer orderId);

	void register(OrderMasterDTO orderMasterDTO);

	void modify(OrderMasterDTO orderMasterDTO);

	boolean remove(Integer orderId);

	// 화면에서 입력된 주문정보DTO -> 주문 Entity 전환
	default OrderMaster dtoToEntity(OrderMasterDTO orderMasterDTO) {
		Member member = Member.builder()
				.email(orderMasterDTO.getEmail())
				.build();
		
		return OrderMaster.builder()
				.address(orderMasterDTO.getAddress())
				.orderItems(orderMasterDTO.getOrderItems().stream()
				.map(orderItem -> orderItemDtoToEntity(orderItem))
				.collect(Collectors.toList()))
				.member(member)
				.totalAmt(orderMasterDTO.getTotalAmt())
				.build();
	}

	// 데이터베이스에 조회한 주문정보 엔티티 -> 주문DTO로 전환
	default OrderMasterDTO entityToDto(OrderMaster orderMaster) {
	    return OrderMasterDTO.builder()
	            .orderId(orderMaster.getOrderId())
	            .email(orderMaster.getMember().getEmail())
	            .name(orderMaster.getMember().getName())
	            .address(orderMaster.getAddress())
	            .orderItems(orderMaster.getOrderItems().stream()
	                    .map(orderItem -> orderItemEntityToDto(orderItem))
	                    .collect(Collectors.toList()))
	            .totalAmt(orderMaster.getTotalAmt())
	            .regDate(orderMaster.getRegDate())
	            .modDate(orderMaster.getModDate())
	            .build();
	}
	


	// 주문 Item 정보 엔티티 -> 주문 Item 정보 DTO로 전환
	default OrderItemDTO orderItemEntityToDto(OrderItem orderItem) {
		return OrderItemDTO.builder()
				.orderItemId(orderItem.getOrderItemId())
				.productId(orderItem.getProduct().getProductId())
				.quantity(orderItem.getQuantity())
				.totalAmt(orderItem.getTotalAmt())
				.build();
	}

	// 주문 Item DTO -> 주문 Item Entity
	default OrderItem orderItemDtoToEntity(OrderItemDTO orderItemDTO) {
	    // Convert OrderItemDTO to OrderItem entity
	    return OrderItem.builder()
	            .orderItemId(orderItemDTO.getOrderItemId())
	            .product(Product.builder()
	            		.productId(orderItemDTO.getProductId())
	            		.build())
	            .quantity(orderItemDTO.getQuantity())
	            .totalAmt(orderItemDTO.getTotalAmt())
	            .build();
	}
	
}
