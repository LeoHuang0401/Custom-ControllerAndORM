package framework.example.test.entitymanager;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


import framework.example.test.DBUtils;
import framework.example.test.annotation.MyComponent;
import framework.example.test.annotation.MyEntity;
import framework.example.test.annotation.MyId;
import framework.example.test.annotation.MyTable;

@MyComponent
public class EntityManager {
    
    /**
     * 查詢全部或查詢指定ID
     * @param <T>
     * @param t (要執行的Entity)
     * @param id (pk)
     * @return 
     */
    public <T> List<T> findAll(Class<T> t,String id){
        String tableName = checkEntity(t);
        List<T> dataList = new ArrayList<>();
        Connection conn = DBUtils.getConnection();
        String sql = "";
        Field[] fields = t.getDeclaredFields();
        sql = "SELECT * FROM " + tableName.toUpperCase();
        try {
            if (id != null) {
                System.out.println("ID為=>" + id);
                for (Field fld : fields) {
                    // pk
                    if (fld.isAnnotationPresent(MyId.class)) {
                        System.out.println("註解=> " + fld.getName());
                        sql = getSelectSql(tableName, fld, id);
                    }
                }
            }
            selectPk(t, dataList, sql, conn);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dataList;
    }
    
    /**
     * 新增修改
     * @param <T>
     * @param t (要執行的Entity)
     * @param o (新增或修改的物件)
     * @return 
     * @throws Exception
     */
    public <T> String save(Class<T> t,Object o) throws Exception {
        String tableName = checkEntity(t);
        List<T> dataList = new ArrayList<>();
        Connection conn = DBUtils.getConnection();
        // update 
        // 欄位名稱
        String name = "";
        String sql = "UPDATE " + tableName + " SET ";
        // update 欄位
        String updateSql = "";
        // where 條件
        String finalSql = "";
        // pk
        String selectSql = "";
        // insert
        String insertSql = "INSERT INTO " + tableName + " ( ";
        // return message
        String message = "";
        List<String> val = new ArrayList();
        
        Statement stmt = conn.createStatement();
        Field[] fields = t.getDeclaredFields();
        for (Field fld : fields) {
            String mtName = fld.getName().substring(0, 1).toUpperCase() + fld.getName().substring(1);
            // get欄位方法
            Method mt = t.getDeclaredMethod("get" + mtName);
            // 取得欄位值
            String value = mt.invoke(o).toString();
            name = fld.getName().toUpperCase();
            updateSql = sql += name + "=" + "'" + value + "'" + ",";
            insertSql += name + ",";
            val.add(value);
            if (fld.isAnnotationPresent(MyId.class)) {
                System.out.println("註解=> " + fld.getName());
                selectSql = getSelectSql(tableName, fld, value);
                finalSql = " WHERE " + fld.getName().toUpperCase() + " = " + value;
            }
        }
        // 查詢是否有這筆資料 有則update 無則insert
        List<T> list = selectPk(t, dataList, selectSql, conn);
        if (list.isEmpty()) {
            doInsert(insertSql, val, stmt);
            message = "新增成功";
        } else if(!list.isEmpty()){
            doUpdate(updateSql, finalSql, stmt);
            message = "修改完成";
        }
        return message;
    }
    
    /**
     * 刪除指定ID資料
     * @param <T>
     * @param t (要執行的Entity)
     * @param id (pk)
     * @return
     * @throws Exception
     */
    public <T> String delete(Class<T> t,String id) throws Exception {
        String tableName = checkEntity(t);
        List<T> dataList = new ArrayList<>();
        Connection conn = DBUtils.getConnection();
        String message = "";
        String selectSql = "";
        String deleteSql = "DELETE FROM " + t.getSimpleName() + " WHERE ";
        Statement stmt = conn.createStatement();
        Field[] fields = t.getDeclaredFields();
        for (Field fld : fields) {
            if (fld.isAnnotationPresent(MyId.class)) {
                System.out.println("註解=> " + fld.getName());
                selectSql = getSelectSql(tableName, fld, id);
                deleteSql += fld.getName();
            }
        }
        List<T> list = selectPk(t, dataList, selectSql, conn);
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
    
    /**
     * 執行update語法
     * @param updateSql
     * @param finalSql
     * @param stmt
     * @throws SQLException
     */
    public void doUpdate(String updateSql, String finalSql, Statement stmt) throws SQLException  {
        System.out.println("finalUpdateSql => " + updateSql.substring(0, updateSql.length() - 1) + finalSql);
        stmt.executeUpdate(updateSql.substring(0, updateSql.length() - 1) + finalSql);
    }
    
    /**
     * 執行insert語法
     * @param insertSql
     * @param val
     * @param stmt
     * @throws SQLException
     */
    public void doInsert(String insertSql, List<String> val,  Statement stmt) throws SQLException  {
        String sql1 = insertSql.substring(0, insertSql.length() - 1);
        sql1 += " ) VALUES (";
        for (String value : val) {
            sql1 += "'" + value + "' ,";
        }
        String finalInsertSql = sql1.substring(0, sql1.length() - 1);
        finalInsertSql += ")";
        System.out.println("finalInsertSql => " + finalInsertSql);
        stmt.executeUpdate(finalInsertSql);
    }
    
    /**
     * 取得查詢指定ID資料的SelectSQL
     * @param tableName
     * @param fld
     * @param value
     * @return
     */
    public String getSelectSql(String tableName, Field fld, String value) {
        return "SELECT * FROM " + tableName.toUpperCase() + " WHERE " + fld.getName().toUpperCase() + " = " + value;
    }
    
    /**
     * 查詢@Entity、@MyTable - TableName
     * @param <T>
     * @param c
     * @return
     */
    public <T> String checkEntity(Class<T> c) {
        String tableName = "";
        if (c.getAnnotation(MyEntity.class) != null && c.getAnnotation(MyTable.class) != null) {
            MyTable table = c.getAnnotation(MyTable.class);
            tableName = table.name();
        }
        return tableName;
    }
    
    /**
     * 執行查詢語法Select SQL
     * @param <T>
     * @param t
     * @param list
     * @param sql
     * @param conn
     * @return
     * @throws Exception
     */
    public <T> List<T> selectPk(Class<T> t,List<T> list,String sql,Connection conn) throws Exception{
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
