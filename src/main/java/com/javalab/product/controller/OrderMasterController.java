package com.javalab.product.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.javalab.product.dto.OrderItemDTO;
import com.javalab.product.dto.OrderMasterDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.dto.ProductDTO;
import com.javalab.product.entity.OrderMaster;
import com.javalab.product.service.OrderMasterService;
import com.javalab.product.service.ProductService;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/order")
@Slf4j
public class OrderMasterController {
	
	private final OrderMasterService orderMasterService;
	// 주문화면에서 상품 드롭다운 리스트 데이터 생성하기 위한 용도
	private final ProductService productService;
	
	public OrderMasterController(OrderMasterService orderMasterService,
								ProductService productService) {
		this.orderMasterService = orderMasterService;
		this.productService = productService;
	}

	@GetMapping("/list")
	public void getList(PageRequestDTO pageRequestDTO, Model model) {
		PageResultDTO<OrderMasterDTO, OrderMaster> result = orderMasterService.getList(pageRequestDTO);
		model.addAttribute("result", result);
	}

	@GetMapping("/read")
	public void getOrderMasterById(@RequestParam Integer orderId, Model model) {
		log.info("getOrderMasterById");
		OrderMasterDTO dto = orderMasterService.read(orderId);
		log.info("getOrderMasterById : " + dto.toString());
		model.addAttribute("orderMaster", dto);
	}

	@GetMapping("/register")
	public void registerForm(@ModelAttribute("orderMasterDTO") OrderMasterDTO orderMasterDTO,
							BindingResult bindingResult, 
							PageRequestDTO pageRequestDTO, 
							Model model) {
		
		model.addAttribute("orderMasterDTO", new OrderMasterDTO());
		
		// 상품 목록 조회해서 전달
        List<ProductDTO> productList = productService.getList();
        model.addAttribute("productList", productList);
	}
	
	// 상품추가 버튼 클릭시 추가된 상품 드롭다운 리스트에 채울 값을 생성하는 메소드
	@GetMapping("/product")
	@ResponseBody
	public List<ProductDTO> productListForDropdown() {
		
		// 상품 목록 조회해서 전달
		List<ProductDTO> productList = productService.getList();
		return productList;
	}

	// 저장 처리[일반적인 방식]
//	@PostMapping("/register")
//	public String registerOrder(@ModelAttribute("orderMasterDTO") OrderMasterDTO orderMasterDTO,
//								BindingResult bindingResult,
//								PageRequestDTO pageRequestDTO, 
//								Model model) {
//	    
//		log.info("orderMasterDTO.toString() : " + orderMasterDTO.toString());
//		
//		
//		if (bindingResult.hasErrors()) {
//			log.info("주문 정보에 오류가 있습니다.");
//			// 오류 처리 로직
//			// 다시 입력 페이지로 이동
//			return "/order/register";
//	    }
//	    
//		// 저장작업 OrderMaster and OrderItems
//	    orderMasterService.saveOrderMaster(orderMasterDTO); 
//	    
//	    // 목록으로 이동
//	    return "redirect:/order/list";
//	}
	
	/*
	 * 저장처리 메소드[Rest 방식]
	 * - @RequestBody :  화면에서 ajax방식의 JSON Type으로 넘어오는
	 *   데이터를 커맨드 객체에 자동으로 바인딩 해준다.
	 *   바인딩 - 특정객체의 값을 받아 해당 객체에 값을 할당해주는 것
	 */
	@PostMapping("/register")
	@ResponseBody
	public String registerOrder(@RequestBody OrderMasterDTO orderMasterDTO) {

		log.info("저장 처리 시작 : " + orderMasterDTO.toString());
		/*
		 * register.html에서 선택한 상품 정보를 가져올 때 가격은 가져오지 않는다.
		 * 그러므로 여기에서 productId(상품아이디)를 사용해서 가격 정보를 저장한다.
		 */
		// 주문하는 각 상품의 수량과 가격을 합산하는 메소드
		orderMasterDTO = calcTotalAmt(orderMasterDTO);
		
		System.out.println("Controller getTotalAmt : " + orderMasterDTO.getTotalAmt());
	    
	    orderMasterService.register(orderMasterDTO);
	        
	    return "저장 작업 성공";
//	    return "redirect:/order/list";
	}
	
	/*
	 * register.html에서 주문하는 상품의 정보를 넘겨 받을때 '가격정보'는 존재하지 않는다.
	 * 그래서 ordermasterDTO에 들어 있는 orderItemList에서 각 요소의 productId 값을 사용하여
	 * 상품의 가격(price)을 추출한 뒤 각 주문(orderItem)의 구매수량(quantity)을 합산하여
	 * 한 상품의 총 가격을 구한다.
	 * 그리고 orderMasterDTO의 totalAmt(주문 총 가격)에 누적 합산한다.
	 */
	private OrderMasterDTO calcTotalAmt(OrderMasterDTO dto) {
		List<OrderItemDTO> list = dto.getOrderItems();
		Integer totalAmt = 0;
		
		for(OrderItemDTO item : list) {
			/*
			 *  주문 아이템 멤버의 productId를 통해서 가격을 불러오고 합산함
			 */
			ProductDTO product = productService.read(item.getProductId());
			Integer price = product.getPrice();
			Integer quantity = item.getQuantity();
			Integer totalPrice = price * quantity;
			item.setTotalAmt(totalAmt);
			totalAmt += totalPrice;
		}
		dto.setOrderItems(list);	// 각 개별 주문의 가격을 세팅한 리스트를 저장
		dto.setTotalAmt(totalAmt);	// 전체 주문의 가격을 저장
		return dto;
	}
	
	@GetMapping("/delete")
	public String delete(@RequestParam Integer orderId) {
		
		orderMasterService.remove(orderId);
		
		return "redirect:/order/list";
	}
	
	@GetMapping("/modify")
	public void modify(@RequestParam("orderId") Integer orderId,
			@ModelAttribute("orderMasterDTO") OrderMasterDTO orderMasterDTO, BindingResult bindingResult, Model model) {
		log.info("modify - get");

		OrderMasterDTO dto = orderMasterService.read(orderId);
		model.addAttribute("orderMaster", dto);
	}
	
	
	@PostMapping("/modify")
	public String modify(@ModelAttribute @Valid OrderMasterDTO orderMasterDTO, BindingResult bindingResult,
			Model model) {
		log.info("modify - post dto: " + orderMasterDTO.toString());

		if (bindingResult.hasErrors()) {
			List<FieldError> fieldErrors = bindingResult.getFieldErrors();
			for (FieldError error : fieldErrors) {
				log.error(error.getField() + " " + error.getDefaultMessage());
			}

			model.addAttribute("orderMaster", orderMasterDTO);
			return "order/modify";
		}

		orderMasterService.modify(orderMasterDTO);

		return "redirect:/order/list";
	}

}
