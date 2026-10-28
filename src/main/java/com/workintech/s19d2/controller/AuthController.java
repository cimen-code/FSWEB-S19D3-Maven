package com.workintech.s19d2.controller;

import com.workintech.s19d2.dto.RegisterResponse;
import com.workintech.s19d2.dto.RegistrationMember;
import com.workintech.s19d2.entity.Member;
import com.workintech.s19d2.service.AuthenticationService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public RegisterResponse register(
            @RequestBody RegistrationMember request) {

        Member member = authenticationService.register(
            request.email(),
            request.password()
        );

        return new RegisterResponse(
            member.getEmail(),
            "kayıt başarılı bir şekilde gerçekleşti."
        );
    }
}
