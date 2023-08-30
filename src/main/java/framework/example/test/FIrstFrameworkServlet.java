package framework.example.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import framework.example.test.annotation.MyTransaction;

import static framework.example.test.listener.LeoWebListener.METHOD_MAP;;

@WebServlet("/")
public class FIrstFrameworkServlet extends HttpServlet {

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Servlet#Start");
        // 取得資料庫連線
        Connection conn = DBUtils.getConnection();
        
        // 取得url
        String url = request.getRequestURI().replace("/framework.example", "");
        Method mt = METHOD_MAP.get(url);
        
        if (mt != null) {
            Class<?> clazzPath = mt.getDeclaringClass();
            try {
                conn.setAutoCommit(false);
                Parameter[] params = mt.getParameters();
                // 看方法有沒有參數
                if (params.length > 0) {
                    // 大於1 代表 request 跟 response都有
                    if (params.length > 1) {
                        mt.invoke(clazzPath.newInstance(), request, response);
                    } else {
                        // 取得參數
                        Object arg = getHttp(params);
                        mt.invoke(clazzPath.newInstance(), arg);
                    }
                } else {
                    // 沒有參數的情況
                    mt.invoke(clazzPath.newInstance());
                }
                // 取得connection裡的資訊並commit
                if (!conn.getAutoCommit()) {
                    conn.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                // 如果有transaction註解就 並發生exception 就 rollback
                if (mt.isAnnotationPresent(MyTransaction.class)) {
                    try {
                        conn.rollback();
                    } catch (SQLException e1) {
                        e1.printStackTrace();
                    }
                }
            } finally {
                // 關掉連線
                DBUtils.closeConn(conn);
            }
        }
    }
    
    // 取得參數
    public Object getHttp(Parameter[] params) {
        Object arg = null;
        for (Parameter param : params) {
            String http = param.getParameterizedType().toString();
            // 判斷 是request or response
            if (http.endsWith("HttpServletRequest")) {
                arg = HttpServletRequest.class;
            } else if (http.endsWith("HttpServletResponse")) {
                arg = HttpServletResponse.class;
            }
        }
        return arg;
    }
}
