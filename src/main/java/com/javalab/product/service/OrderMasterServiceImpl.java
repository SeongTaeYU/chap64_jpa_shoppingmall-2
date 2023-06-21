package com.javalab.product.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.javalab.product.dto.OrderItemDTO;
import com.javalab.product.dto.OrderMasterDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Member;
import com.javalab.product.entity.OrderItem;
import com.javalab.product.entity.OrderMaster;
import com.javalab.product.repository.OrderItemRepository;
import com.javalab.product.repository.OrderMasterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
//@Transactional
@Slf4j
public class OrderMasterServiceImpl implements OrderMasterService {

    private final OrderMasterRepository orderMasterRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderMasterServiceImpl(OrderMasterRepository orderMasterRepository,
    							OrderItemRepository orderItemRepository) {
        this.orderMasterRepository = orderMasterRepository;
        this.orderItemRepository = orderItemRepository;
    }

    @Override
    public PageResultDTO<OrderMasterDTO, OrderMaster> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("orderId").descending());
        Page<OrderMaster> result = orderMasterRepository.findAll(pageable);
        Function<OrderMaster, OrderMasterDTO> fn = this::entityToDto;
        return new PageResultDTO<>(result, fn);
    }

    @Override
    public OrderMasterDTO read(Integer orderId) {
        Optional<OrderMaster> orderMaster = orderMasterRepository.findById(orderId);
        return orderMaster.map(this::entityToDto).orElse(null);
    }

    /*
     * 저장
     * @Transactional 
     *  : 마스터 저장후 아이템 저장을 하나의 트랜잭션으로 묶음
     *  ServiceLayer에서 설정 작업이 하나라도 완료되지 않으면 rollback
     */
    @Override
    @Transactional
    public void register(OrderMasterDTO orderMasterDTO) {
    	
     	/*
    	 * !!! orderMasterDTO가 값을 못 가져옴.
    	 */
        // [1] OrderMasterDTO DTO -> OrderMaster Entity 변환
        OrderMaster orderMaster = dtoToEntity(orderMasterDTO);
        // 이 부분에서 totalAmt 안들어감
        
        log.info("ServiceImpl register : " + orderMaster.toString());
        
        // 우선 OrderMaster 저장
        // OrderId가 저장된다.
        /*
         *   OrderMaster를 먼저 저장하고 저장할 때의 내부 정보를 활용하기 위해서
         *   변수에 반환을 받아 놓음.
         */
//        OrderMaster savedOrderMaster = orderMasterRepository.save(orderMaster);	23_0621 변경

        // OrderItemDTO -> OrderItem Entity 변환 및 연결  Item 만들기
        List<OrderItem> orderItems = orderMasterDTO.getOrderItems().stream()
                .map(item -> {
                    OrderItem orderItem = orderItemDtoToEntity(item);	//orderItemDtoToEntity : DTO -> entity로 변환
                    // 저장된 엔티티를 할당, 거기에 orderId 있음, 
                    // 그 orderId가 orderItem테이블에 들어감
//                    orderItem.setOrderMaster(savedOrderMaster); 	2023_06_21 2번저장 때문에 주석처리
                    orderItem.setOrderMaster(orderMaster); 
                    return orderItem;
                }).collect(Collectors.toList());
          orderMaster.setOrderItems(orderItems);
          
//        savedOrderMaster.setOrderItems(orderItems);	2023_06_21 2번저장 때문에 주석처리
        OrderMaster saveOrderMaster = orderMasterRepository.save(orderMaster);
        // OrderItem들 저장
//        orderItemRepository.saveAll(orderItems);	//Spring JPA 가 알아서 저장해준다.
     // OrderItemDTO -> OrderItem Entity 변환 및 연결
        // OrderItem들 저장
        /*
         *   기본적으로 만들어져 있는 saveAll 메소드
         *   : save와의 차이점은 Iterator를 통해서 모든 객체를 
         *   한 번에 받아서 각각 저장함.
         */
        
    }
    
    /* 
     * 주문 마스터 수정
     */
    @Override
    public void modify(OrderMasterDTO orderMasterDTO) {
        Optional<OrderMaster> orderMaster = orderMasterRepository.findById(orderMasterDTO.getOrderId());

        // 화면에서 전달된 이메일(pk)로 회원 정보 테이블에서 회원 조회
        Member member = Member.builder().email(orderMasterDTO.getEmail()).build();

        orderMaster.ifPresent(orderMasterEntity -> {
            OrderMaster updatedOrderMaster = dtoToEntity(orderMasterDTO);
            updatedOrderMaster.setMember(member);
            orderMasterRepository.save(updatedOrderMaster);
        });
    }
    
    @Override
    public boolean remove(Integer orderId) {
        Optional<OrderMaster> orderMaster = orderMasterRepository.findById(orderId);
        if (orderMaster.isPresent()) {
            orderMasterRepository.deleteById(orderId);
            return true;
        } else {
            return false;
        }
    }

}
