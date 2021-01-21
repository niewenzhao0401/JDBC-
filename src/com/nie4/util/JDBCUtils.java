package com.nie4.util;


import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.apache.commons.dbutils.DbUtils;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class JDBCUtils {
    /**
     * 数据库获取连接
     *
     * @return
     * @throw
     */

    public static Connection getConnection() throws Exception {
        //1,读取配置文件中的4个基本信息
        InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("jdbc.properties");

        Properties pros = new Properties();
        pros.load(is);

        String user = pros.getProperty("user");
        String password = pros.getProperty("password");
        String url = pros.getProperty("url");
        String driverClass = pros.getProperty("driverClass");

        //加载驱动
        Class.forName(driverClass);

        //获取连接
        Connection conn = DriverManager.getConnection(url, user, password);
        return conn;
    }



    /*


/**
     * 使用C3p0 的数据库连接技术
     *
     * @return
     * @throws SQLException
     */
    private static ComboPooledDataSource cpds = new ComboPooledDataSource("hellc3p0");
    public static Connection getConnection1() throws SQLException {
        Connection con = cpds.getConnection();
        return con;
    }

//
//

    /*
    使用DBCP数据块连接池
     */
    private static DataSource source;

    static {//静态代码块 减少不必要的连接
        try {
            Properties pros = new Properties();
            FileInputStream is = new FileInputStream(new File("src/dbcp.properties"));
            pros.load(is);
            source = BasicDataSourceFactory.createDataSource(pros);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConntectin2() throws Exception {
        Connection conn = source.getConnection();
        return conn;
    }

//
//    /**
//     * 使用Druid数据库连接池技术
//     *
//     * @return
//     * @throws Exception
//     */
    private static DataSource source1;

    static {
        try {
            Properties pros = new Properties();
            InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream("druid.properties");

            pros.load(is);
            source1 = DruidDataSourceFactory.createDataSource(pros);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection3() throws Exception {
        Connection conn = source1.getConnection();
        return conn;
    }
//
//
    /**
     * 关闭Statement操作
     */
    public static void closeResource(Connection conn, Statement ps)  {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            if (ps != null)
                ps.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

//
//    /**
//     * @Description 关闭资源操作
//     */
    public static void closeResource(Connection conn, Statement ps, ResultSet rs) {
        try {
            if (ps != null)
                ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (rs != null)
                rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

//    /**
//     * 使用dbutils.jar中提供的DbUtils工具类，实现资源的关闭
//     */
    public static void closeResource1(Connection conn, Statement ps, ResultSet rs) {
/*
            try {
                DbUtils.close(conn);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {
                DbUtils.close(ps);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            try {

                DbUtils.close(rs);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }*/
        DbUtils.closeQuietly(conn);
        DbUtils.closeQuietly(ps);
        DbUtils.closeQuietly(rs);
    }
}
