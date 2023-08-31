package framework.example.test.entity;

import framework.example.test.annotation.MyEntity;
import framework.example.test.annotation.MyId;
import framework.example.test.annotation.MyTable;

@MyEntity
@MyTable(name = "Employee")
public class Employee {

    @MyId
    private String id;
    
    private String iden;
    
    private String username ;
    
    private String password ;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIden() {
        return iden;
    }

    public void setIden(String iden) {
        this.iden = iden;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
}
