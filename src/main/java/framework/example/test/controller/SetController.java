package framework.example.test.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import framework.example.test.annotation.MyAutowired;
import framework.example.test.annotation.MyRequestMapping;
import framework.example.test.annotation.MyRestController;
import framework.example.test.entity.Employee;
import framework.example.test.entitymanager.EntityManager;

@MyRestController
@MyRequestMapping("/abc")
public class SetController {

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
                    pwt.write("ID       =>" + emp.getIdTest() + "\n");
                    pwt.write("IDEN     =>" + emp.getIden() + "\n");
                    pwt.write("USERNAME =>" + emp.getUser() + "\n");
                    pwt.write("PASSWORD =>" + emp.getPwd() + "\n");
                }
            }
            pwt.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
