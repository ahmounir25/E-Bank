package com.proj.ebank.audit_dashboard.service;

import com.proj.ebank.account.dto.AccountDTO;
import com.proj.ebank.transactions.dto.TransactionDTO;
import com.proj.ebank.users.dto.UserDTO;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface AuditorService {

    Map<String,Long> getSystemTotals();

    Optional<UserDTO> findUserByEmail(String email);

    Optional<AccountDTO> findAccountDetailsByAccountNum(String accountNum);

    List<TransactionDTO> findTransactionsByAccountNumber(String accountNum);

    Optional<TransactionDTO> findTransactionsById(Long id);


}
