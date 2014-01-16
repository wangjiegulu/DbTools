/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.dbtools;

import com.wangjie.dbtools.anno.Column;
import com.wangjie.dbtools.anno.PrimaryKey;
import com.wangjie.dbtools.anno.Table;
import com.wangjie.dbtools.util.Log;
import com.wangjie.refect.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 *
 * @author wangjie <wangjie@cyyun.com>
 */
public class DbExecutor<T>
{
    final static Log logger = Log.getLogger(DbExecutor.class);
    /**
     * 查询数据库表并自动装箱到clazz对象中
     * @param sql
     * @param clazz
     * @return
     * @throws Exception 
     */
    public List<T> executeQuery(String sql, Class<?> clazz) throws Exception{
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try{
            sql = sql.trim();
            if(null == sql || !sql.toLowerCase().contains("select")){
                throw new Exception("paramter sql is not a SELECT statement!");
            }
            conn = C3p0DbHelper.getConnection();
            stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            logger.log(Level.INFO, "sql ==> " + sql);
            List<T> list = null;
            final ResultSet rr = rs;
            while(rs.next()){
                @SuppressWarnings("unchecked")
                final T obj = (T)clazz.newInstance();

                ReflectionUtils.doWithFields(clazz, new ReflectionUtils.FieldCallback(){

                    public void doWith(Field field) throws Exception
                    {
                        String columnValue = getColumnValue(field);
                        if(null == columnValue){ // 如果该属性没有加column注解
                            return;
                        }
                        field.setAccessible(true);
                        field.set(obj, rr.getObject(columnValue));
                    }

                });
                if(null == list){
                    list = new ArrayList<T>();
                }
                list.add(obj);

            }
            logger.log(Level.INFO, "[executeQuery]result: ", list);
            return list;
        }catch(Exception ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(rs, stmt, conn);
        }
        
    }
    /**
     * 插入一条数据
     * @param obj
     * @return
     * @throws Exception 
     */
    public int executeSave(final T obj) throws Exception{
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = C3p0DbHelper.getConnection();
            stmt = conn.createStatement();

            String tablename = getTableValue(obj.getClass()); // 获取表名

            final Map<String, Object> map = new HashMap<String, Object>();
            ReflectionUtils.doWithFields(obj.getClass(), new ReflectionUtils.FieldCallback() {

                public void doWith(Field field) throws Exception
                {
                    String columnValue = getColumnValue(field);
                    if(null == columnValue){ // 如果该属性没有加column注解，则不插入数据库
                        return;
                    }
                    PrimaryKey pk = field.getAnnotation(PrimaryKey.class);
                    if(null != pk && !pk.insertable()){ // 如果是主键，并且设置为不需插入，则不插入数据库
                        return;
                    }
                    field.setAccessible(true);
                    map.put(columnValue, field.get(obj));
                }
            });
            StringBuilder tables = new StringBuilder();
            StringBuilder values = new StringBuilder();
            for(Map.Entry<String, Object> entry : map.entrySet()){
                tables.append(",").append(entry.getKey());
                Object value = entry.getValue();
                values.append(",");
                if(value instanceof Integer || value instanceof Double || value instanceof Float){
                    values.append(value);
                }else{
                    values.append("'").append(value).append("'");
                }
            }
            String sql = "insert into " + tablename + "(" + tables.toString().substring(1) + ")" + " values(" + values.toString().substring(1) + ")";
            logger.log(Level.INFO, "sql ==> " + sql);
            int result = stmt.executeUpdate(sql);
            logger.log(Level.INFO, "[executeSave]result ==> " + result);
            return result;
        }catch(Exception ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
        
    }
    
    /**
     * 获得Column对应的value值（表的列名）
     * @param field
     * @return 如果该属性没有加column注解，则返回null；如果该属性的column注解value为空，则使用属性名（全小写）作为列名；否则使用value值
     */
    private String getColumnValue(Field field){
        Column column = field.getAnnotation(Column.class);
        if(null == column){ // 如果该属性没有加column注解，则返回null
            return null;
        }
        String value = column.value();
        if("".equals(value)){ // 如果该属性的column注解value为空，则使用属性名
            return field.getName().toLowerCase();
        }
        return value;
    }
    /**
     * 获得Table对应的value值（表名）
     * @param clazz
     * @return 如果类中没有加Table注解，或者Table注解为空，那么直接使用类名（全小写）作为表名；否则使用value值
     */
    private String getTableValue(Class<?> clazz){
        Table tableAnno = clazz.getAnnotation(Table.class);
        if(null == tableAnno || "".equals(tableAnno.value())){ // 如果类中没有加Table注解，或者Table注解为空，那么直接使用类名作为表名
            return clazz.getSimpleName().toLowerCase();
        }
        return tableAnno.value();
    }
    
    
    
    
    
    public boolean execute(String sql) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = C3p0DbHelper.getConnection();
            stmt = conn.createStatement();
            logger.log(Level.INFO, "sql ==> " + sql);
            boolean result = stmt.execute(sql);
            logger.log(Level.INFO, "[execute]result: " + result);
            return result;
        }catch(SQLException ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
        
    }
    
    public boolean execute(String sql, String[] columnNames) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = C3p0DbHelper.getConnection();
            stmt = conn.createStatement();
            logger.log(Level.INFO, "sql ==> " + sql, columnNames);
            boolean result = stmt.execute(sql, columnNames);
            logger.log(Level.INFO, "[execute]result: " + result);
            return result;
        }catch(SQLException ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
        
    }
    
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = C3p0DbHelper.getConnection();
            stmt = conn.createStatement();
            boolean result = stmt.execute(sql, autoGeneratedKeys);
            return result;
        }catch(SQLException ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
    }
    
    public boolean execute(String sql, int[] columnIndexes) throws SQLException{
        Connection conn = null;
        Statement stmt = null;
        try{
            conn = C3p0DbHelper.getConnection();
            stmt = conn.createStatement();
            boolean result = stmt.execute(sql, columnIndexes);
            return result;
        }catch(SQLException ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
        
    }
    
    
    
    
    
}
