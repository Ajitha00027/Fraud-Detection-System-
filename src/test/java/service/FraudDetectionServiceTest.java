package service;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;

import dao.AccountDAO;
import dao.FraudAlertDAO;
import dao.TransactionDAO;

class FraudDetectionServiceTest {

    
    @Test
    void testProcessTransaction_NoFraud() throws Exception {

        try (MockedConstruction<TransactionDAO> txnMock =
                     mockConstruction(TransactionDAO.class,
                             (mock, context) -> {
                                 doNothing().when(mock).save(anyLong(), anyLong());
                                 when(mock.countLastTwoMinutes(anyLong())).thenReturn(2);
                             });
             MockedConstruction<AccountDAO> accMock =
                     mockConstruction(AccountDAO.class);
             MockedConstruction<FraudAlertDAO> alertMock =
                     mockConstruction(FraudAlertDAO.class)) {

            FraudDetectionService service = new FraudDetectionService();

            boolean result = service.processTransaction(1L, 1000L);

            Assertions.assertFalse(result);
            verify(txnMock.constructed().get(0)).save(1L, 1000L);
            verify(txnMock.constructed().get(0)).countLastTwoMinutes(1L);
            verifyNoInteractions(accMock.constructed().get(0));
            verifyNoInteractions(alertMock.constructed().get(0));
        }
    }

    
    @Test
    void testProcessTransaction_FraudDetected() throws Exception {

        try (MockedConstruction<TransactionDAO> txnMock =
                     mockConstruction(TransactionDAO.class,
                             (mock, context) -> {
                                 doNothing().when(mock).save(anyLong(), anyLong());
                                 when(mock.countLastTwoMinutes(anyLong())).thenReturn(5);
                             });
             MockedConstruction<AccountDAO> accMock =
                     mockConstruction(AccountDAO.class,
                             (mock, context) -> doNothing().when(mock).block(anyLong()));
             MockedConstruction<FraudAlertDAO> alertMock =
                     mockConstruction(FraudAlertDAO.class,
                             (mock, context) -> doNothing().when(mock)
                                     .save(anyLong(), anyString(), anyString()))) {

            FraudDetectionService service = new FraudDetectionService();

            boolean result = service.processTransaction(2L, 5000L);

            Assertions.assertTrue(result);
            verify(accMock.constructed().get(0)).block(2L);
            verify(alertMock.constructed().get(0))
                    .save(2L, "More than 3 transactions within 2 minutes", "HIGH");
        }
    }

    
    @Test
    void testProcessTransaction_SaveException() throws Exception {

        try (MockedConstruction<TransactionDAO> txnMock =
                     mockConstruction(TransactionDAO.class,
                             (mock, context) ->
                                     doThrow(new RuntimeException("DB error"))
                                             .when(mock).save(anyLong(), anyLong()))) {

            FraudDetectionService service = new FraudDetectionService();

            boolean result = service.processTransaction(3L, 2000L);

            Assertions.assertFalse(result);
        }
    }

    
    @Test
    void testProcessTransaction_CountException() throws Exception {

        try (MockedConstruction<TransactionDAO> txnMock =
                     mockConstruction(TransactionDAO.class,
                             (mock, context) -> {
                                 doNothing().when(mock).save(anyLong(), anyLong());
                                 doThrow(new RuntimeException("Query error"))
                                         .when(mock).countLastTwoMinutes(anyLong());
                             })) {

            FraudDetectionService service = new FraudDetectionService();

            boolean result = service.processTransaction(4L, 3000L);

            Assertions.assertFalse(result);
        }
    }
}
