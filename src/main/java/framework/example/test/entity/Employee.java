package framework.example.test.entity;

import framework.example.test.annotation.MyColumn;
import framework.example.test.annotation.MyEntity;
import framework.example.test.annotation.MyId;
import framework.example.test.annotation.MyTable;

@MyEntity
@MyTable(name = "Employee")
public class Employee {

    @MyId
    @MyColumn(name = "id")
    private String idTest;
    
    private String iden;
    
    @MyColumn(name = "username")
    private String user ;
    
    @MyColumn(name = "password")
    private String pwd ;

    public String getIdTest() {
        return idTest;
    }

    public void setIdTest(String idTest) {
        this.idTest = idTest;
    }

    public String getIden() {
        return iden;
    }

    public void setIden(String iden) {
        this.iden = iden;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

}
