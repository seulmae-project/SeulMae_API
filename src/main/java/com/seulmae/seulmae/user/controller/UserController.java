package com.seulmae.seulmae.user.controller;

import com.seulmae.seulmae.global.util.enums.SuccessCode;
import com.seulmae.seulmae.global.util.enums.SuccessResponse;
import com.seulmae.seulmae.user.dto.request.UserSignUpDto;
import com.seulmae.seulmae.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @PostMapping("")
    public ResponseEntity<?> signUp(@RequestBody UserSignUpDto userSignUpDto) {
        userService.createUser(userSignUpDto);
        SuccessResponse successResponse = new SuccessResponse(SuccessCode.SIGNUP_SUCCESS);
        return new ResponseEntity<>(successResponse, HttpStatus.CREATED);
    }

    @GetMapping("/api/jwt-test")
    public String jwtTest() {
        return "jwtTest 요청 성공";
    }
}
