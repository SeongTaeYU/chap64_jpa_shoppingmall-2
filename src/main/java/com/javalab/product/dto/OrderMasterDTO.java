package com.javalab.product.dto;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderMasterDTO {
	
	// 주문번호
    private Integer orderId;
    
    // 주문자 이메일(회원ID)
    @NotNull(message = "Member ID 필수 입력")
    private String email;
    // 주문자명
    private String name;

    // 배송지 주소
    @NotEmpty(message = "배송 주소 필수 입력")
    private String address;
    
    // 주문 상품들
    /*
     * 
     * 	리스트에 @Valid 어노테이션이 붙어 있어서 각각의 아이템(OrderItemDTO)마다 
     * 	검사를 시도할 수 있음. 
     * 
     *  th:error에서 사용시에 'OrderMasterDTO.orderItems[0]...[N]'의 이름으로 검사 가능. 
     */
    @NotEmpty(message = "주문 아이템 필수 입력")
    @Valid
    private List<OrderItemDTO> orderItems;

    // 총 주문금액
    @Builder.Default
    private Integer totalAmt = 0;
    
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    
}
