/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wangjie.dbtools;

import com.wangjie.dbtools.anno.Column;
import com.wangjie.dbtools.anno.PrimaryKey;
import com.wangjie.dbtools.anno.Table;
import com.wangjie.dbtools.util.*;
import com.wangjie.dbtools.util.Log;
import com.wangjie.refect.*;
import java.io.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;
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
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try{
            sql = sql.trim();
            if(null == sql || !sql.toLowerCase().contains("select")){
                throw new Exception("paramter sql is not a SELECT statement!");
            }
            conn = C3p0DbHelper.getConnection();
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            logger.log(Level.INFO, "[executeQuery]sql ==> " + sql);
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
        PreparedStatement stmt = null;
        try{
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
            
            String tablename = getTableValue(obj.getClass()); // 获取表名
            
            String sql = "insert into " + tablename + "(" + TextUtil.joinStrings(map.keySet(), ",") + ")" + " values(" + TextUtil.generatePlaceholders(map.size()) + ")";            
            
            conn = C3p0DbHelper.getConnection();
            stmt = conn.prepareStatement(sql);
            int i = 1;
            for(Object o : map.values()){
                stmt.setObject(i++, o);
            }
            logger.log(Level.INFO, "[executeSave]sql ==> " + sql);
            int result = stmt.executeUpdate();
            logger.log(Level.INFO, "[executeSave]result ==> " + result);
            return result;
        }catch(Exception ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
        
    }
    /**
     * 根据主键更新数据
     * @param obj
     * @param includeParams 包含哪些字段需要更新数据（类的属性）
     * @param excludeParams 排除哪些字段不更新数据（类的属性）
     * @return
     * @throws Exception 
     */
    public int executeUpdate(final T obj, final String[] includeParams, final String[] excludeParams) throws Exception{
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            final List<String> includeParamsList = null == includeParams ? null : Arrays.asList(includeParams);
            final List<String> excludeParamsList = null == excludeParams ? new ArrayList<String>() : Arrays.asList(excludeParams);

            final Map<String, Object> pkMap = new HashMap<String, Object>(); // 存储主键的属性和值（类中使用Primay Key注解的）
            final Map<String, Object> updateMap = new HashMap<String, Object>(); // 存储需要更新的属性和值
            ReflectionUtils.doWithFields(obj.getClass(), new ReflectionUtils.FieldCallback() {

                public void doWith(Field field) throws Exception
                {
                    String columnValue = getColumnValue(field);
                    if(null == columnValue){ // 如果该属性没有加column注解，则不插入数据库
                        return;
                    }
                    PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                    if(null == primaryKey){ // 如果不是主键
                        if(shouldModifyField(field, includeParamsList, excludeParamsList)){ // 如果包含在includeParams中，并不包含在excludeParams中，则需要更新这个字段（）
                            field.setAccessible(true);
                            updateMap.put(columnValue, field.get(obj));
                        }
                        return;
                    }
                    //如果是主键
                    field.setAccessible(true);
                    pkMap.put(columnValue, field.get(obj));

                }
            });

            if(updateMap.size() <= 0 || pkMap.size() <= 0){
                logger.log(Level.INFO, "[executeUpdate]更新数据失败，无需更新任何字段或未指定主键而无法更新数据");
                return -1;
            }

            String tablename = getTableValue(obj.getClass()); // 获取表名

            String updateStr = TextUtil.joinStrings(updateMap.keySet(), ",", "=?");
            String pkStr = TextUtil.joinStrings(pkMap.keySet(), ",", "=?");
            String sql = "update " + tablename + " set " + updateStr + " where " + pkStr;
            logger.log(Level.INFO, "==> [executeUpdate]sql: " + sql);

            conn = C3p0DbHelper.getConnection();
            stmt = conn.prepareStatement(sql);
            int i = 1;
            for(Object o : updateMap.values()){
                stmt.setObject(i++, o);
            }
            for(Object o : pkMap.values()){
                stmt.setObject(i++, o);
            }

            int result = stmt.executeUpdate();
            logger.log(Level.INFO, "[executeUpdate]result ==> " + result);
            return result;
        }catch(Exception ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
    }
    
    public int executeDelete(final T obj) throws Exception{
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            final Map<String, Object> pkMap = new HashMap<String, Object>(); // 存储主键的属性和值（类中使用Primay Key注解的）
            ReflectionUtils.doWithFields(obj.getClass(), new ReflectionUtils.FieldCallback() {

                    public void doWith(Field field) throws Exception
                    {
                        String columnValue = getColumnValue(field);
                        if(null == columnValue){ // 如果该属性没有加column注解，则不插入数据库
                            return;
                        }
                        PrimaryKey primaryKey = field.getAnnotation(PrimaryKey.class);
                        if(null == primaryKey){ // 如果不是主键
                            return;
                        }
                        //如果是主键
                        field.setAccessible(true);
                        pkMap.put(columnValue, field.get(obj));

                    }
                });

            String tablename = getTableValue(obj.getClass()); // 获取表名

            String pkStr = TextUtil.joinStrings(pkMap.keySet(), ",", "=?");
            String sql = "delete from " + tablename + " where " + pkStr;
            logger.log(Level.INFO, "==> [executeDelete]sql: " + sql);

            conn = C3p0DbHelper.getConnection();
            stmt = conn.prepareStatement(sql);
            
            int i = 1;
            for(Object o : pkMap.values()){
                stmt.setObject(i++, o);
            }
            
            int result = stmt.executeUpdate();
            logger.log(Level.INFO, "[executeDelete]result ==> " + result);
            return result;
        }catch(Exception ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
    }
    
    
    /**
     * 判断更新数据时是否需要修改某个字段
     * @param field
     * @param includeParamsList
     * @param excludeParamsList
     * @return 
     */
    private boolean shouldModifyField(Field field, List<String> includeParamsList, List<String> excludeParamsList){
        String fieldname = field.getName();
        if(null == includeParamsList){
            return !excludeParamsList.contains(fieldname);
        }
        return includeParamsList.contains(fieldname) && !excludeParamsList.contains(fieldname);
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
        Table table = clazz.getAnnotation(Table.class);
        if(null == table || "".equals(table.value())){ // 如果类中没有加Table注解，或者Table注解为空，那么直接使用类名作为表名
            return clazz.getSimpleName().toLowerCase();
        }
        return table.value();
    }
    
    
    
    
    
    
    

    
    public boolean execute(String sql) throws SQLException{
        Connection conn = null;
        PreparedStatement stmt = null;
        try{
            conn = C3p0DbHelper.getConnection();
            stmt = conn.prepareStatement(sql);
            logger.log(Level.INFO, "sql ==> " + sql);
            boolean result = stmt.execute();
            logger.log(Level.INFO, "[execute]result: " + result);
            return result;
        }catch(SQLException ex){
            throw ex;
        }finally{
            C3p0DbHelper.closeJDBC(null, stmt, conn);
        }
    }
    
    public static Connection getConnection() {
        return C3p0DbHelper.getConnection();
    }
    
    public static void closeJDBC(ResultSet rs, Statement st, Connection con) {
        C3p0DbHelper.closeJDBC(rs, st, con);
    }
    
    public static void initDatabase(File properityFile){
        C3p0DbHelper.initDatabase(properityFile);
    }
    
    public static void initDatabase(InputStream properityIs){
        C3p0DbHelper.initDatabase(properityIs);
    }
    
}
