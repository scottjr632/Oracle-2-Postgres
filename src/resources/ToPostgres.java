package resources;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ToPostgres {
    // New Postgres connection

    public static void executePgFile(Connection conn, File pgFile) throws SQLException, IOException {
    	// Scanner used to read the Oracle DDL tmp file.
        Scanner sc = new Scanner(pgFile);
        sc.useDelimiter(";");
        // File to store any statements that did not commit to Postgres
        File errorFile = new File("ErrorFile.txt");
        FileWriter writer = new FileWriter(errorFile);
        
        while (sc.hasNext()){
            Statement stmt = conn.createStatement();
            String sql = sc.next();
            try {
                stmt.execute(sql);
            } catch (Exception e) {
            	writer.write(sql);
            }
        }
        // Close all scanners and files
        sc.close();
        writer.close();
    }

    public static Connection connect(String host, String port,
                                     String database, String user, String pass) {
    	// Creates connection to Postgres database
        Connection conn = null;

        try {
            String url = "jdbc:postgresql://" + host + ":" + port + "/" + database;
            conn = DriverManager.getConnection(url, user, pass);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    public static void createSchema(Connection conn, String schemaName) throws SQLException {
        String dropSql = "drop schema if exists " + schemaName + " cascade";
        // The search_path needs to be set to the schema so that we can get rid of
        // unnecessary schema names in the oracle DDL because it causes problems with indexes
        String sql = "create schema " + schemaName;
        String searchpath = "set search_path to " + schemaName;
        Statement stmt = conn.createStatement();
        stmt.execute(dropSql);
        stmt.execute(sql);
        stmt.execute(searchpath);
    }
}
