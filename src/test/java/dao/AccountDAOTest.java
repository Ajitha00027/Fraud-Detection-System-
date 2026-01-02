package dao;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import util.DBConnectionUtil;


class AccountDAOTest {

	@Test
	void testBlockAccount() throws Exception {

		Long accountId = 1001L;

		Connection connectionMock = mock(Connection.class);
		PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

		when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

		try (MockedStatic<DBConnectionUtil> mockedStatic = Mockito.mockStatic(DBConnectionUtil.class)) {

			mockedStatic.when(DBConnectionUtil::getConnection).thenReturn(connectionMock);

			AccountDAO dao = new AccountDAO();

			dao.block(accountId);

			verify(connectionMock).prepareStatement("UPDATE accounts SET status = 'BLOCKED' WHERE account_id = ?");

			verify(preparedStatementMock).setLong(1, accountId);
			verify(preparedStatementMock).executeUpdate();
		}
	}
	@Test
	void testBlockAccount_Success() throws Exception {
	    Long accountId = 1001L;

	    Connection connectionMock = mock(Connection.class);
	    PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

	    when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

	    try (MockedStatic<DBConnectionUtil> mockedStatic = Mockito.mockStatic(DBConnectionUtil.class)) {
	        mockedStatic.when(DBConnectionUtil::getConnection).thenReturn(connectionMock);

	        AccountDAO dao = new AccountDAO();

	        dao.block(accountId);

	        verify(connectionMock).prepareStatement("UPDATE accounts SET status = 'BLOCKED' WHERE account_id = ?");
	        verify(preparedStatementMock).setLong(1, accountId);
	        verify(preparedStatementMock).executeUpdate();
	    }
	}
	

	@Test
	void testBlockAccount_WhenConnectionFails_ShouldThrowSQLException() throws Exception {

	    Long accountId = 1001L;

	    try (MockedStatic<DBConnectionUtil> mockedStatic = Mockito.mockStatic(DBConnectionUtil.class)) {

	        mockedStatic.when(DBConnectionUtil::getConnection)
	                    .thenThrow(new SQLException("Database not reachable"));

	        AccountDAO dao = new AccountDAO();

	        assertThrows(SQLException.class, () -> dao.block(accountId));
	    }
	}
	
	@Test
	void testBlockAccount_WhenExecuteUpdateFails_ShouldThrowSQLException() throws Exception {

	    Long accountId = 1001L;

	    Connection connectionMock = mock(Connection.class);
	    PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

	    when(connectionMock.prepareStatement(anyString()))
	            .thenReturn(preparedStatementMock);

	    when(preparedStatementMock.executeUpdate())
	            .thenThrow(new SQLException("Update failed"));

	    try (MockedStatic<DBConnectionUtil> mockedStatic = Mockito.mockStatic(DBConnectionUtil.class)) {

	        mockedStatic.when(DBConnectionUtil::getConnection)
	                    .thenReturn(connectionMock);

	        AccountDAO dao = new AccountDAO();

	        assertThrows(SQLException.class, () -> dao.block(accountId));
	    }
	}
	

}
