package service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import dao.AccountDAO;
import dao.FraudAlertDAO;
import dao.TransactionDAO;

class FraudDetectionServiceTest {

    private FraudDetectionService service;

    private TransactionDAO transactionDAOMock;
    private AccountDAO accountDAOMock;
    private FraudAlertDAO fraudAlertDAOMock;

    @BeforeEach
    void setUp() throws Exception {
        service = new FraudDetectionService();

        transactionDAOMock = Mockito.mock(TransactionDAO.class);
        accountDAOMock = Mockito.mock(AccountDAO.class);
        fraudAlertDAOMock = Mockito.mock(FraudAlertDAO.class);

        injectMock("transactionDAO", transactionDAOMock);
        injectMock("accountDAO", accountDAOMock);
        injectMock("alertDAO", fraudAlertDAOMock);
    }

    private void injectMock(String fieldName, Object mock) throws Exception {
        Field field = FraudDetectionService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(service, mock);
    }

    @Test
    void shouldDetectFraudWhenMoreThanThreeTransactionsInTwoMinutes() throws Exception {
        Long accountId = 1001L;
        long amount = 5000;

        doNothing().when(transactionDAOMock).save(accountId, amount);
        when(transactionDAOMock.countLastTwoMinutes(accountId)).thenReturn(4);

        boolean result = service.processTransaction(accountId, amount);

        assertTrue(result);

        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock).countLastTwoMinutes(accountId);
        verify(accountDAOMock).block(accountId);
        verify(fraudAlertDAOMock)
                .save(accountId,
                      "More than 3 transactions within 2 minutes",
                      "HIGH");
    }

    @Test
    void shouldAllowTransactionWhenTransactionCountIsLow() throws Exception {
        Long accountId = 1002L;
        long amount = 2000;

        doNothing().when(transactionDAOMock).save(accountId, amount);
        when(transactionDAOMock.countLastTwoMinutes(accountId)).thenReturn(2);

        boolean result = service.processTransaction(accountId, amount);

        assertFalse(result);

        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock).countLastTwoMinutes(accountId);
        verify(accountDAOMock, never()).block(anyLong());
        verify(fraudAlertDAOMock, never()).save(any(), any(), any());
    }
    @Test
    void shouldReturnFalseWhenExceptionOccurs() throws Exception {
        Long accountId = 1003L;
        long amount = 3000;

        doThrow(new RuntimeException("DB error")).when(transactionDAOMock).save(accountId, amount);

        boolean result = service.processTransaction(accountId, amount);

        assertFalse(result);

        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock, never()).countLastTwoMinutes(anyLong());
        verify(accountDAOMock, never()).block(anyLong());
        verify(fraudAlertDAOMock, never()).save(any(), any(), any());
    }
    @Test
    void shouldReturnFalseWhenCountingTransactionsFails() throws Exception {
        Long accountId = 1004L;
        long amount = 4000;

        
        doNothing().when(transactionDAOMock).save(accountId, amount);

        when(transactionDAOMock.countLastTwoMinutes(accountId))
                .thenThrow(new RuntimeException("DB counting error"));

        boolean result = service.processTransaction(accountId, amount);

        assertFalse(result);

        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock).countLastTwoMinutes(accountId);
        verify(accountDAOMock, never()).block(anyLong());
        verify(fraudAlertDAOMock, never()).save(any(), any(), any());
    }
    @Test
    void shouldNotDetectFraudWhenTransactionCountIsExactlyThree() throws Exception {
        Long accountId = 1005L;
        long amount = 2500;

        doNothing().when(transactionDAOMock).save(accountId, amount);
        when(transactionDAOMock.countLastTwoMinutes(accountId)).thenReturn(3);

        boolean result = service.processTransaction(accountId, amount);

        assertFalse(result);

        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock).countLastTwoMinutes(accountId);
        verify(accountDAOMock, never()).block(anyLong());
        verify(fraudAlertDAOMock, never()).save(any(), any(), any());
    }

    @Test
    void shouldNotDetectFraudWhenTransactionCountIsExactlyThree1() throws Exception {
        Long accountId = 1005L;
        long amount = 2500;

        doNothing().when(transactionDAOMock).save(accountId, amount);
        when(transactionDAOMock.countLastTwoMinutes(accountId)).thenReturn(3);

        boolean result = service.processTransaction(accountId, amount);

        assertFalse(result);
        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock).countLastTwoMinutes(accountId);
        verify(accountDAOMock, never()).block(anyLong());
        verify(fraudAlertDAOMock, never()).save(any(), any(), any());
    }
    @Test
    void shouldAllowZeroAmountTransactionWhenCountIsLow() throws Exception {
        Long accountId = 1006L;
        long amount = 0;

        doNothing().when(transactionDAOMock).save(accountId, amount);
        when(transactionDAOMock.countLastTwoMinutes(accountId)).thenReturn(1);

        boolean result = service.processTransaction(accountId, amount);

        assertFalse(result);
        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock).countLastTwoMinutes(accountId);
        verify(accountDAOMock, never()).block(anyLong());
        verify(fraudAlertDAOMock, never()).save(any(), any(), any());
    }
    @Test
    void shouldAllowNegativeAmountTransactionWhenCountIsLow() throws Exception {
        Long accountId = 1007L;
        long amount = -100;

        doNothing().when(transactionDAOMock).save(accountId, amount);
        when(transactionDAOMock.countLastTwoMinutes(accountId)).thenReturn(2);

        boolean result = service.processTransaction(accountId, amount);

        assertFalse(result);
        verify(transactionDAOMock).save(accountId, amount);
        verify(transactionDAOMock).countLastTwoMinutes(accountId);
        verify(accountDAOMock, never()).block(anyLong());
        verify(fraudAlertDAOMock, never()).save(any(), any(), any());
    }


}
