package servlet;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.PrintWriter;
import java.io.StringWriter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.FraudDetectionService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class TransactionServletTest {

	private TransactionServlet servlet;

	@Mock
	private HttpServletRequest request;

	@Mock
	private HttpServletResponse response;

	private StringWriter responseContent;

	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
		servlet = new TransactionServlet();

		responseContent = new StringWriter();
		when(response.getWriter()).thenReturn(new PrintWriter(responseContent));
	}

	@Test
	void testValidTransactionRequest() throws Exception {
		when(request.getParameter("accountId")).thenReturn("1001");
		when(request.getParameter("amount")).thenReturn("1000");

		servlet.doPost(request, response);

		verify(response).setStatus(anyInt());
		assertFalse(responseContent.toString().isEmpty());
	}

	@Test
	void testInvalidInput() throws Exception {
		when(request.getParameter("accountId")).thenReturn("abc");
		when(request.getParameter("amount")).thenReturn("1000");

		servlet.doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
		assertTrue(responseContent.toString().contains("Invalid accountId or amount"));
	}

	@Test
	void testInternalServerError() throws Exception {
		when(request.getParameter("accountId")).thenThrow(new RuntimeException("Unexpected error"));

		servlet.doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		assertTrue(responseContent.toString().contains("Internal server error"));
	}
    @Test
    void testFraudulentTransaction() throws Exception {
        when(request.getParameter("accountId")).thenReturn("1002");
        when(request.getParameter("amount")).thenReturn("5000");

        FraudDetectionService fraudServiceSpy = spy(new FraudDetectionService());
        doReturn(true).when(fraudServiceSpy).processTransaction(1002L, 5000L);

        java.lang.reflect.Field field = TransactionServlet.class.getDeclaredField("fraudService");
        field.setAccessible(true);
        field.set(servlet, fraudServiceSpy);

        servlet.doPost(request, response);

       
        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        assertTrue(responseContent.toString().contains("Transaction flagged as fraudulent"));
    }
    @Test
    void testMissingAccountId() throws Exception {
        when(request.getParameter("accountId")).thenReturn(null);
        when(request.getParameter("amount")).thenReturn("1000");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseContent.toString().contains("Invalid accountId or amount"));
    }

    @Test
    void testMissingAmount() throws Exception {
        when(request.getParameter("accountId")).thenReturn("1001");
        when(request.getParameter("amount")).thenReturn(null);

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseContent.toString().contains("Invalid accountId or amount"));
    }
    @Test
    void testNonNumericAmount() throws Exception {
        when(request.getParameter("accountId")).thenReturn("1001");
        when(request.getParameter("amount")).thenReturn("abc");

        servlet.doPost(request, response);

        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        assertTrue(responseContent.toString().contains("Invalid accountId or amount"));
    }

	@Test
	void testSuccessfulTransaction() throws Exception {
		when(request.getParameter("accountId")).thenReturn("1003");
		when(request.getParameter("amount")).thenReturn("1500");

		FraudDetectionService fraudServiceSpy = spy(new FraudDetectionService());
		doReturn(false).when(fraudServiceSpy).processTransaction(1003L, 1500L);

		java.lang.reflect.Field field = TransactionServlet.class.getDeclaredField("fraudService");
		field.setAccessible(true);
		field.set(servlet, fraudServiceSpy);

		servlet.doPost(request, response);

		verify(response).setStatus(HttpServletResponse.SC_OK);
		assertTrue(responseContent.toString().contains("Transaction successful"));
	}
	

}
