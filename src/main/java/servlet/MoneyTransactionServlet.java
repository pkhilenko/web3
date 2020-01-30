package servlet;

import com.google.gson.Gson;
import exception.DBException;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MoneyTransactionServlet extends HttpServlet {

    BankClientService bankClientService = new BankClientService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> pageVariables = new HashMap<>();;
        pageVariables.put("message", "");
        resp.getWriter().println(PageGenerator.getInstance().getPage("moneyTransactionPage.html", pageVariables));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        String json = "";
        Map<String, Object> pageVariables = new HashMap<>();

        String senderName = req.getParameter("senderName");
        String nameTo = req.getParameter("nameTo");
        String password = req.getParameter("senderPass");
        Long count = 0L;

        try {
            count = Long.parseLong(req.getParameter("count"), 10);
            System.out.println(count);
        } catch (NumberFormatException ignored) {
        }

        BankClient sender = new BankClient(senderName, password, count);

        if (bankClientService.sendMoneyToClient(sender, nameTo, count)) {
            resp.setStatus(HttpServletResponse.SC_OK);
            json = gson.toJson("The transaction was successful");
            pageVariables.put("message", "The transaction was successful");
        } else {
            resp.setStatus(HttpServletResponse.SC_OK);
            json = gson.toJson("transaction rejected");
            pageVariables.put("message", "transaction rejected");
        }
        resp.getWriter().println(json);
//        resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
    }
}
