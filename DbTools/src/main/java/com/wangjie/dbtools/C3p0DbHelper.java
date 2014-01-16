package com.wangjie.dbtools;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.wangjie.dbtools.util.*;
import java.io.*;
import java.util.Properties;
import java.util.logging.*;

public class C3p0DbHelper {
    static final Log logger = Log.getLogger(C3p0DbHelper.class);
	public static final ComboPooledDataSource cpds = new ComboPooledDataSource();
   
    static final String DRIVER_CLASS = "driverClass";
    static final String JDBC_URL = "jdbcUrl";
    static final String USERNAME = "username";
    static final String PASSWORD = "password";
    static final String INITIAL_POOL_SIZE = "initialPoolSize";
    static final String MIN_POOL_SIZE = "minPoolSize";
    static final String MAX_POOL_SIZE = "maxPoolSize";
    
    /** 
	static {
        try {
            // 驱动器
            cpds.setDriverClass("org.postgresql.Driver");
            // 数据库url
            cpds.setJdbcUrl("jdbc:postgresql://localhost:3500/mistest");
            // 用户名
            cpds.setUser("postgres");
            // 密码
            cpds.setPassword("wangjie");
            // 初始化连接池的大小
            cpds.setInitialPoolSize(30);
            // 最小连接数
            cpds.setMinPoolSize(20);
            // 最大连接数
            cpds.setMaxPoolSize(100);
            logger.log(Level.INFO, "数据库连接池初始化成功...");
        } catch (PropertyVetoException e) {
            logger.log(Level.SEVERE, "数据库连接池初始化失败", e);
        }

	}
    */
    /**
     * 初始化数据库配置
     * @param properityIs 
     */
    public static void initDatabase(InputStream properityIs){
        try {
            Properties prop = new Properties();
            prop.load(properityIs);
            // 驱动器
            cpds.setDriverClass(prop.getProperty(DRIVER_CLASS));
            // 数据库url
            cpds.setJdbcUrl(prop.getProperty(JDBC_URL));
            // 用户名
            cpds.setUser(prop.getProperty(USERNAME));
            // 密码
            cpds.setPassword(prop.getProperty(PASSWORD));
            // 初始化连接池的大小
            cpds.setInitialPoolSize(Integer.valueOf(prop.getProperty(INITIAL_POOL_SIZE, "10")));
            // 最小连接数
            cpds.setMinPoolSize(Integer.valueOf(prop.getProperty(MIN_POOL_SIZE, "5")));
            // 最大连接数
            cpds.setMaxPoolSize(Integer.valueOf(prop.getProperty(MIN_POOL_SIZE, "50")));
            logger.log(Level.INFO, "数据库连接池初始化成功...");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "数据库连接池初始化失败...", e);
        }
    }
    /**
     * 初始化数据库配置
     * @param properityFile 
     */
    public static void initDatabase(File properityFile){
        InputStream is = null;
        try
        {
            is = new FileInputStream(properityFile);
        }
        catch (FileNotFoundException ex)
        {
            logger.log(Level.SEVERE, "数据库配置文件打开失败...", ex);
        }
        initDatabase(is);
    }
    
    
	/**
	 * 用于数据库的链接
	 * 
	 * @return 返回Connection
	 */
	public static Connection getConnection() {
		try {
			return cpds.getConnection();
		} catch (SQLException e) {
			logger.log(Level.SEVERE, "获取连接失败", e);
		}
		return null;
	}

	/**
	 * 用于关闭数据库的关闭
	 * @param rs ResultSet对象
	 * @param st Statement对象
	 * @param con Connection对象
	 */
	public static void closeJDBC(ResultSet rs, Statement st, Connection con) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "ResultSet 关闭失败...", e);
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Statement 关闭失败...", e);
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				logger.log(Level.SEVERE, "Connection 关闭失败...", e);
			}
		}
	}

}
