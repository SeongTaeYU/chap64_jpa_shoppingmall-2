package com.javalab.product.dto;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {

	// 주문Item ID
    private Integer orderItemId;
    
    // 주문OrderID
    private Integer orderId;    
    
    @NotNull(message = "상품은 필수입력입니다.")
    private Integer productId;

    @NotNull(message = "수량은 필수입력입니다.")
    @Positive(message = "수량은 양수여야 합니다.")
    private Integer quantity;

    // 가격은 상품 엔티티 참조
    private Integer price;
    
    private Integer totalAmt;

}
