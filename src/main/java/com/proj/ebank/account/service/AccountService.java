package com.proj.ebank.account.service;

import com.proj.ebank.account.dto.AccountDTO;
import com.proj.ebank.account.entity.Account;
import com.proj.ebank.enums.AccountType;
import com.proj.ebank.response.Response;
import com.proj.ebank.users.entity.User;

import java.util.List;

public interface AccountService {
    Account createAccount(AccountType accountType, User user);

    Response<List<AccountDTO>> getMyAccounts();

    Response<?> closeAccount(String accountNumber);
}
