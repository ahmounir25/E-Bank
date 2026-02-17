package com.proj.ebank.users.controller;

import com.proj.ebank.response.Response;
import com.proj.ebank.users.dto.*;
import com.proj.ebank.users.entity.User;
import com.proj.ebank.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody @Valid RegisterRequest request){
        return ResponseEntity.ok(userService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(userService.login(request));
    }


    @PostMapping("/forget-pass")
    public ResponseEntity<Response<?>> forgetPass(@RequestBody @Valid ForgetPassRequest request){
        return ResponseEntity.ok(userService.forgetPassword(request.getEmail()));
    }


    @PostMapping("/reset-pass")
    public ResponseEntity<Response<?>> updatePass(@RequestBody @Valid PassResetRequest request){
        return ResponseEntity.ok(userService.updatePasswordViaResetCode(request));
    }

}
