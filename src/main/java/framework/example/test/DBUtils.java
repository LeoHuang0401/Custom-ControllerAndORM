package framework.example.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import framework.example.test.annotation.MyComponent;
import framework.example.test.annotation.MyValue;

@MyComponent
public class DBUtils {
    
    @MyValue("db.driver.class")
    private String driverClass;
    
    @MyValue("db.url")
    private String url;
    
    @MyValue("db.user")
    private String user;
    
    @MyValue("db.pwd")
    private String pwd;

public final ThreadLocal<Connection> pools = new ThreadLocal<>() ;
    
    public Connection getConnection() {
        
        Connection conn = pools.get();
        if (conn == null) {
            try {
                System.out.println("連線池為空，建立新的連線");
                Class.forName(driverClass);
                conn = DriverManager.getConnection(url, user, pwd);
                conn.setAutoCommit(false);
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
    
    public void closeConn(Connection conn) {
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
