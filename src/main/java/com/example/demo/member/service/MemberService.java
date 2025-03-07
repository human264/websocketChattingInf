package com.example.demo.member.service;

import com.example.demo.member.domain.Member;
import com.example.demo.member.domain.MemberLoginReqDto;
import com.example.demo.member.dto.MemberSaveReqDto;
import com.example.demo.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.internal.StandardEntityNotFoundDelegate;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor

public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public Member create(MemberSaveReqDto memberSaveReqDto) {
        if (memberRepository.findByEmail(memberSaveReqDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 가입된 회원입니다.");
        }
        Member newMember = Member.builder()
                .name(memberSaveReqDto.getName())
                .email(memberSaveReqDto.getEmail())
                .password(passwordEncoder.encode(memberSaveReqDto.getPassword()))
                .build();
        Member member = memberRepository.save(newMember);

        return member;
    }

    public Member login(MemberLoginReqDto memberLoginReqDto) {
        Member member = memberRepository.findByEmail(memberLoginReqDto.getEmail()).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 이메일입니다."));

        if(!passwordEncoder.matches(memberLoginReqDto.getPassword(), member.getPassword())) {
            throw new IllegalIdentifierException("비밀번호가 일치하지 않습니다.");
        }
        return member;
    }
}
