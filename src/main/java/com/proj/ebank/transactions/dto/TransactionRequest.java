package com.proj.ebank.transactions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proj.ebank.account.entity.Account;
import com.proj.ebank.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class TransactionRequest {

    @NotNull
    private TransactionType transactionType;

    @NotNull
    @Positive
    private BigDecimal amount;

    private String description;

    @NotBlank
    private String accountNumber;

    private String destinationAccountNumber;

}
