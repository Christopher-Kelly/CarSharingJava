package carsharing;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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
                "0. Exit");
        int input = scanner.nextInt();
        return input;
    }

    public static void company_list(Database database) throws SQLException {
        Map<Integer,String> companies = database.listCOMPANYValues();
        if (companies.size() == 0 ){
            System.out.println("The company list is empty!\n");
        }else {
            companies.forEach((key,value) -> System.out.printf("%d. %s",key,value));
        }
        System.out.println("\n");
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
                    company_list(database);
                    break;
                case 2:
                    create_company(database,scanner);
                    break;
                default:
                    System.out.println("Please enter a valid option\n");
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        String dbName = filename_parser(args);
        Scanner scanner = new Scanner(System.in);
        int exit = 0;

        Database database = Database.createDB(dbName);
        database.createCOMPANYTable();


        while (exit==0){
            int input = manager_options(scanner,database);
            if (input == 0) {
                database.closeDB();
                return;
            }else{
                company_menu(scanner,database);

            }
        }


    }
}