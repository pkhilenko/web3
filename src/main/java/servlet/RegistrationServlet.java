package servlet;

import com.google.gson.Gson;
import model.BankClient;
import service.BankClientService;
import util.PageGenerator;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegistrationServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Map<String, Object> pageVariables = new HashMap<>();
        pageVariables.put("message", "");
        resp.getWriter().println(PageGenerator.getInstance().getPage("registrationPage.html", pageVariables));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Gson gson = new Gson();
        String json = "";

        Map<String, Object> pageVariables = new HashMap<>();

        String name = req.getParameter("name");
        String password = req.getParameter("password");
        Long money = 0L;

        try {
            money = Long.parseLong(req.getParameter("money"), 10);
        } catch (NumberFormatException ignored) {
        }

        BankClient client = new BankClient(name, password, money);

           if (new BankClientService().addClient(client)) {
               resp.setStatus(HttpServletResponse.SC_OK);
               json = gson.toJson("Add client successful");
               pageVariables.put("message", "Add client successful");
           } else {
               resp.setStatus(HttpServletResponse.SC_OK);
               json = gson.toJson("Client not add");
               pageVariables.put("message", "Client not add");
           }
        resp.getWriter().println(json);
//        resp.getWriter().println(PageGenerator.getInstance().getPage("resultPage.html", pageVariables));
    }
}
