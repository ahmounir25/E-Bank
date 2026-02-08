package com.proj.ebank.transactions.dto;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.proj.ebank.account.dto.AccountDTO;
import com.proj.ebank.enums.TransactionStatus;
import com.proj.ebank.enums.TransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(content = JsonInclude.Include.NON_NULL)
public class TransactionDTO {

    private Long id;

    private BigDecimal amount;

    private TransactionType transactionType;

    private LocalDateTime transactionDate ;

    private TransactionStatus transactionStatus;

    @JsonBackReference
    private AccountDTO accountDTO;

    private String sourceAccount;

    private String destinationAccount;
}
