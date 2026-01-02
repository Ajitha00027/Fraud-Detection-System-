package model;

import java.time.LocalDateTime;

public class FraudAlert {
	private Long alertId;
	private Long accountId;
	private String reason;
	private String severity;
	private LocalDateTime alertTime;

	public FraudAlert() {}

	public FraudAlert(Long alertId, Long accountId, String reason, String severity, LocalDateTime alertTime) {
		this.alertId = alertId;
		this.accountId = accountId;
		this.reason = reason;
		this.severity = severity;
		this.alertTime = alertTime;
	}

	public Long getAlertId() { return alertId; }
	public void setAlertId(Long alertId) { this.alertId = alertId; }

	public Long getAccountId() { return accountId; }
	public void setAccountId(Long accountId) { this.accountId = accountId; }

	public String getReason() { return reason; }
	public void setReason(String reason) { this.reason = reason; }

	public String getSeverity() { return severity; }
	public void setSeverity(String severity) { this.severity = severity; }

	public LocalDateTime getAlertTime() { return alertTime; }
	public void setAlertTime(LocalDateTime alertTime) { this.alertTime = alertTime; }
}
