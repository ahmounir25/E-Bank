package com.proj.ebank.users.service;

import com.proj.ebank.account.entity.Account;
import com.proj.ebank.account.service.AccountService;
import com.proj.ebank.enums.AccountType;
import com.proj.ebank.enums.Currency;
import com.proj.ebank.enums.NotificationType;
import com.proj.ebank.exceptions.BadRequestException;
import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.notification.dto.NotificationDTO;
import com.proj.ebank.notification.service.NotificationService;
import com.proj.ebank.response.Response;
import com.proj.ebank.role.entity.Role;
import com.proj.ebank.role.repo.RoleRepo;
import com.proj.ebank.security.TokenService;
import com.proj.ebank.users.dto.LoginRequest;
import com.proj.ebank.users.dto.LoginResponse;
import com.proj.ebank.users.dto.PassResetRequest;
import com.proj.ebank.users.dto.RegisterRequest;
import com.proj.ebank.users.entity.PassResetCode;
import com.proj.ebank.users.entity.User;
import com.proj.ebank.users.repo.PassResetCodeRepo;
import com.proj.ebank.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;
    private final AccountService accountService;
    private final TokenService tokenService;
    private final NotificationService notificationService;
    private final CodeGenerator codeGenerator;
    private final PassResetCodeRepo passResetCodeRepo;

    @Value("${password.reset.link}")
    private String resetLink;

    @Override
    public Response<String> register(RegisterRequest request) {

        List<Role> roleList;

        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            // Default role
            Role defaultRole = roleRepo.findByName("CUSTOMER").orElseThrow(() -> new NotFoundException("Role Not Found"));
            roleList = Collections.singletonList(defaultRole);
        } else {

            roleList = request.getRoles()
                    .stream()
                    .map(role -> roleRepo.findByName(role)
                            .orElseThrow(() -> new NotFoundException("Role Not Found " + role)))
                    .toList();
        }

        if (userRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .password(passwordEncoder.encode(request.getPassword()))
                .roles(roleList)
                .active(true)
                .createdAt(LocalDateTime.now())
                .build();

        User savedUser=userRepo.save(user);

        // TODO:: AUTO GENERATE AN ACCOUNT

        Account savedAccount = accountService.createAccount(AccountType.SAVING,savedUser);

        // Send welcome email

        Map<String,Object>vars=new HashMap<>();
        vars.put("name",savedUser.getFirstName());

        NotificationDTO notificationDTO= NotificationDTO.builder()
                .type(NotificationType.EMAIL)
                .recipient(savedUser.getEmail())
                .subject("Welcome To E-Bank 🥳")
                .templateName("welcome")
                .templateVariables(vars)
                .build();
        notificationService.sendEmail(notificationDTO,savedUser);

        // Send Account Details email

        Map<String,Object>accountVars=new HashMap<>();
        accountVars.put("fname",savedUser.getFirstName());
        accountVars.put("lname",savedUser.getLastName());
        accountVars.put("accountNumber",savedAccount.getAccountNumber());
        accountVars.put("type", AccountType.SAVING.name());
        accountVars.put("currency", Currency.EGP.name());

        NotificationDTO accountCreationEmail= NotificationDTO.builder()
                .type(NotificationType.EMAIL)
                .recipient(savedUser.getEmail())
                .subject("New Bank Account Has Been Created")
                .templateName("accountCreated")
                .templateVariables(accountVars)
                .build();

        notificationService.sendEmail(accountCreationEmail,savedUser);



        return Response.<String>builder()
                .StatusCode(HttpStatus.CREATED.value())
                .message("User Register Successfully")
                .data("Email of your account Details has been sent to you. Your Account Number is: "
                        +savedAccount.getAccountNumber())
                .build();
    }

    public Response<LoginResponse> login(LoginRequest request) {

        String email = request.getEmail();
        String password = request.getPassword();

        User myUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Invalid Credentials"));

        if (!passwordEncoder.matches(password, myUser.getPassword())) {
            throw new BadRequestException("Invalid Credentials");
        }

        String token = tokenService.generateToken(myUser.getEmail());

        LoginResponse loginResponse = LoginResponse.builder()
                .token(token)
                .roles(myUser.getRoles().stream().map(Role::getName).toList())
                .build();

        return Response.<LoginResponse>builder()
                .StatusCode(HttpStatus.OK.value())
                .message("Login Successfully")
                .data(loginResponse)
                .build();
    }

    @Override
    @Transactional
    public Response<?> forgetPassword(String email) {
        User myUser = userRepo.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Email Not exist"));

        passResetCodeRepo.deleteByUserId(myUser.getId());

        // Generate The Code
        String code = codeGenerator.generateUniqueCode();

        PassResetCode passResetCode = PassResetCode.builder()
                .code(code)
                .user(myUser)
                .expiryDate(generateExpiryDate())
                .used(false)
                .build();

        passResetCodeRepo.save(passResetCode);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", myUser.getFirstName());
        vars.put("resetLink", resetLink + code);

        NotificationDTO forgetPassMail = NotificationDTO.builder()
                .recipient(myUser.getEmail())
                .subject("Password Reset Code")
                .templateName("PassReset")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(forgetPassMail, myUser);

        return Response.builder()
                .StatusCode(HttpStatus.OK.value())
                .message("Sent a Reset Code")
                .build();
    }

    private LocalDateTime generateExpiryDate() {
        return LocalDateTime.now().plusHours(1);
    }

    @Override
    @Transactional
    public Response<?> updatePasswordViaResetCode(PassResetRequest passResetRequest) {

        String code = passResetRequest.getCode();
        String newPass = passResetRequest.getNewPass();

        PassResetCode resetCode = passResetCodeRepo.findByCode(code).orElseThrow(
                () -> new NotFoundException("Invalid Reset Code")
        );

        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
            passResetCodeRepo.delete(resetCode);
            throw new BadRequestException("Reset code has been expired");
        }

        User myUser = resetCode.getUser();
        myUser.setPassword(passwordEncoder.encode(newPass));
        userRepo.save(myUser);

        // clean up after updating pass by remove reset code
        passResetCodeRepo.delete(resetCode);

        // send mail

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", myUser.getFirstName());

        NotificationDTO updatePassMail = NotificationDTO.builder()
                .recipient(myUser.getEmail())
                .subject("Password Changed Successfully")
                .templateName("updatePass")
                .templateVariables(vars)
                .build();

        notificationService.sendEmail(updatePassMail, myUser);

        return Response.builder()
                .StatusCode(HttpStatus.OK.value())
                .message(" Password Updated Successfully")
                .build();
    }
}
