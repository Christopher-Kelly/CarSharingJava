package carsharing;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Database {
    private static Database db = null;
    static final String JDBC_DRIVER = "org.h2.Driver";
    private String DB_URL = "jdbc:h2:file:../task/src/carsharing/db/";
    private final Connection connection;

    public static Database createDB(String dbName) {
        if (db == null) {
            db = new Database(dbName);
        }
        return db;
    }

    private Database (String dbName) {
        DB_URL = DB_URL + dbName;
        connection = getConnection();
    }

    private Connection getConnection() {
        Connection con = null;
        try {
            Class.forName(JDBC_DRIVER);
            con = DriverManager.getConnection(DB_URL);
            con.setAutoCommit(true);
        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to get connection with db, check the url" + e.getMessage());
        }
        return con;
    }


    public void closeDB() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }
    public ResultSet executeSQLStatement(String sql){
        try (Statement statement = connection.createStatement()) {
            ResultSet result = statement.executeQuery(sql);
            return result;
        } catch (SQLException e) {
            e.getStackTrace();
        }
        return null;
    }


    public void createTable(String sql){
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.getStackTrace();
        }
    }

    public void createCOMPANYTable() {
        String sql = "CREATE TABLE IF NOT EXISTS COMPANY (\n"
                + "     NAME VARCHAR(255) NOT NULL , \n"
                + "     ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL  ,\n"
                + "     UNIQUE(NAME)\n"
                + ");";

       createTable(sql);
    }

    public void CreateCOMPANY(String company) throws SQLException {
        String sql = String.format("INSERT INTO COMPANY (NAME) VALUES ('%s');",company);
        connection.createStatement().execute(sql);
    }
    public Map<Integer,String> listCOMPANYValues() throws SQLException {
        Map<Integer,String> company_names = new HashMap<>();
        
        String sql = "SELECT * FROM COMPANY;";
        ResultSet companies = connection.createStatement().executeQuery(sql);
        if (companies == null){
            return company_names;
        }

        while (companies.next()){
            String name = companies.getString("NAME");
            int id = companies.getInt("ID");
            
            company_names.put(id,name);
        }
        return company_names;
    }

    public void createCARTable(){
        String sql = "CREATE TABLE IF NOT EXISTS COMPANY (\n"
                + "     NAME VARCHAR(255) NOT NULL , \n"
                + "     ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL  ,\n"
                + "     UNIQUE(NAME)\n"
                + ");";

    }
}