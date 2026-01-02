package dao;

import java.sql.*;
import util.DBConnectionUtil;

public class TransactionDAO {

	public void save(Long accountId, long amount) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO transactions (account_id, amount, txn_time) VALUES (?, ?, NOW())";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, accountId);
			ps.setLong(2, amount);
			ps.executeUpdate();
		}
	}

	public int countLastTwoMinutes(Long accountId) throws SQLException, ClassNotFoundException {
		String sql = "SELECT COUNT(*) FROM transactions WHERE account_id = ? AND txn_time >= NOW() - INTERVAL 2 MINUTE";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, accountId);
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getInt(1);
			}
		}
	}
}
