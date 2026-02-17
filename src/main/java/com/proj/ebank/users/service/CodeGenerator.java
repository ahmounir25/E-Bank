package com.proj.ebank.users.service;

import com.proj.ebank.users.repo.PassResetCodeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
@RequiredArgsConstructor
public class CodeGenerator {

    private final PassResetCodeRepo passResetCodeRepo;

    private static final String ALPHA_NUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private static final int CODE_LEN = 5;


    public String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();
        } while (passResetCodeRepo.findByCode(code).isPresent());
        return code;
    }

    private String generateRandomCode() {
        StringBuilder stringBuilder = new StringBuilder(CODE_LEN);
        SecureRandom secureRandom = new SecureRandom();

        for (int i=0;i<CODE_LEN;i++){
            int index = secureRandom.nextInt(ALPHA_NUM.length());
            stringBuilder.append(ALPHA_NUM.charAt(index));
        }
        return stringBuilder.toString();
    }

}
