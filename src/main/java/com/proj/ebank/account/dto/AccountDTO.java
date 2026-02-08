package com.proj.ebank.account.dto;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.proj.ebank.enums.AccountStatus;
import com.proj.ebank.enums.AccountType;
import com.proj.ebank.enums.Currency;
import com.proj.ebank.transactions.dto.TransactionDTO;
import com.proj.ebank.users.dto.UserDTO;
import com.proj.ebank.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.annotation.Retention;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class AccountDTO {
    private Long id;

    private String accountNumber;

    private BigDecimal balance = BigDecimal.ZERO;

    private AccountType accountType ;

    private AccountStatus accountStatus ;

    private Currency currency;

    @JsonBackReference // to ignore userDTO
    private UserDTO user;

    @JsonManagedReference // to avoid recursion loop by ignoring accountDTO that is inside TransactionDTO
    private List<TransactionDTO> transactions;

    private LocalDateTime createdAt=LocalDateTime.now();

    private LocalDateTime closedAt;

    private LocalDateTime updatedAt;

}
