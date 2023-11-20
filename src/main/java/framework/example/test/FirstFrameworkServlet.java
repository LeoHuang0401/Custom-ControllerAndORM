package framework.example.test;

import static framework.example.test.listener.LeoWebListener.IOC_MAP;
import static framework.example.test.listener.LeoWebListener.METHOD_MAP;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import framework.example.test.annotation.MyRequestBody;
import framework.example.test.annotation.MyRequestParam;
import framework.example.test.annotation.MyRestController;
import framework.example.test.annotation.MyTransactional;
import framework.example.test.utils.StringUtil;

@WebServlet("/")
public class FirstFrameworkServlet extends HttpServlet {
    
    private DBUtils dBUtils;
    
    @Override
    public void init(ServletConfig config) throws ServletException {
        this.dBUtils = (DBUtils) IOC_MAP.get("dBUtils");
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Servlet#Start");
        // 取得資料庫連線
        Connection conn = dBUtils.getConnection();
        
        // 取得url
        String url = request.getRequestURI().replace("/framework.example", "");
        Method mt = METHOD_MAP.get(url);
        IOC_MAP.forEach((k,v) -> {
            Class<?> clazzPath = v.getClass();
            if (clazzPath.isAnnotationPresent(MyRestController.class)) {
                Class<?> mtPath = mt.getDeclaringClass();
                if (k.equals(StringUtil.lowerFirstCase(mtPath.getSimpleName()))) {
                    try {
//                        conn.setAutoCommit(false);
                        Parameter[] params = mt.getParameters();
                        Object args[] = getArgs(request, response, params);
                        mt.invoke(v, args);
                        // 取得connection裡的資訊並commit
                        if (!conn.getAutoCommit()) {
                            conn.commit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        // 如果有transactional註解就 並發生exception 就 rollback
                        if (mt.isAnnotationPresent(MyTransactional.class)) {
                            try {
                                conn.rollback();
                            } catch (SQLException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } finally {
                        // 關掉連線
                        dBUtils.closeConn(conn);
                    }
                }
            }
        });
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        System.out.println("Post#start");
        doGet(request, response);
     }
    
    // 取得參數
    public Object[] getArgs(HttpServletRequest request, HttpServletResponse response, Parameter[] params) throws Exception {
        Object args[] = new Object[params.length];
        for (int i = 0; i < params.length;i++) {
            String arg = params[i].getParameterizedType().toString();
            // 判斷 是request or response
            if (arg.endsWith("HttpServletRequest")) {
                args[i] = request;
            } else if (arg.endsWith("HttpServletResponse")) {
                args[i] = response;
            } else {
                if (params[i].isAnnotationPresent(MyRequestParam.class)) {
                    String val = params[i].getAnnotation(MyRequestParam.class).name();
                    args[i] = request.getParameter(val);
                }
                if (params[i].isAnnotationPresent(MyRequestBody.class)) {
                    args[i] = getRequestBody(request, params[i]);
                }
            }
        }
        return args;
    }
    
    /**
     * 取得RequestBody資料
     * @param request
     * @param parameter
     * @return
     * @throws Exception
     */
    public Object getRequestBody(HttpServletRequest request, Parameter parameter) throws Exception {
        Class<?> clazz = parameter.getType();
        Object arg = clazz.newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field fld : fields) {
            if (StringUtil.isNotBlank(request.getParameter(fld.getName()))) {
                String mtName = fld.getName().substring(0, 1).toUpperCase() + fld.getName().substring(1);
                Method mt = clazz.getDeclaredMethod("set" + mtName, fld.getType());
                mt.invoke(arg, request.getParameter(fld.getName()));
            }
        }
        return arg;
    }
}
