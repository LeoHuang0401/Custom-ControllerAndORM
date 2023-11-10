package framework.example.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtils {

public static ThreadLocal<Connection> pools = new ThreadLocal<>() ;
    
    public static Connection getConnection() {
        
        Connection conn = pools.get();
        if (conn == null) {
            try {
                System.out.println("連線池為空，建立新的連線");
                Class.forName("oracle.jdbc.OracleDriver");
                conn = DriverManager.getConnection("jdbc:oracle:thin:@//61.216.84.217:1534/ORCL", "DEMO", "123456");
                System.out.println("連線成功");
                pools.set(conn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            System.out.println("連線池已有連線");
        }
        return conn;
    }
    
    public static void closeConn(Connection conn) {
        try {
            if(conn != null) {
                conn.close();
                System.out.println("關閉連線");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
