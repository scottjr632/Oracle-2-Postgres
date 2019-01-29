package resources;

import java.sql.*;

public class OracleCon {

    public static ResultSet getOracleDDL(String host, Integer port,
                                         String userName, String password, String database, String schema){

        StringBuilder connectionString = new StringBuilder()
                .append("jbdc:oracle:thin:@")
                .append(host)
                .append(":")
                .append(port)
                .append(":")
                .append(database);

        String sql = "SELECT OBJECT_TYPE, OBJECT_NAME,DBMS_METADATA.GET_DDL(OBJECT_TYPE, OBJECT_NAME, OWNER)\n" +
                "  FROM ALL_OBJECTS \n" +
                "  WHERE (OWNER = '" + schema + "') AND " +
                "OBJECT_TYPE NOT IN('LOB','MATERIALIZED VIEW', 'TABLE PARTITION') " +
                "ORDER BY OBJECT_TYPE DESC";

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            Connection conn = DriverManager.getConnection(connectionString.toString(), userName, password);

            Statement stmt = conn.createStatement();
            return stmt.executeQuery(sql);


        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

}