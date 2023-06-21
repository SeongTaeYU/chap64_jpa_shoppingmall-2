package com.javalab.product.entity;


import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 상품 엔티티 클래스
 *  - EntityManager에 의해서 관리되는 클래스
 *  @Id	어노테이션은 import javax.persistence.Id; [주의]
 *  @ToString(exclude = "category") 
 *   : category 멤버 변수는 ToString()에서 제외하겠다.
 *     제외하지 않으면 toString()시 모든 데이터를 갖고 오기 위해서
 *     데이터베이스에 쿼리하게 됨.(지연로딩 방해)
 *
 */
@Builder
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tbl_product")
@ToString(exclude = "category") // category 멤버 변수는 ToString()에서 제외하겠다.  
public class Product extends BaseEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(length=10)
	private Integer productId;
	
	@NotNull
	@Size(min=8, 
		  max=50, 
		  message="상품명은 8~50자입니다")
	@Column(length = 50, nullable=false)
	private String productName;
	
	@Column(columnDefinition = "integer(10) default 0")
	private Integer price;
	
	// 외래키 - 카테고리 테이블 참조함.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId")
    private Category category;
	
	@Column(length = 100)
	private String description;
	
    //@Column(columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    //private LocalDateTime regDate = LocalDateTime.now(); // 현재일자
	
	// 추가됨.
    private LocalDateTime regDate= LocalDateTime.now();;
    private LocalDateTime modDate= LocalDateTime.now();;
	
}
