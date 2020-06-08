package markovpetr.graphic;

import java.sql.*;

public class DBConnect {

    private static Connection conn;
    private static String url = "jdbc:postgresql://localhost:5432/studs";
    private static String user = "postgres";
    private static String pass = "Buzuluk2002";

    public static Connection connect() throws SQLException{
        try{
            Class.forName("org.postgresql.Driver");
        }catch(ClassNotFoundException cnfe) {
            System.err.println("Error: " + cnfe.getMessage());
        }
        conn = DriverManager.getConnection(url,user,pass);
        return conn;
    }
    public static Connection getConnection() throws SQLException, ClassNotFoundException{
        if(conn !=null && !conn.isClosed())
            return conn;
        connect();
        return conn;

    }
}
