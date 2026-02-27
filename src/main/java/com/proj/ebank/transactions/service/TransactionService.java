package com.proj.ebank.transactions.service;

import com.proj.ebank.response.Response;
import com.proj.ebank.transactions.dto.TransactionDTO;
import com.proj.ebank.transactions.dto.TransactionRequest;

import java.util.List;

public interface TransactionService {

    Response<?> createTransaction(TransactionRequest transactionRequest);

    Response<List<TransactionDTO>> getTransactionsForAccount(String accountNum, int page, int size);



}
