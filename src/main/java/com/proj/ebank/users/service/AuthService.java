package com.proj.ebank.users.service;


import com.proj.ebank.response.Response;
import com.proj.ebank.users.dto.LoginRequest;
import com.proj.ebank.users.dto.LoginResponse;
import com.proj.ebank.users.dto.PassResetRequest;
import com.proj.ebank.users.dto.RegisterRequest;
import com.proj.ebank.users.entity.PassResetCode;
import com.proj.ebank.users.entity.User;

public interface AuthService {

    Response<String> register(RegisterRequest request);

    Response<LoginResponse> login(LoginRequest request);

    Response<?> forgetPassword(String email);

    Response<?>updatePasswordViaResetCode(PassResetRequest passResetRequest);


}
