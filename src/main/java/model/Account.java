package model;

public class Account {
	private Long accountId;
	private String status;
	private Double balance;

	public Account() {}
	public Account(Long accountId, String status, Double balance) {
		this.accountId = accountId;
		this.status = status;
		this.balance = balance;
	}

	public Long getAccountId() { return accountId; }
	public void setAccountId(Long accountId) { this.accountId = accountId; }

	public String getStatus() { return status; }
	public void setStatus(String status) { this.status = status; }

	public Double getBalance() { return balance; }
	public void setBalance(Double balance) { this.balance = balance; }
}
