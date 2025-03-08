package com.example.demo.member.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberListRestDto {

    private Long id;
    private String name;
    private String email;

}
