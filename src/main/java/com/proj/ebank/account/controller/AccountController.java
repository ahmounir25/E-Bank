package com.proj.ebank.account.controller;

import com.proj.ebank.account.dto.AccountDTO;
import com.proj.ebank.account.entity.Account;
import com.proj.ebank.account.service.AccountService;
import com.proj.ebank.enums.AccountType;
import com.proj.ebank.response.Response;
import com.proj.ebank.users.entity.User;
import com.proj.ebank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/accounts")
public class AccountController {

    private final AccountService accountService;
    private final UserService userService;


    //todo:: make user create another accounts
//    @PostMapping("/create")
//    public ResponseEntity<Response<Account>>createAccount(@RequestBody AccountType accountType){
//        User user=userService.getCurrentLoggedInUser();
//        return ResponseEntity.ok(Response.<Account>builder().data(accountService.createAccount(accountType,user)).build());
//    }

    @GetMapping("/me")
    public ResponseEntity<Response<List<AccountDTO>>>getMyAccount(){

        return ResponseEntity.ok(accountService.getMyAccounts());
    }

    @DeleteMapping("/close/{accountNum}")
    public ResponseEntity<Response<?>>closeAccount(@PathVariable String accountNum){

        return ResponseEntity.ok(accountService.closeAccount(accountNum));
    }


}
