package carsharing;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.Scanner;

public class Main {

    public static String filename_parser(String[] args){
        String dbName = "test";

        for (int i = 0; i < args.length; i++) {
            if ("-databaseFileName".equals(args[i])) {
                dbName = args[i + 1];

            }
        }
        return dbName;
    }

    public static int manager_options(Scanner scanner, Database database){
        System.out.println("1. Log in as a manager\n" +
                "2. Log in as a customer\n" +
                "3. Create a customer\n" +
                "0. Exit");
        int input = scanner.nextInt();
        return input;
    }

    public static void car_list(Database database,int input) throws SQLException {
        Map<Integer,String> companies = database.listCARValues(input);
        int counter = 1;

        if (companies.size() == 0 ){
            System.out.println("The car list is empty!\n");
        }else {
            for (String value : companies.values()){
                System.out.printf("%d. %s\n",counter++,value);

            }
        }
    }

    public static void create_car(int input,Database database,Scanner scanner) throws SQLException {
        System.out.println("Enter the car name:\n");
        scanner.nextLine();
        String car_name = scanner.nextLine();

        database.createCAREntry(car_name.strip(),input);

        System.out.println("The car was added!\n");

    }
    public static void car_menu(Scanner scanner, Database database,Map<Integer,String> companies, int input) throws SQLException {
        int exit = 1;
        while (exit==1) {
            System.out.printf("'%s' company:\n", companies.get(input));
            System.out.println("1. Car list\n" +
                    "2. Create a car\n" +
                    "0. Back");
            int car_choice = scanner.nextInt();
            switch (car_choice) {
                case 0:
                    return;
                case 1:
                    car_list(database,input);
                    break;
                case 2:
                    create_car(input,database,scanner);
                    break;
            }
        }

    }

    public static void company_list(Database database, Scanner scanner) throws SQLException {
        Map<Integer,String> companies = database.listCOMPANYValues();
        if (companies.size() == 0 ){
            System.out.println("The company list is empty!\n");
        }else {
            System.out.println("Choose a company: \n");
            companies.forEach((key,value) -> System.out.printf("%d. %s\n",key,value));
            System.out.println("0. Back\n");
            int input = scanner.nextInt();

            if (input ==0){
                return;
            }
            car_menu(scanner,database,companies,input);

        }


    }
    public static void create_company(Database database,Scanner scanner) throws SQLException {
        System.out.println("Enter the company name:");
        if (scanner.hasNextLine()) {
            scanner.nextLine();
            String company = scanner.nextLine();
            System.out.println(company);
            database.CreateCOMPANY(company.strip());
            System.out.println("The company was created!\n");
        }

    }

    public static void company_menu(Scanner scanner, Database database) throws SQLException {

        int exit = 0;

        while (exit == 0) {
            System.out.println("1. Company list\n" +
                    "2. Create a company\n" +
                    "0. Back");
            int input = scanner.nextInt();
            switch (input) {
                case 0:
                    return;
                case 1:
                    company_list(database,scanner);
                    break;
                case 2:
                    create_company(database,scanner);
                    break;
                default:
                    System.out.println("Please enter a valid option\n");
            }
        }
    }
    //rent car
    // need a way to list all companies that have free cars
    // need to check I do not have a car rented
    // need to update customer, car and maybe company

    //return car
    // null out car rented

    //current rented car
    // uses same function as return car

    public static void rent_car(int company_ID, String customer_name,Database database,Scanner scanner,int customer_id,Map<Integer, Object[]> cars ) throws SQLException {
        System.out.println("Choose a car:\n");
        Map<Integer, String> companies = database.listCOMPANYValues();
        String[] car_names = new String[cars.size()];
        String company_name = companies.get(company_ID);
        int counter = 0;


        for (int key:cars.keySet()){
            Object[] entry = cars.get(key);
            if (entry[0].equals(company_ID)){
                car_names[counter] = (String) entry[1];
                System.out.printf("%d. %s\n",++counter,entry[1]);

            }

        }
        System.out.printf("0. Back\n");
        int input = scanner.nextInt();

        if (input == 0){
            return;
        }else{
            database.rentCar(input,customer_id);
            System.out.printf("You rented '%s'\n",car_names[input-1]);

        }

    }

    public static int current_rented_car(Database database,String owner) throws SQLException {
        Map<Integer, Object[]> current_car = database.getAvailableCars(owner,0);
        Map<Integer,String> companies =  database.listCOMPANYValues();
        Object[] car = new Object[2];

        for (int key: current_car.keySet()){
            car = current_car.get(key);
        }

        if (car[0]!=null){
            System.out.printf("Your rented car:\n" +
                    "%s\n" +
                    "Company:\n" +
                    "%s\n",car[1],companies.get(car[0]));
            return 1;
        }else{
            System.out.println("You didn't rent a car!\n");
            return 0;
        }
    }
    public static void customer_rent_car(Database database,Scanner scanner,String name, int customer_id) throws SQLException {
        Map<Integer, Object[]> cars = database.getAvailableCars(customer_id,0);
        cars.forEach((key, value) -> System.out.printf("%d. %s\n", key, Arrays.toString(value)));
        System.out.println("size "+cars.size());
        ArrayList<Integer> dup_checker = new ArrayList<>();

        int input = 1;
        if (cars.size() == 0) {
            System.out.println("The company list is empty!\n");
            return;
        } else {
            if (database.getAvailableCars(name,0).size()>0) {
                System.out.println("You've already rented a car!");
                return;

            } else {
                while (input != 0) {
                    for (int i = 1;i<cars.size()+1;i++) {
                        if (dup_checker.contains(cars.get(i)[0])){

                        }else{
                            dup_checker.add((Integer) cars.get(i)[0]);
                        }
                    }


                    System.out.println("Choose a company:");
                    for (int i = 0;i<dup_checker.size();i++) {
                        int company_ID = dup_checker.get(i);

                        Map<Integer, String> companies = database.listCompanyValues(company_ID);
                        String company_name = companies.get(company_ID);
                        System.out.printf("%d. %s\n", i+1, company_name);
                    }
                    System.out.println("0. Back\n");

                    input = scanner.nextInt();

                    if (input==0){
                        return;
                    }else{
                        rent_car(input,name,database,scanner,customer_id,cars);
                        input = 0;

                    }

                }

            }

        }
    }

    public static void customer_rent_car_menu(Database database, Integer customer_number, Scanner scanner, String customer_name) throws SQLException {
        int input = 1;

        while (input != 0){
            System.out.println("1. Rent a car\n" +
                    "2. Return a rented car\n" +
                    "3. My rented car\n" +
                    "0. Back");
            input = scanner.nextInt();
            switch (input){
                case 1:
                    customer_rent_car(database,scanner,customer_name,customer_number);
                    break;
                case 2:
                    customer_return_car(database,customer_number,customer_name);
                    break;
                case 3:
                    current_rented_car(database,customer_name);
                    break;
                case 0:
                    return;
            }
        }

    }

    private static void customer_return_car(Database database, int customer_id,String customer_name) throws SQLException {
        if (current_rented_car(database,customer_name)==1) {
            database.returnCar(customer_id);
            System.out.println("You've returned a rented car!\n");
        }else{
            System.out.println("You didn't rent a car!\n");
        }
    }


    public static Map<Integer,String> customer_listing(Database database) throws SQLException {
        Map<Integer,String> companies = database.listCUSTOMERS();
        return companies;

    }
    public static void customer_login(Scanner scanner, Database database) throws SQLException {
        Map<Integer,String> customers = customer_listing(database);
        int counter = 1;
        int input = 1;

        if (customers.size() == 0 ){
            System.out.println("The customer list is empty!\n");
            return;
        }else {
            while (input != 0) {
                System.out.println("Customer list:");
                for (String value : customers.values()) {
                    System.out.printf("%d. %s\n", counter++, value);
                }
                System.out.println("0. Back\n");
                input = scanner.nextInt();

                customer_rent_car_menu(database,input,scanner,customers.get(input));
                counter = 1;


            }


        }

    }

    public static void create_customer(Database database,Scanner scanner) throws SQLException {
        System.out.println("Enter the customer name:\n");
        scanner.nextLine();
        String name = scanner.nextLine();
        database.createCUSTOMEREntry(name);
        System.out.println("The customer was added!\n");
    }

    public static void main(String[] args) throws SQLException {
        String dbName = filename_parser(args);
        Scanner scanner = new Scanner(System.in);
        int exit = 0;

        Database database = Database.createDB(dbName);
        database.createCOMPANYTable();
        database.createCARTable();
        database.createCUSTOMERTable();
        database.passTest9();


        while (exit==0){
            int input = manager_options(scanner,database);
            switch (input){
                case 0:

                    database.closeDB();
                    return;
                case 1:
                    company_menu(scanner,database);
                    break;
                case 2:
                    customer_login(scanner,database);
                    break;
                case 3:
                    create_customer(database,scanner);
                    break;
            }
        }


    }
}


//alter table car to return and rent