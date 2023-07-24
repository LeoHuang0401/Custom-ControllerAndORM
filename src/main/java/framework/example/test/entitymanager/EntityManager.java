package framework.example.test.entitymanager;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import framework.example.test.DBUtils;
import framework.example.test.annotation.MyId;

public class EntityManager {

//    public static ThreadLocal<Connection> pools = new ThreadLocal<>() ;
//    
//    public Connection getConnection() {
//        
//        Connection conn = pools.get();
//        if (conn == null) {
//            try {
//                System.out.println("連線池為空，建立新的連線");
//                Class.forName("oracle.jdbc.OracleDriver");
//                conn = DriverManager.getConnection("jdbc:oracle:thin:@//61.216.84.220:1534/XE", "DEMO", "123456");
//                System.out.println("連線成功");
//                pools.set(conn);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }else {
//            System.out.println("連線池已有連線");
//        }
//        return conn;
//    }
    
    public <T> List<T> findAll(Class<T> t,String id){
        List<T> dataList = new ArrayList<>();
        Connection conn = DBUtils.getConnection();
        String sql = "";
        Field[] fields = t.getDeclaredFields();
        sql = "SELECT * FROM " + t.getSimpleName().toUpperCase();
        try {
            if (id != null) {
                System.out.println("ID為=>" + id);
                for (Field fld : fields) {
                    if (fld.isAnnotationPresent(MyId.class)) {
                        System.out.println("註解=> " + fld.getName());
                        sql = "SELECT * FROM " + t.getSimpleName().toUpperCase() + " WHERE " + fld.getName().toUpperCase() + " = " + id;
                    }
                }
            }
            entity(t, dataList, sql, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
    
    public <T> String save(Class<T> t,Object o) throws Exception {
        List<T> dataList = new ArrayList<>();
        Connection conn = DBUtils.getConnection();
        String name = "";
        String message = "";
        Field[] fields = t.getDeclaredFields();
        String selectSql = "";
        String sql = "UPDATE " + t.getSimpleName() + " SET ";
        String updateSql = "";
        String finalSql = "";
        String insertSql = "INSERT INTO " + t.getSimpleName() + " ( ";
        List<String> val = new ArrayList();
        
        Statement stmt = conn.createStatement();
        for (Field fld : fields) {
            String mtName = fld.getName().substring(0, 1).toUpperCase() + fld.getName().substring(1);
            Method mt = t.getDeclaredMethod("get" + mtName);
            String value = mt.invoke(o).toString();
            name = fld.getName().toUpperCase();
            updateSql = sql += name + "=" + "'" + value + "'" + ",";
            insertSql += name + ",";
            val.add(value);
            if (fld.isAnnotationPresent(MyId.class)) {
                System.out.println("註解=> " + fld.getName());
                selectSql = "SELECT * FROM " + t.getSimpleName().toUpperCase() + " WHERE " + fld.getName().toUpperCase()
                        + " = " + value;
                finalSql = " WHERE " + fld.getName().toUpperCase() + " = " + value;
            }
        }
        List<T> list = entity(t, dataList, selectSql, conn);
        System.out.println("list => " + list);
        if (list.isEmpty()) {
            String sql1 = insertSql.substring(0, insertSql.length() - 1);
            sql1 += " ) VALUES (";
            for (String value : val) {
                sql1 += "'" + value + "' ,";
            }
            String finalInsertSql = sql1.substring(0, sql1.length() - 1);
            finalInsertSql += ")";
            System.out.println("finalInsertSql => " + finalInsertSql);
            stmt.executeUpdate(finalInsertSql);
            message = "新增成功";
        } else if(!list.isEmpty()){
            System.out.println("finalUpdateSql => " + updateSql.substring(0, updateSql.length() - 1) + finalSql);
            stmt.executeUpdate(updateSql.substring(0, updateSql.length() - 1) + finalSql);
            message = "修改完成";
        }
        System.out.println("message => " + message);
        return message;
    }
    
    public <T> String delete(Class<T> t,String id) throws Exception {
        List<T> dataList = new ArrayList<>();
        Connection conn = DBUtils.getConnection();
        String message = "";
        Field[] fields = t.getDeclaredFields();
        String selectSql = "";
        String deleteSql = "DELETE FROM " + t.getSimpleName() + " WHERE ";
        Statement stmt = conn.createStatement();
        for (Field fld : fields) {
            if (fld.isAnnotationPresent(MyId.class)) {
                System.out.println("註解=> " + fld.getName());
                selectSql = "SELECT * FROM " + t.getSimpleName().toUpperCase() + " WHERE " + fld.getName().toUpperCase()
                        + " = " + id;
                deleteSql += fld.getName();
            }
        }
        List<T> list = entity(t, dataList, selectSql, conn);
        System.out.println("list => " + list);
        if (list == null || list.isEmpty()) {
            message = "查無此ID";
            return message;
        } else {
            System.out.println("刪除sql => " + deleteSql + " = '" + id + "'");
            stmt.execute(deleteSql + " = '" + id + "'");
            message = "刪除完成";
        }
        try {
            conn.setAutoCommit(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return message;
    }
    
    public <T> List<T> entity(Class<T> t,List<T> list,String sql,Connection conn) throws Exception{
        Field[] fields = t.getDeclaredFields();
        System.out.println("sql=> " + sql);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            T entity = t.newInstance();
            for (Field fld : fields) {
                String name = fld.getName().substring(0, 1).toUpperCase() + fld.getName().substring(1);
                Method mt = t.getDeclaredMethod("set" + name, fld.getType());
                mt.invoke(entity, rs.getString(fld.getName()));
            }
            list.add(entity);
        }
        return list;
    }
    
}
