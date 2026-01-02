package dao;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import util.DBConnectionUtil;

class TransactionDAOTest {

	@Test
	void testSaveTransaction() throws Exception {
		Long accountId = 1001L;
		long amount = 5000L;

		Connection connectionMock = mock(Connection.class);
		PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

		when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

		try (MockedStatic<DBConnectionUtil> mockedStatic = Mockito.mockStatic(DBConnectionUtil.class)) {

			mockedStatic.when(DBConnectionUtil::getConnection).thenReturn(connectionMock);

			TransactionDAO dao = new TransactionDAO();

			dao.save(accountId, amount);

			verify(connectionMock)
					.prepareStatement("INSERT INTO transactions (account_id, amount, txn_time) VALUES (?, ?, NOW())");
			verify(preparedStatementMock).setLong(1, accountId);
			verify(preparedStatementMock).setLong(2, amount);
			verify(preparedStatementMock).executeUpdate();
		}
	}

	@Test
	void testCountLastTwoMinutes() throws Exception {
		Long accountId = 2002L;
		int expectedCount = 4;

		Connection connectionMock = mock(Connection.class);
		PreparedStatement preparedStatementMock = mock(PreparedStatement.class);
		ResultSet resultSetMock = mock(ResultSet.class);

		when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);
		when(preparedStatementMock.executeQuery()).thenReturn(resultSetMock);
		when(resultSetMock.next()).thenReturn(true);
		when(resultSetMock.getInt(1)).thenReturn(expectedCount);

		try (MockedStatic<DBConnectionUtil> mockedStatic = Mockito.mockStatic(DBConnectionUtil.class)) {

			mockedStatic.when(DBConnectionUtil::getConnection).thenReturn(connectionMock);

			TransactionDAO dao = new TransactionDAO();

			int actualCount = dao.countLastTwoMinutes(accountId);

			assertEquals(expectedCount, actualCount);

			verify(connectionMock).prepareStatement(
					"SELECT COUNT(*) FROM transactions WHERE account_id = ? AND txn_time >= NOW() - INTERVAL 2 MINUTE");
			verify(preparedStatementMock).setLong(1, accountId);
			verify(preparedStatementMock).executeQuery();
			verify(resultSetMock).getInt(1);
		}
	}
	@Test
	void testCountLastTwoMinutes_WhenConnectionFails() throws Exception {

	    Long accountId = 2002L;

	    try (MockedStatic<DBConnectionUtil> mockedStatic =
	                 Mockito.mockStatic(DBConnectionUtil.class)) {

	        mockedStatic.when(DBConnectionUtil::getConnection)
	                    .thenThrow(new SQLException("DB connection failed"));

	        TransactionDAO dao = new TransactionDAO();

	        try {
	            dao.countLastTwoMinutes(accountId);
	            fail("Expected SQLException to be thrown");
	        } catch (SQLException e) {
	            // Expected exception – test passes
	        }
	    }
	}
	@Test
	void testSaveTransaction_WhenConnectionFails() throws Exception {

	    Long accountId = 1001L;
	    long amount = 5000L;

	    try (MockedStatic<DBConnectionUtil> mockedStatic =
	                 Mockito.mockStatic(DBConnectionUtil.class)) {

	        mockedStatic.when(DBConnectionUtil::getConnection)
	                    .thenThrow(new SQLException("DB connection failed"));

	        TransactionDAO dao = new TransactionDAO();

	        try {
	            dao.save(accountId, amount);
	            fail("Expected SQLException to be thrown");
	        } catch (SQLException e) {
	            // Expected exception – test passes
	        }
	    }
	}


}
