package com.example.demo.member.controller;

import com.example.demo.member.common.auth.JwtTokenProvider;
import com.example.demo.member.domain.Member;
import com.example.demo.member.domain.MemberListRestDto;
import com.example.demo.member.domain.MemberLoginReqDto;
import com.example.demo.member.dto.MemberSaveReqDto;
import com.example.demo.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/create")
    public ResponseEntity<?> memberCreate(@RequestBody MemberSaveReqDto memberSaveReqDto) {
        Member member = memberService.create(memberSaveReqDto);
        return new ResponseEntity<>(member.getId(), HttpStatus.CREATED);
    }

    @PostMapping("/doLogin")
    public ResponseEntity<?> memberCreate(@RequestBody MemberLoginReqDto memberLoginReqDto) {
        Member member = memberService.login(memberLoginReqDto);
        String jwtToken = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        Map<String, Object> loginInfo = new HashMap<>();
        loginInfo.put("id", member.getId());
        loginInfo.put("token", jwtToken);
        return new ResponseEntity<>(loginInfo, HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<?> memberList() {
        List<MemberListRestDto> memberList = memberService.findAll();
        return new ResponseEntity<>(memberList, HttpStatus.OK);
    }

}
