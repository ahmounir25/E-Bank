package com.proj.ebank.users.repo;

import com.proj.ebank.users.entity.PassResetCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PassResetCodeRepo extends JpaRepository<PassResetCode,Long> {
    Optional<PassResetCode>findByCode(String code);
    void deleteByUserId(Long userId);
}
