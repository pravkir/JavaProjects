package com.dws.challenge.service;

import com.dws.challenge.domain.Account;
import com.dws.challenge.repository.AccountsRepository;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;
  private final NotificationService notificationService;
  private final Lock lock = new ReentrantLock();

  @Autowired
	public AccountsService(AccountsRepository accountsRepository, NotificationService notificationService) {
		this.accountsRepository = accountsRepository;
		this.notificationService = notificationService;
	}

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
 	}
	
	public AccountsRepository getAccountsRepository() {
		return accountsRepository;
	}

	public void transferMoney(String accountFromId, String accountToId, BigDecimal amount) {
		if (amount.compareTo(BigDecimal.ZERO) <= 0) {
			throw new IllegalArgumentException("Transfer amount must be positive.");
		}

		lock.lock();
		try {
			Account accountFrom = this.accountsRepository.getAccount(accountFromId);
			Account accountTo = this.accountsRepository.getAccount(accountToId);
			
			if (accountFrom == null || accountTo == null) {
	            throw new IllegalArgumentException("One or both account IDs are invalid.");
	        }
			
			synchronized (accountFrom) {
				synchronized (accountTo) {
					if (accountFrom.getBalance().compareTo(amount) < 0) {
						throw new IllegalArgumentException("Insufficient funds in account " + accountFromId);
					}

					accountFrom.setBalance(accountFrom.getBalance().subtract(amount));
					accountTo.setBalance(accountTo.getBalance().add(amount));
				}
			}

			notifyTransfer(accountFrom, accountTo, amount);
		} finally {
			lock.unlock();
		}
	}

	private void notifyTransfer(Account accountFrom, Account accountTo, BigDecimal amount) {
		notificationService.notifyAboutTransfer(accountFrom,
				String.format("Transferred %s to %s", amount, accountTo.getAccountId()));
		notificationService.notifyAboutTransfer(accountTo,
				String.format("Received %s from %s", amount, accountFrom.getAccountId()));
	}
}

