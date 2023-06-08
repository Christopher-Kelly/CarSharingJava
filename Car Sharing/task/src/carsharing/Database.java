package carsharing;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class Database {
    private static Database db = null;
    static final String JDBC_DRIVER = "org.h2.Driver";
    private String DB_URL = "jdbc:h2:file:../task/src/carsharing/db/";
    private static Connection connection = null;

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
                //connection.createStatement().execute("DROP ALL OBJECTS");
                connection.close();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        }
    }

    public void passTest9() throws SQLException {
        String sql = "ALTER TABLE COMPANY ALTER COLUMN ID RESTART WITH 1";
        connection.createStatement().execute(sql);
        sql = "ALTER TABLE CAR ALTER COLUMN ID RESTART WITH 1";
        connection.createStatement().execute(sql);
        sql = "ALTER TABLE CUSTOMER ALTER COLUMN ID RESTART WITH 1";
        connection.createStatement().execute(sql);
    }


    public void createTable(String sql){
        try (Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            e.getStackTrace();
        }
    }

    public void createCOMPANYTable() {
        String sql =
                 "CREATE TABLE IF NOT EXISTS COMPANY (\n"
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
        return getIntegerStringMap(company_names, sql);
    }

    public void createCARTable(){
        String sql = "CREATE TABLE IF NOT EXISTS CAR (\n"
                + "     NAME VARCHAR(255) NOT NULL , \n"
                + "     ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL  ,\n"
                + "     COMPANY_ID INTEGER  NOT NULL  ,\n"
                + "     foreign key (COMPANY_ID) references COMPANY(ID) ,\n"
                + "     UNIQUE(NAME)\n"
                + ");";

        createTable(sql);
    }

    public void createCAREntry(String name, int ID) throws SQLException {
        String sql = String.format("INSERT INTO CAR (NAME,COMPANY_ID) VALUES ('%s','%s');",name,ID);
        connection.createStatement().execute(sql);

    }
    public Map<Integer,String> listCARValues(int ID) throws SQLException {
        Map<Integer,String> car_names = new HashMap<>();

        String sql = "SELECT * FROM CAR where COMPANY_ID = '"+ID+"'";
        return getIntegerStringMap(car_names, sql);
    }

    public Map<Integer,String> listCompanyValues(int ID) throws SQLException {
        Map<Integer,String> car_names = new HashMap<>();

        String sql = "SELECT * FROM Company where ID = '"+ID+"'";
        return getIntegerStringMap(car_names, sql);
    }


    public Map<Integer, String> getIntegerStringMap(Map<Integer, String> car_names, String sql) throws SQLException {
        ResultSet companies = connection.createStatement().executeQuery(sql);
        if (companies == null){
            return car_names;
        }

        while (companies.next()){
            String name = companies.getString("NAME");
            int id = companies.getInt("ID");

            car_names.put(id,name);
        }
        return car_names;
    }

    public void createCUSTOMERTable(){
        String sql = "CREATE TABLE IF NOT EXISTS CUSTOMER (\n"
                + "     NAME VARCHAR(255) NOT NULL , \n"
                + "     ID INTEGER AUTO_INCREMENT PRIMARY KEY NOT NULL  ,\n"
                + "     RENTED_CAR_ID INTEGER  ,\n"
                + "     foreign key (RENTED_CAR_ID) references CAR(ID) ,\n"
                + "     UNIQUE(NAME)\n"
                + ");";

        createTable(sql);
    }

    public static void returnCar(int customerAccountID) throws SQLException {
        String sql = String.format("UPDATE CUSTOMER SET RENTED_CAR_ID = NULL WHERE ID = '%d'",customerAccountID);
        connection.createStatement().executeUpdate(sql);
    }

    public static void rentCar(int carId, int customerAccountID) throws SQLException {
        String sql = String.format("UPDATE CUSTOMER SET RENTED_CAR_ID = '%d' WHERE ID = '%d'",carId,customerAccountID);

        connection.createStatement().executeUpdate(sql);
    }



    public void createCUSTOMEREntry(String name) throws SQLException {
        String sql = String.format("INSERT INTO CUSTOMER (NAME,RENTED_CAR_ID) VALUES ('%s',NULL);",name);
        connection.createStatement().execute(sql);

    }
    public Map<Integer,String> listCUSTOMERS() throws SQLException {
        Map<Integer,String> customer_names = new HashMap<>();

        String sql = "SELECT * FROM CUSTOMER";
        return getIntegerStringMap(customer_names, sql);
    }

    //null for all cars
    //user for username
    public Map<Integer, Object[]> getAvailableCars(int owner,int flag) throws SQLException {
        Map<Integer,Object[]> availableCars = new HashMap<>();
        String sql;

            sql = String.format("                    SELECT car.id, car.name, car.company_id \n" +
                    "                    FROM car LEFT JOIN customer \n" +
                    "                    ON car.id = customer.rented_car_id \n" +
                    "                    WHERE customer.name IS NULL ;",owner);


        return getIntegerMap(availableCars, sql);
    }

    public Map<Integer, Object[]> getIntegerMap(Map<Integer, Object[]> availableCars, String sql) throws SQLException {
        ResultSet carsFound = connection.createStatement().executeQuery(sql);

        if (carsFound == null){
            return availableCars;
        }

        while (carsFound.next()){
            String name = carsFound.getString("car.name");
            int id = carsFound.getInt("car.id");
            int company_id = carsFound.getInt("car.company_id");


            Object[] list = new Object[2];
            list[0] = company_id;
            list[1] = name;
            availableCars.put(id,list);

        }
        return availableCars;
    }

    public Map<Integer, Object[]> getAvailableCars(String owner,int flag) throws SQLException {
        Map<Integer,Object[]> availableCars = new HashMap<>();
        String sql;

        if (flag == 0) {

            sql = String.format(" SELECT car.ID, car.NAME, car.COMPANY_ID \n" +
                    "                    FROM car LEFT JOIN customer \n" +
                    "                    ON car.ID = customer.RENTED_CAR_ID \n" +
                    "                    WHERE customer.NAME = '%s';", owner);
        }
        else{
            sql = String.format("SELECT CAR.id as id, CAR.name as name, CAR.company_id as company_id FROM CAR LEFT JOIN CUSTOMER ON CAR.id = CUSTOMER.rented_car_id " +
                    "WHERE company_id = '%s' AND CUSTOMER.rented_car_id is null;",owner);
        }

        return getIntegerMap(availableCars, sql);
    }

    public static Map getAllUserData() throws SQLException {
        Map<Integer,Object> availableCars = new HashMap<>();

        String sql = String.format("Select * from Customer");

        ResultSet carsFound = connection.createStatement().executeQuery(sql);

        if (carsFound == null){
            return availableCars;
        }

        while (carsFound.next()){
            String name = carsFound.getString("NAME");
            int id = carsFound.getInt("ID");
            int rented_car_id = carsFound.getInt("RENTED_CAR_ID");


            Object[] list = new Object[2];
            list[0] = rented_car_id;
            list[1] = name;
            availableCars.put(id,list);

        }
        return availableCars;
    }

}



/**
 * Customer
 * get all customer data
 * 1. Select * from Customer
 *  return {customerID,name}
 *
 *  get customer rented car
 * 2. Select Car.name, car.company_id, car.id
 *      from customer union car
 *      where customer.name == name and car.id == customer.rented_car_id
 *
 * find non rented cars
 * 3. Select Car.name, car.company_id, car.id
 *      from customer union car
 *      where customer.rented_car_id == null
 *
 *4. select * from company where company.id == ID
 *
 */