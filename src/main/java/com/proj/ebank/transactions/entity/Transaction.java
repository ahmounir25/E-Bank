package com.proj.ebank.transactions.entity;

import com.proj.ebank.account.entity.Account;
import com.proj.ebank.enums.TransactionStatus;
import com.proj.ebank.enums.TransactionType;
import jakarta.persistence.*;
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
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "amount")
    private BigDecimal amount;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @Column(nullable = false, name = "transaction_date")
    private final LocalDateTime transactionDate = LocalDateTime.now();

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, name = "transaction_status")
    private TransactionStatus transactionStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Column(name = "source_account", nullable = false)
    private String sourceAccount;
    @Column(name = "destination_account", nullable = false)
    private String destinationAccount;


}
