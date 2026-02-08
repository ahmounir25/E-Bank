package com.proj.ebank.transactions.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proj.ebank.account.entity.Account;
import com.proj.ebank.enums.TransactionType;
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

    private TransactionType transactionType;

    private BigDecimal amount;

    private String description;

    private String sourceAccountNumber;

    private String destinationAccountNumber;

}
