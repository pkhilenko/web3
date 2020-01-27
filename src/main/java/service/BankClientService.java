package service;

import dao.BankClientDAO;
import exception.DBException;
import model.BankClient;

import java.sql.*;
import java.util.List;

public class BankClientService {

    public BankClientService() {
    }

    public BankClient getClientById(long id) throws DBException {
        try {
            return getBankClientDAO().getClientById(id);
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public BankClient getClientByName(String name) {
        try {
            return getBankClientDAO().getClientByName(name);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<BankClient> getAllClient() {
        BankClientDAO dao = getBankClientDAO();
        return dao.getAllBankClient();
    }

    public boolean deleteClient(String name) {
        String sql = "delete bank_client  where name='" + name + "'";

        try (Statement stmt = getMysqlConnection().createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addClient(BankClient client) {
        BankClientDAO dao = getBankClientDAO();

        if (getClientByName(client.getName()) != null || ! dao.validateClient(client.getName(), client.getPassword())) {
            return false;
        }

        String name = client.getName();
        String password = client.getPassword();
        Long money = client.getMoney();

        BankClient createClient = new BankClient(name, password, money);

        try {
            dao.addClient(createClient);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean sendMoneyToClient(BankClient sender, String name, Long value) {
        BankClientDAO dao = getBankClientDAO();
        String senderName = sender.getName();
        String senderPassword = sender.getPassword();
        Connection dc =  dao.getConnection();
        BankClient toSend = getClientByName(name);

        try {
            if (dao.isClientHasSum(senderName, value) && toSend != null) {
                dc.setAutoCommit(false);

                try {
                    dao.updateClientsMoney(senderName, senderPassword, -value);
                    dao.updateClientsMoney(name, toSend.getPassword(), value);
                    dc.commit();
                } catch (SQLException e) {
                    dc.rollback();
                    System.out.println(e.getMessage());
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void cleanUp() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.dropTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    public void createTable() throws DBException {
        BankClientDAO dao = getBankClientDAO();
        try {
            dao.createTable();
        } catch (SQLException e) {
            throw new DBException(e);
        }
    }

    private static Connection getMysqlConnection() {

        try {
            DriverManager.registerDriver((Driver) Class.forName("com.mysql.jdbc.Driver").newInstance());

            StringBuilder url = new StringBuilder();

            url.
                    append("jdbc:mysql://").        //db type
                    append("localhost:").           //host name
                    append("3306/").                //port
                    append("db_example?").          //db name
                    append("user=best&").          //login
                    append("password=best");       //password

            System.out.println("URL: " + url + "\n");

            Connection connection = DriverManager.getConnection(url.toString());
            return connection;
        } catch (SQLException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            e.printStackTrace();
            throw new IllegalStateException();
        }
    }

    private static BankClientDAO getBankClientDAO() {
        return new BankClientDAO(getMysqlConnection());
    }
}