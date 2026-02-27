package com.proj.ebank.transactions.controller;


import com.proj.ebank.response.Response;
import com.proj.ebank.transactions.dto.TransactionRequest;
import com.proj.ebank.transactions.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;


    @PostMapping("/create")
    public ResponseEntity<Response<?>> createTransaction(@RequestBody @Valid TransactionRequest request) {


        return ResponseEntity.ok(transactionService.createTransaction(request));

    }

    @GetMapping("/{accountNum}")
    public ResponseEntity<Response<?>> getTransactions(
            @PathVariable String accountNum
            , @RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "4") int size) {
        return ResponseEntity.ok(transactionService.getTransactionsForAccount(accountNum, page, size));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('ADMIN','AUDITOR')")
    public ResponseEntity<Response<?>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page
            , @RequestParam(defaultValue = "4") int size) {
        return ResponseEntity.ok(transactionService.getAllTransactions(page, size));
    }




}
