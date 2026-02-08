package com.proj.ebank.account.repo;

import com.proj.ebank.account.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountRepo extends JpaRepository<Account,Long> {
    Optional<Account>findByAccountNumber(String accountNumber);
    Optional<List<Account>>findByUserId(Long userId);
}
