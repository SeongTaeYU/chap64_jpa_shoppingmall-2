package com.javalab.product.service;

import java.util.List;

import com.javalab.product.dto.MemberDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Member;

public interface MemberService {

    PageResultDTO<MemberDTO, Member> getList(PageRequestDTO requestDTO);
    List<MemberDTO> getList();
    MemberDTO read(String email);
    Member register(MemberDTO memberDTO);
    void modify(MemberDTO memberDTO);
    boolean remove(String email);

    default Member dtoToEntity(MemberDTO memberDTO) {
        return Member.builder()
                .email(memberDTO.getEmail())
                .password(memberDTO.getPassword())
                .name(memberDTO.getName())
                .address(memberDTO.getAddress())
                .build();
    }

    default MemberDTO entityToDto(Member member) {
        return MemberDTO.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .address(member.getAddress())
                .regDate(member.getRegDate())
                .build();
    }
}
