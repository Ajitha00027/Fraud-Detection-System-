package dao;

import java.sql.*;
import util.DBConnectionUtil;

public class FraudAlertDAO {

	public void save(Long accountId, String reason, String severity) throws SQLException, ClassNotFoundException {
		String sql = "INSERT INTO fraud_alerts (account_id, reason, severity, alert_time) VALUES (?, ?, ?, NOW())";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, accountId);
			ps.setString(2, reason);
			ps.setString(3, severity);
			ps.executeUpdate();
		}
	}
}
