package dao;

import java.sql.*;
import util.DBConnectionUtil;

public class AccountDAO {

	public void block(Long accountId) throws SQLException, ClassNotFoundException {
		String sql = "UPDATE accounts SET status = 'BLOCKED' WHERE account_id = ?";
		try (Connection con = DBConnectionUtil.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setLong(1, accountId);
			ps.executeUpdate();
		}
	}
}
