package dao;

//import com.sun.deploy.util.SessionState;
import model.BankClient;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class
BankClientDAO {

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public BankClientDAO(Connection connection) {
        this.connection = connection;
    }

    public List<BankClient> getAllBankClient() {
        String sqlCommand = "SELECT * from bank_client";
        List<BankClient> all = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sqlCommand);

            while (resultSet.next()) {
                Long id = resultSet.getLong("id");
                String name = resultSet.getString("name");
                String password = resultSet.getString("password");
                Long money = resultSet.getLong("money");
                BankClient client = new BankClient(id, name, password, money);
                all.add(client);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        return all;
    }

    public boolean validateClient(String name, String password) {
        if (name == null || password == null) {
            return false;
        }
        if (name.isEmpty() || password.isEmpty()) {
            return false;
        }
        return true;
    }

    public void updateClientsMoney(String name, String password, Long transactValue) throws SQLException {
        String sql = "update bank_client set money=money+?  where name=? and password=?";
        PreparedStatement pst = connection.prepareStatement(sql);
        pst.setLong(1, transactValue);
        pst.setString(2, name);
        pst.setString(3, password);
        int queried = pst.executeUpdate();
        if (queried != 1) {
            throw new SQLException();
        }
    }

    public BankClient getClientById(long id) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where id='" + id + "'");
        ResultSet result = stmt.getResultSet();
        String password = "";
        String name = "";
        Long money = 0L;

        if (result.next()) {
            money = result.getLong(4);
            name = result.getString("name");
            password = result.getString("password");
        }

        result.close();
        stmt.close();

        return new BankClient(id, name, password, money);
    }

    public boolean isClientHasSum(String name, Long expectedSum) throws SQLException {
        BankClient client = getClientByName(name);
        return client.getMoney() >= expectedSum;
    }

    public long getClientIdByName(String name) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("select * from bank_client where name='" + name + "'");
        ResultSet result = stmt.getResultSet();
        result.next();
        Long id = result.getLong(1);
        result.close();
        stmt.close();
        return id;
    }

    public BankClient getClientByName(String name) throws SQLException {
        PreparedStatement preparedStatement = null;
        preparedStatement = connection.prepareStatement("select * from bank_client where name='" + name + "'");
        ResultSet result = preparedStatement.executeQuery();

        String foundName = "";
        Long id = 0L;
        Long money = 0L;
        String password = "";

        if(result.next()) {
            foundName = result.getString(2);
            id = result.getLong(1);
            money = result.getLong(4);
            password = result.getString(3);
            result.close();
        }

        if (foundName.isEmpty()) {
            return null;
        }
        return new BankClient(id, name, password, money);
    }

    public void addClient(BankClient client) throws SQLException {
            String sql = "INSERT INTO bank_client (name, password, money) VALUES (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, client.getName());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.setLong(3, client.getMoney());
            preparedStatement.executeUpdate();
    }

    public void createTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.execute("create table if not exists bank_client (id bigint auto_increment, name varchar(256), password varchar(256), money bigint, primary key (id))");
        stmt.close();
    }

    public void dropTable() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DROP TABLE IF EXISTS bank_client");
        stmt.close();
    }
}
