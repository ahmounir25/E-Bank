package com.proj.ebank.audit_dashboard.service;

import com.proj.ebank.account.dto.AccountDTO;
import com.proj.ebank.account.repo.AccountRepo;
import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.transactions.dto.TransactionDTO;
import com.proj.ebank.transactions.entity.Transaction;
import com.proj.ebank.transactions.repo.TransactionRepo;
import com.proj.ebank.users.dto.UserDTO;
import com.proj.ebank.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AuditorServiceImpl implements AuditorService {

    private final UserRepo userRepo;
    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final ModelMapper modelMapper;


    @Override
    public Map<String, Long> getSystemTotals() {
        long totalUsers = userRepo.count();
        long totalAccounts = accountRepo.count();
        long totalTransactions = transactionRepo.count();

        return Map.of(
                "totalUsers", totalUsers,
                "totalAccounts", totalAccounts,
                "totalTransactions", totalTransactions
        );
    }

    @Override
    public Optional<UserDTO> findUserByEmail(String email) {
        return userRepo.findByEmail(email)
                .map(user -> modelMapper.map(user, UserDTO.class));
    }

    @Override
    public Optional<AccountDTO> findAccountDetailsByAccountNum(String accountNum) {
        return accountRepo.findByAccountNumber(accountNum)
                .map(account -> modelMapper.map(account, AccountDTO.class));
    }

    @Override
    public List<TransactionDTO> findTransactionsByAccountNumber(String accountNum) {
        return transactionRepo.findByAccount_AccountNumber(accountNum)
                .stream().map(transaction -> modelMapper.map(transaction, TransactionDTO.class)).toList();
    }

    @Override
    public Optional<TransactionDTO> findTransactionsById(Long id) {
        return transactionRepo.findById(id)
                .map(transaction -> modelMapper.map(transaction, TransactionDTO.class));
    }
}
