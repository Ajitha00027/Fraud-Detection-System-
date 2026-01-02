package model;

import java.time.LocalDateTime;

public class Transaction {
	private Long transactionId;
	private Long accountId;
	private long amount;
	private LocalDateTime txnTime;

	public Transaction() {}

	public Long getTransactionId() { return transactionId; }
	public void setTransactionId(Long transactionId) { this.transactionId = transactionId; }

	public Long getAccountId() { return accountId; }
	public void setAccountId(Long accountId) { this.accountId = accountId; }

	public long getAmount() { return amount; }
	public void setAmount(long amount) { this.amount = amount; }

	public LocalDateTime getTxnTime() { return txnTime; }
	public void setTxnTime(LocalDateTime txnTime) { this.txnTime = txnTime; }
}
