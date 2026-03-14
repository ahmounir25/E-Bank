package com.proj.ebank.users.controller;

import com.proj.ebank.response.Response;
import com.proj.ebank.users.dto.*;
import com.proj.ebank.users.service.AuthService;
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
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody @Valid RegisterRequest request){
        return ResponseEntity.ok(authService.register(request));
    }


    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest request){
        return ResponseEntity.ok(authService.login(request));
    }


    @PostMapping("/forget-password")
    public ResponseEntity<Response<?>> forgetPass(@RequestBody @Valid ForgetPassRequest request){
        return ResponseEntity.ok(authService.forgetPassword(request.getEmail()));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> updatePass(@RequestBody @Valid PassResetRequest request){
        return ResponseEntity.ok(authService.updatePasswordViaResetCode(request));
    }

}
