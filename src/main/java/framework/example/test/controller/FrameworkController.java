package framework.example.test.controller;

import static framework.example.test.listener.LeoWebListener.IOC_MAP;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import framework.example.test.annotation.MyAutowired;
import framework.example.test.annotation.MyRequestMapping;
import framework.example.test.annotation.MyRestController;
import framework.example.test.annotation.MyTransactional;
import framework.example.test.entity.Employee;
import framework.example.test.entitymanager.EntityManager;

@MyRestController
public class FrameworkController {
    
    @MyAutowired
    EntityManager entityManager;
    
    /*
     * 總查詢以及指定查詢
     */
	@MyRequestMapping("/emp/query")
	public void findAll(HttpServletRequest request, HttpServletResponse response) {
	    EntityManager em = new EntityManager();
	    System.out.println("findAll#start");
	    try {
	        String id = request.getParameter("id");
	        System.out.println("ID=> " + id);
	        PrintWriter pwt = response.getWriter();
	        List<Employee> list = entityManager.findAll(Employee.class,id);
	        if(list.isEmpty()) {
	            pwt.write("ID not found");
	        }else {
	            for(Employee emp : list) {
	                pwt.write("ID       =>" + emp.getId() + "\n");
	                pwt.write("IDEN     =>" + emp.getIden() + "\n");
	                pwt.write("USERNAME =>" + emp.getUsername() + "\n");
	                pwt.write("PASSWORD =>" + emp.getPassword() + "\n");
	            }
	        }
	        pwt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	/*
	 * 新增
	 */
	@MyRequestMapping("/emp/insert")
    public void insert(HttpServletRequest request, HttpServletResponse response) throws Exception{
        System.out.println("insert#start");
        Employee entity = new Employee();
        String id = request.getParameter("id");
        // 設定相關屬性值
        entity.setId(id);
        entity.setIden("LeoTest");
        entity.setUsername("Leo123");
        entity.setPassword("Leo123456");
        String message = entityManager.save(Employee.class, entity);
        resMessage(response, message);
    }
	
	/*
	 * 修改
	 */
	@MyRequestMapping("/emp/update")
	public void update(HttpServletRequest request, HttpServletResponse response) throws Exception{
        System.out.println("update#start");
        String id = request.getParameter("id");
        Employee entity = new Employee();
        // 設定相關屬性值
        entity.setId(id);
        entity.setIden("LeoUPDATE");
        entity.setUsername("Leo321");
        entity.setPassword("Leo654321");
        String message = entityManager.save(Employee.class, entity);
        resMessage(response, message);
	}
	
	/*
	 * 刪除
	 */
	@MyRequestMapping("/emp/delete")
	public void delete(HttpServletRequest request,HttpServletResponse response) throws Exception{
        System.out.println("delete#start 刪除的id=> " + request.getParameter("id"));
        String id = request.getParameter("id");
        String message = entityManager.delete(Employee.class, id);
        resMessage(response,message);
	}
	
	/*
	 * 交易控制
	 */
	@MyTransactional
	@MyRequestMapping("/emp/insert/TransactionTest")
	public void transactionTest(HttpServletRequest request, HttpServletResponse response) throws Exception {
	    Employee entity1 = new Employee();
        // 設定相關屬性值
	    entity1.setId("1132113");
	    entity1.setIden("leo33123");
	    entity1.setUsername("leo33123");
	    entity1.setPassword("leo123456");
	    entityManager.save(Employee.class,entity1);
        
        Employee entity2 = new Employee();
        // 設定相關屬性值
        entity2.setId("");
        entity2.setIden("leo33123");
        entity2.setUsername("leo33123");
        entity2.setPassword("leo123456");
        entityManager.save(Employee.class,entity2);
	}
	
	public void resMessage(HttpServletResponse response,String message) {
	    response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        try {
            PrintWriter pwt = response.getWriter();
            pwt.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
