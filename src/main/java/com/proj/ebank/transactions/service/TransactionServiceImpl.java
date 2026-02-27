package com.proj.ebank.transactions.service;

import com.proj.ebank.account.entity.Account;
import com.proj.ebank.account.repo.AccountRepo;
import com.proj.ebank.enums.NotificationType;
import com.proj.ebank.enums.TransactionStatus;
import com.proj.ebank.enums.TransactionType;
import com.proj.ebank.exceptions.BadRequestException;
import com.proj.ebank.exceptions.InsufficientBalanceException;
import com.proj.ebank.exceptions.InvalidTransactionException;
import com.proj.ebank.exceptions.NotFoundException;
import com.proj.ebank.notification.dto.NotificationDTO;
import com.proj.ebank.notification.service.NotificationService;
import com.proj.ebank.response.Response;
import com.proj.ebank.transactions.dto.TransactionDTO;
import com.proj.ebank.transactions.dto.TransactionRequest;
import com.proj.ebank.transactions.entity.Transaction;
import com.proj.ebank.transactions.repo.TransactionRepo;
import com.proj.ebank.users.entity.User;
import com.proj.ebank.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final AccountRepo accountRepo;
    private final NotificationService notificationService;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    @Transactional
    public Response<?> createTransaction(TransactionRequest transactionRequest) {

        Transaction transaction = new Transaction();
        transaction.setTransactionType(transactionRequest.getTransactionType());
        transaction.setAmount(transactionRequest.getAmount());

        switch (transaction.getTransactionType()) {
            case TRANSFER -> handleTransfer(transactionRequest, transaction);

            case DEPOSIT -> handleDeposit(transactionRequest, transaction);

            case WITHDRAW -> handleWithDraw(transactionRequest, transaction);

            default -> throw new InvalidTransactionException("Invalid Transaction Type");
        }

        transaction.setTransactionStatus(TransactionStatus.SUCCESS);
        Transaction savedTransaction = transactionRepo.save(transaction);

        // send email
        sendTransactionNotification(savedTransaction);

        return Response.builder()
                .StatusCode(HttpStatus.OK.value())
                .message("Transaction Done Successfully")
                .build();
    }

    @Override
    @Transactional
    public Response<List<TransactionDTO>> getTransactionsForAccount(String accountNum, int page, int size) {

        User user = userService.getCurrentLoggedInUser();

        Account account = accountRepo.findByAccountNumber(accountNum)
                .orElseThrow(() -> new NotFoundException("account not found"));

        if (!account.getUser().getId().equals(user.getId())) {
            throw new BadRequestException("Account doesn't belong to authenticated user");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("transactionDate").descending());
        Page<Transaction> transactionPage = transactionRepo.findByAccount_AccountNumber(accountNum, pageable);

        List<TransactionDTO> transactionList = transactionPage
                .getContent()
                .stream()
                .map(t -> modelMapper.map(t, TransactionDTO.class))
                .toList();

        return Response.<List<TransactionDTO>>builder()
                .message("Transactions Retrieved")
                .StatusCode(200)
                .data(transactionList)
                .metaData(Map.of(
                        "currentPage", transactionPage.getNumber(),
                        "pageSize", transactionPage.getSize(),
                        "totalPages", transactionPage.getTotalPages(),
                        "totalElements", transactionPage.getTotalElements()
                ))
                .build();
    }


    private void handleWithDraw(TransactionRequest transactionRequest, Transaction transaction) {
        Account account = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account Not Found"));

        if (account.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }
        account.setBalance(account.getBalance().subtract(transactionRequest.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }

    private void handleDeposit(TransactionRequest transactionRequest, Transaction transaction) {
        Account account = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("Account Not Found"));

        account.setBalance(account.getBalance().add(transactionRequest.getAmount()));
        transaction.setAccount(account);
        accountRepo.save(account);
    }

    private void handleTransfer(TransactionRequest transactionRequest, Transaction transaction) {
        User currentUser=userService.getCurrentLoggedInUser();
        Account sourceAccount = accountRepo.findByAccountNumber(transactionRequest.getAccountNumber())
                .orElseThrow(() -> new NotFoundException("account not found"));
        Account destinationAccount = accountRepo.findByAccountNumber(transactionRequest.getDestinationAccountNumber())
                .orElseThrow(() -> new NotFoundException("receiver account not found"));

        if (!sourceAccount.getUser().getId().equals(currentUser.getId())){
            throw new BadRequestException("Account doesn't belong to authenticated user");
        }

        if (sourceAccount.getBalance().compareTo(transactionRequest.getAmount()) < 0) {
            throw new InsufficientBalanceException("Insufficient Balance");
        }

        sourceAccount.setBalance(sourceAccount.getBalance().subtract(transactionRequest.getAmount()));
        Account savedSourceAccount=accountRepo.save(sourceAccount);

        destinationAccount.setBalance(destinationAccount.getBalance().add(transactionRequest.getAmount()));
        Account savedDestAccount= accountRepo.save(destinationAccount);

        transaction.setAccount(savedSourceAccount);
        transaction.setSourceAccount(savedSourceAccount.getAccountNumber());
        transaction.setDestinationAccount(savedDestAccount.getAccountNumber());
    }

    private void sendTransactionNotification(Transaction savedTransaction) {
        User user = savedTransaction.getAccount().getUser();
        String subject;
        String templateName;

        DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd MMM, yyyy - hh:mm a");
        String formatedDate = savedTransaction.getTransactionDate().format(formater);

        Map<String, Object> vars = new HashMap<>();
        vars.put("name", user.getFirstName());
        vars.put("amount", savedTransaction.getAmount());
        vars.put("accountNum", savedTransaction.getAccount().getAccountNumber());
        vars.put("date", formatedDate);
        vars.put("balance", savedTransaction.getAccount().getBalance());

        if (savedTransaction.getTransactionType() == TransactionType.DEPOSIT) {
            subject = "Credit Alert";
            templateName = "deposit";

            NotificationDTO transactionMail = NotificationDTO.builder()
                    .templateVariables(vars)
                    .templateName(templateName)
                    .subject(subject)
                    .recipient(user.getEmail())
                    .type(NotificationType.EMAIL)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationService.sendEmail(transactionMail, user);
        }
        else if (savedTransaction.getTransactionType() == TransactionType.WITHDRAW) {
            subject = "Debit Alert";
            templateName = "withdraw";
            NotificationDTO transactionMail = NotificationDTO.builder()
                    .templateVariables(vars)
                    .templateName(templateName)
                    .subject(subject)
                    .recipient(user.getEmail())
                    .type(NotificationType.EMAIL)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationService.sendEmail(transactionMail, user);
        }
        else if (savedTransaction.getTransactionType() == TransactionType.TRANSFER) {
            subject = "Debit Alert";
            templateName = "withdraw";

            NotificationDTO transactionMail = NotificationDTO.builder()
                    .templateVariables(vars)
                    .templateName(templateName)
                    .subject(subject)
                    .recipient(user.getEmail())
                    .type(NotificationType.EMAIL)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationService.sendEmail(transactionMail, user);

            Account destAccount = accountRepo.findByAccountNumber(savedTransaction.getDestinationAccount())
                    .orElseThrow(() -> new NotFoundException(" Account Not found"));
            User destUser = destAccount.getUser();

            Map<String, Object> destVars = new HashMap<>();
            destVars.put("name", destUser.getFirstName());
            destVars.put("amount", savedTransaction.getAmount());
            destVars.put("accountNum", destAccount.getAccountNumber());
            destVars.put("date", formatedDate);
            destVars.put("balance", destAccount.getBalance());

            NotificationDTO transactionMailToReceiver = NotificationDTO.builder()
                    .templateVariables(destVars)
                    .templateName("deposit")
                    .subject("Credit Alert")
                    .recipient(destUser.getEmail())
                    .type(NotificationType.EMAIL)
                    .createdAt(LocalDateTime.now())
                    .build();
            notificationService.sendEmail(transactionMailToReceiver, destUser);

        }
    }

}
