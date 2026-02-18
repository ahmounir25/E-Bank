package com.proj.ebank.account.service;

import com.proj.ebank.account.dto.AccountDTO;
import com.proj.ebank.account.entity.Account;
import com.proj.ebank.account.repo.AccountRepo;
import com.proj.ebank.enums.AccountStatus;
import com.proj.ebank.enums.AccountType;
import com.proj.ebank.enums.Currency;
import com.proj.ebank.exceptions.BadRequestException;
import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.response.Response;
import com.proj.ebank.users.entity.User;
import com.proj.ebank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountServiceImpl implements AccountService {
    private final AccountRepo accountRepo;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final Random random = new Random();

    @Override
    public Account createAccount(AccountType accountType, User user) {

        String accountNumber=generateAccountNumber();
        Account myAccount= Account.builder()
                .accountNumber(accountNumber)
                .accountType(accountType)
                .user(user)
                .accountStatus(AccountStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .balance(BigDecimal.ZERO)
                .currency(Currency.EGP)
                .build();

        return accountRepo.save(myAccount);
    }

    private String generateAccountNumber() {
        String accountNum;
        do {
           accountNum="5532"+(random.nextInt(90000000)+10000000);

        }while (accountRepo.findByAccountNumber(accountNum).isPresent());

        return accountNum;
    }

    @Override
    public Response<List<AccountDTO>> getMyAccounts() {

        User myUser = userService.getCurrentLoggedInUser();
        List<Account> accountList = accountRepo.
                findByUserId(myUser.getId()).orElseThrow(()->new NotFoundException("Account Not Found"));


        return Response.<List<AccountDTO>>builder()
                .StatusCode(HttpStatus.OK.value())
                .message("get all accounts of the user")
                .data(accountList.stream().map(account -> modelMapper.map(account,AccountDTO.class)).toList())
                .build();
    }

    @Override
    public Response<?> closeAccount(String accountNumber) {

        User myUser=userService.getCurrentLoggedInUser();
        Account account =accountRepo.findByAccountNumber(accountNumber)
                .orElseThrow(()->new NotFoundException("Account Not Found"));

        if(!myUser.getAccounts().contains(account)){
            throw new NotFoundException("Account does not belong to you");
        }
        if (account.getBalance().compareTo(BigDecimal.ZERO) > 0){
            throw new BadRequestException("Account Can't be Closed Until the Balance reach Zero");

        }

        account.setAccountStatus(AccountStatus.CLOSED);
        account.setClosedAt(LocalDateTime.now());
        accountRepo.save(account);

        return Response.builder()
                .StatusCode(HttpStatus.OK.value())
                .message("Account has been closed Successfully")
                .build();
    }
}
