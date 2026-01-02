package servlet;

import service.FraudDetectionService;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.apache.logging.log4j.*;

@WebServlet("/Transaction")
public class TransactionServlet extends HttpServlet {

	private static final Logger logger = LogManager.getLogger(TransactionServlet.class);
	private final FraudDetectionService fraudService = new FraudDetectionService();

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.setContentType("application/json");

		try {
			Long accountId = Long.parseLong(req.getParameter("accountId"));
			long amount = Long.parseLong(req.getParameter("amount"));

			logger.info("Transaction request received for {}", accountId);

			boolean isFraud = fraudService.processTransaction(accountId, amount);

			if (isFraud) {
				resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
				resp.getWriter().write("{\"message\":\"Transaction flagged as fraudulent.\"}");
				return;
			}

			resp.setStatus(HttpServletResponse.SC_OK);
			resp.getWriter().write("{\"message\":\"Transaction successful.\"}");

		} catch (NumberFormatException e) {
			logger.warn("Invalid input", e);
			resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			resp.getWriter().write("{\"message\":\"Invalid accountId or amount.\"}");

		} catch (Exception e) {
			logger.error("Transaction failed", e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			resp.getWriter().write("{\"message\":\"Internal server error.\"}");
		}
	}
}
