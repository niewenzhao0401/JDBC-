package com.nie4.conntion;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.dbcp.BasicDataSourceFactory;
import org.junit.Test;

import javax.sql.DataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 测试DBCp 的数据库连接
 */
public class DBCPTest {

    @Test
    //**速度相对c3p0较快**，但因自身存在BUG，Hibernate3已不再提供支持。
    //运行失败
    public void testGetConnection() throws Exception {
        //创建了DBCP的数据库连接池
        BasicDataSource source = new BasicDataSource();

        //设置基本信息
        source.setDriverClassName("com.mysql.jdbc.Driver");
        source.setUrl("jdbc:mysql:///test");
        source.setUsername("root");
        source.setPassword("123123");


        //设置其他设计数据库连接池管理的相关属性
        source.setInitialSize(10);
        source.setMaxActive(10);


        //
        Connection conn = source.getConnection();
        System.out.println(conn);
    }

    //方式二 配饰文件
    @Test
    public void testGetConnection1() throws Exception {
        Properties pros = new Properties();
        InputStream is= ClassLoader.getSystemClassLoader().getResourceAsStream("dbcp.properties");
        pros.load(is);

        DataSource source = BasicDataSourceFactory.createDataSource(pros);

        Connection conn = source.getConnection();
        System.out.println(conn);
    }
}
