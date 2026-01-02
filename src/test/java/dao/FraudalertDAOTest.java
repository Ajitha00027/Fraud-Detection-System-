package dao;

import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import util.DBConnectionUtil;

class FraudAlertDAOTest {

    @Test
    void testSaveFraudAlert() throws Exception {

        Long accountId = 1001L;
        String reason = "More than 3 transactions within 2 minutes";
        String severity = "HIGH";

        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(anyString()))
                .thenReturn(preparedStatementMock);

        try (MockedStatic<DBConnectionUtil> mockedStatic =
                     Mockito.mockStatic(DBConnectionUtil.class)) {

            mockedStatic.when(DBConnectionUtil::getConnection)
                        .thenReturn(connectionMock);

            FraudAlertDAO dao = new FraudAlertDAO();
            dao.save(accountId, reason, severity);

            verify(connectionMock).prepareStatement(
                "INSERT INTO fraud_alerts (account_id, reason, severity, alert_time) VALUES (?, ?, ?, NOW())");

            verify(preparedStatementMock).setLong(1, accountId);
            verify(preparedStatementMock).setString(2, reason);
            verify(preparedStatementMock).setString(3, severity);
            verify(preparedStatementMock).executeUpdate();
        }
    }
    @Test
    void testSaveFraudAlert_Success() throws Exception {
        Long accountId = 1001L;
        String reason = "Suspicious activity";
        String severity = "HIGH";

        Connection connectionMock = mock(Connection.class);
        PreparedStatement preparedStatementMock = mock(PreparedStatement.class);

        when(connectionMock.prepareStatement(anyString())).thenReturn(preparedStatementMock);

        try (MockedStatic<DBConnectionUtil> mockedStatic = Mockito.mockStatic(DBConnectionUtil.class)) {
            mockedStatic.when(DBConnectionUtil::getConnection).thenReturn(connectionMock);

            FraudAlertDAO dao = new FraudAlertDAO();
            dao.save(accountId, reason, severity);

            verify(connectionMock).prepareStatement(
                "INSERT INTO fraud_alerts (account_id, reason, severity, alert_time) VALUES (?, ?, ?, NOW())");
            verify(preparedStatementMock).setLong(1, accountId);
            verify(preparedStatementMock).setString(2, reason);
            verify(preparedStatementMock).setString(3, severity);
            verify(preparedStatementMock).executeUpdate();
            verify(preparedStatementMock).close(); 
        }
    }


    @Test
    void testSaveFraudAlert_WhenConnectionFails() throws Exception {

        Long accountId = 1001L;
        String reason = "More than 3 transactions within 2 minutes";
        String severity = "HIGH";

        try (MockedStatic<DBConnectionUtil> mockedStatic =
                     Mockito.mockStatic(DBConnectionUtil.class)) {

            mockedStatic.when(DBConnectionUtil::getConnection)
                        .thenThrow(new SQLException("DB unavailable"));

            FraudAlertDAO dao = new FraudAlertDAO();

            try {
                dao.save(accountId, reason, severity);
                fail("Expected SQLException to be thrown");
            } catch (SQLException e) {
                // ✅ Expected — test passes
            }
        }
    }
}
