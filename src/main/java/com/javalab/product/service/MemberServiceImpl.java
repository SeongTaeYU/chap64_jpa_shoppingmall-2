package com.javalab.product.service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.javalab.product.dto.MemberDTO;
import com.javalab.product.dto.PageRequestDTO;
import com.javalab.product.dto.PageResultDTO;
import com.javalab.product.entity.Member;
import com.javalab.product.repository.MemberRepository;

@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;

    public MemberServiceImpl(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public PageResultDTO<MemberDTO, Member> getList(PageRequestDTO requestDTO) {
        Pageable pageable = requestDTO.getPageable(Sort.by("email").descending());
        Page<Member> result = memberRepository.findAll(pageable);
        Function<Member, MemberDTO> fn = member -> entityToDto(member);
        return new PageResultDTO<>(result, fn);
    }


    @Override
    public List<MemberDTO> getList() {
        List<Member> entityList = memberRepository.findAll();
        return entityList.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public MemberDTO read(String email) {
        Optional<Member> member = memberRepository.findById(email);
        return member.map(mem -> entityToDto(mem)).orElse(null);
    }
    
    @Override
    public Member register(MemberDTO dto) {
        Member entity = dtoToEntity(dto);
        return memberRepository.save(entity);
    }

    @Override
    public void modify(MemberDTO memberDTO) {
        Optional<Member> member = memberRepository.findById(memberDTO.getEmail());
        member.ifPresent(existingMember -> {
            existingMember.setPassword(memberDTO.getPassword());
            existingMember.setName(memberDTO.getName());
            memberRepository.save(existingMember);
        });
    }

    @Override
    public boolean remove(String email) {
        Optional<Member> member = memberRepository.findById(email);
        if (member.isPresent()) {
            memberRepository.deleteById(email);
            return true;
        } else {
            return false;
        }
    }

    /*
    public MemberDTO entityToDto(Member member) {
        return MemberDTO.builder()
                .email(member.getEmail())
                .password(member.getPassword())
                .name(member.getName())
                .address(member.getAddress())
                .regDate(member.getRegDate())
                .build();
    }

    public Member dtoToEntity(MemberDTO dto) {
        return Member.builder()
                .email(dto.getEmail())
                .password(dto.getPassword())
                .name(dto.getName())
                .address(dto.getAddress())
                .build();
    }
*/

}
