package com.wangjie.dbtools;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public class C3p0DbHelper {
	private static ComboPooledDataSource cpds = new ComboPooledDataSource();
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
		} catch (PropertyVetoException e) {
			e.printStackTrace();
		}

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
			e.printStackTrace();
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
				e.printStackTrace();
			}
		}
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if (con != null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
