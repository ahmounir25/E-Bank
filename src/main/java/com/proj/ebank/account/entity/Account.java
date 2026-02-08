package com.proj.ebank.account.entity;

import com.proj.ebank.enums.AccountStatus;
import com.proj.ebank.enums.AccountType;
import com.proj.ebank.enums.Currency;
import com.proj.ebank.transactions.entity.Transaction;
import com.proj.ebank.users.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "accounts")
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "account_number", unique = true, length = 15)
    private String accountNumber;

    @Column(nullable = false, name = "balance", scale = 6, precision = 20)
    private BigDecimal balance = BigDecimal.ZERO;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "account_type", nullable = false)
    private AccountType accountType ;

    @Enumerated(value = EnumType.STRING)
    @Column(name = "account_status",nullable = false)
    private AccountStatus accountStatus ;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false,name = "currency")
    private Currency currency;

    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;

    @OneToMany(mappedBy = "account",cascade =CascadeType.ALL,orphanRemoval = true,fetch = FetchType.LAZY)
    private List<Transaction>transactions=new ArrayList();

    @Column(name = "created_at")
    private LocalDateTime createdAt=LocalDateTime.now();

    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


}
