package Db_Connection;

/*
 * Import librerie per gestione connessione a DB
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author Alessandro Basile
 * @version 1.0
 */
public class DBAccess {

    private static LinkedList<String> tag;
    private static String DRIVER_CLASS = "com.mysql.jdbc.Driver";
    private static String DBMS = "jdbc:mysql";
    private static String SERVER = "localhost";
    private static String DB = "dblp";
    private static String PORT = "3306";
    private static String USER_ID = "dbm";
    private static String PASSW = "dbmpass";
    private static Connection conn;

    //FUNZIONE PER INIZIALIZZARE LA CONNESSIONE AL DATABASE
    public static void initConnection() {
        //Setto i paramentri della connessione
        System.out.println(DRIVER_CLASS);
        String ConnectionString = DBMS + "://" + SERVER + ":" + PORT + "/" + DB;
        System.out.println(ConnectionString);


        //Inizializzo la connessione al database;
        try {
            Class.forName(DRIVER_CLASS).newInstance();

        } catch (Exception e) {
            System.out.println("impossibile trovate il driver:" + DRIVER_CLASS);
        }
        try {
            conn = DriverManager.getConnection(ConnectionString, USER_ID, PASSW);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //RESTITUISCE L'OGGETTO CHE PERMETTE LA GESTIONE DELLA CONNESSIONE
    public static Connection getConnection() {
        return conn;
    }

    //PERMETTE DI CHIUDERE LA CONNESSIONE
    public static void closeConnection() {
        try {
            conn.close();
        } catch (SQLException e) {
            System.out.println("Impossibile chiudere la connessione");
        }
    }

    //PERMETTE DI CONTROLLARE CHE LA CONNESSIONE SIA STATA INIZIALIZZATA
    public static boolean checkConnection() {
        return (DBAccess.getConnection() != null);
    }
}