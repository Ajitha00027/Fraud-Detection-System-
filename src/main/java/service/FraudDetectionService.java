package service;

import dao.AccountDAO;
import dao.FraudAlertDAO;
import dao.TransactionDAO;

public class FraudDetectionService {

	private final TransactionDAO transactionDAO = new TransactionDAO();
	private final AccountDAO accountDAO = new AccountDAO();
	private final FraudAlertDAO alertDAO = new FraudAlertDAO();

	public boolean processTransaction(Long accountId, long amount) {
		try {
			transactionDAO.save(accountId, amount);

			int count = transactionDAO.countLastTwoMinutes(accountId);

			if (count > 3) {
				accountDAO.block(accountId);
				alertDAO.save(
					accountId,
					"More than 3 transactions within 2 minutes",
					"HIGH"
				);
				return true;
			}

			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean checkTransaction(Long accountId, long amount) {
		try {
			int recentCount = transactionDAO.countLastTwoMinutes(accountId);

			return (recentCount + 1) > 3;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
