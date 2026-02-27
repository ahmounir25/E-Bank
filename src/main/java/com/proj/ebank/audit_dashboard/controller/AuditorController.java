package com.proj.ebank.audit_dashboard.controller;

import com.proj.ebank.account.dto.AccountDTO;
import com.proj.ebank.audit_dashboard.service.AuditorService;
import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.response.Response;
import com.proj.ebank.transactions.dto.TransactionDTO;
import com.proj.ebank.users.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequestMapping("/api/audit")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('AUDITOR') or hasAuthority('ADMIN')")
public class AuditorController {
    private final AuditorService auditorService;

    @GetMapping("/totals")
    public ResponseEntity<Map<String, Long>> getTotals() {
        return ResponseEntity.ok(auditorService.getSystemTotals());
    }

    @GetMapping("/users")
    public ResponseEntity<UserDTO> getUser(@RequestParam String email) {
        Optional<UserDTO> userDTO = auditorService.findUserByEmail(email);
        return userDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/accounts")
    public ResponseEntity<AccountDTO> getAccount(@RequestParam String accountNum) {
        Optional<AccountDTO> accountDTO = auditorService.findAccountDetailsByAccountNum(accountNum);

        return accountDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/transactions/by-account")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@RequestParam String accountNum) {
        List<TransactionDTO> transactionDTOList = auditorService.findTransactionsByAccountNumber(accountNum);
        if (transactionDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(transactionDTOList);
    }

    @GetMapping("/transactions/by-id")
    public ResponseEntity<TransactionDTO> getTransactionById(@RequestParam Long id) {
        Optional<TransactionDTO> transactionDTO = auditorService.findTransactionsById(id);
        return transactionDTO.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }


}
