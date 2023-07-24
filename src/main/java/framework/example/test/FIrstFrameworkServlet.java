package framework.example.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import framework.example.test.annotation.MyRequestMapping;
import framework.example.test.annotation.MyTransaction;
import framework.example.test.entitymanager.EntityManager;

@WebServlet("/")
public class FIrstFrameworkServlet extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("Servlet#Start");
		EntityManager em = new EntityManager();
		Connection conn = DBUtils.getConnection();
		System.out.println("路徑為=> " + request.getRequestURL());
		//取出 classListener 儲存有MyRestController的class
		ServletContext sct=getServletConfig().getServletContext();
		List<String> stringList = new ArrayList<>();
		stringList.add(sct.getAttribute("inRestController").toString());
		List<Class> restClassList = convertPath2Clzz(stringList);
		for (Class a : restClassList) {
			System.out.println("a => " + a.getName());
			Method[] method = a.getMethods();
			 for (Method mt : method) {
				 System.out.println("方法 => " + mt.getName());
				 //判斷 方法上有無MyRequestMapping的註解
				 if (mt.isAnnotationPresent(MyRequestMapping.class)) {
					System.out.println("成功找到MyRequestMapping => " + mt.getAnnotation(MyRequestMapping.class).value());
					System.out.println("結尾 => " + request.getRequestURL().toString().endsWith(mt.getAnnotation(MyRequestMapping.class).value()));
					//判斷 URL 有沒有一致
					if(request.getRequestURL().toString().endsWith(mt.getAnnotation(MyRequestMapping.class).value())) {
						try {
						    conn.setAutoCommit(false);
						    Parameter[] params = mt.getParameters();
						    //看方法有沒有參數
						        if(params.length > 0) {
						            //大於1 代表 request 跟 response都有
						            if(params.length > 1) {
						                mt.invoke(a.newInstance(),request,response);
						            }else {
						                //取得參數 
    						            for(Parameter param : params) {
    						                System.out.println("參數=> " + param.getParameterizedType());
    						                String s = param.getParameterizedType().toString();
    						                //判斷 是request or response
    						                if(s.endsWith("HttpServletRequest")) {
    						                    mt.invoke(a.newInstance(),request);
    						                }else if(s.endsWith("HttpServletResponse")){
    						                    mt.invoke(a.newInstance(),response);
    						                }
    						            }
						            }
						        }else {
						            // 沒有參數的情況
						            mt.invoke(a.newInstance());
						        }
						           //取得connection裡的資訊並commit
						        System.out.println("conn.getAutoCommit() => " + conn.getAutoCommit());
						        if(!conn.getAutoCommit()) {
	                                conn.commit();
	                                System.out.println("conn已送出");
						        }
						} catch (Exception e) {
						    e.printStackTrace();
						    // 如果有transaction註解就 並發生exception 就 rollback
						    if(mt.isAnnotationPresent(MyTransaction.class)) {
						        try {
                                    conn.rollback();
                                    System.out.println("發生錯誤，已取消操作");
                                } catch (SQLException e1) {
                                    e1.printStackTrace();
                                }
						    }
						}finally {
						    //關掉連線
						    DBUtils.closeConn(conn);
						}
						return;
					}
				 }
			 }
		}
	}
	
	public List<Class> convertPath2Clzz(List<String> fullPaths){
		List<Class> classList = new ArrayList<>();
		System.out.println("path => " + fullPaths);
		for (String path : fullPaths) {
			try {
				Class clazz = Class.forName(path);
				classList.add(clazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
		return classList;
	}
}
