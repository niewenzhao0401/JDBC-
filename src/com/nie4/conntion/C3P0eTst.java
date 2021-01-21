package com.nie4.conntion;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.junit.Test;
import sun.management.snmp.jvmmib.JVM_MANAGEMENT_MIBOidTable;

import java.beans.PropertyVetoException;
import java.sql.Connection;


/**
 * 速度慢 稳定性强   hibernate官方推荐使用
 */
public class C3P0eTst {
    //方式一
    @Test
    public void testGetConnection() throws Exception {
        ComboPooledDataSource cpds = new ComboPooledDataSource();
        cpds.setDriverClass("com.mysql.jdbc.Driver");
        cpds.setJdbcUrl("jdbc:mysql://localhost:3306/test");
        cpds.setUser("root");
        cpds.setPassword("123123");


        //通过设置相关参数,对数据库连接池进行管理
        cpds.setInitialPoolSize(10);

        Connection conn = cpds.getConnection();
        System.out.println(conn);




    }
    //方式二:
    @Test
    public void  testGetConnection1() throws Exception{
        ComboPooledDataSource cpds = new ComboPooledDataSource("hellc3p0");
        Connection conn = cpds.getConnection();
        System.out.println(conn);
    }
}
