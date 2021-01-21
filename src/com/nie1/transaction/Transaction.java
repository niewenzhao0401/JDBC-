package com.nie1.transaction;

import com.nie1.transaction.util.JDBCUtils;
import org.junit.Test;

import java.lang.reflect.Field;
import java.sql.*;

/**
 * 1.什么叫数据库事务
 * 事务:一组逻辑操作单元,使数据从一种状态到另外一种状态
 * > 一组逻辑操作单元,一个或者多个DML操作
 * 2. 事务处理的原则:保证所有事务都为一个工作单元执行  即使出现了故障,都不能改变这种方式
 * 当一个事务中执行了多个操作时,要么所有的事务都被提交(commit)  那么这些修改就永久保存
 * 下来: 要么数据库管理系统将放弃所作的所有修改,整个事务回滚(roolBack) 到最初始状态
 * <p>
 * 3 数据一旦提交 就不会发生回滚
 * <p>
 * 4. 那些操作会导致数据的自动提交?
 * > DDL数据定义语言，用于定义和管理 SQL 数据库中的所有对象的语言
 * DDL操作一旦执行,就会自动提交
 * >set autocommit = false 对DDL操作失效
 * > DML数据操作语言，SQL中处理数据等操作统称为数据操纵语言
 * DML默认情况下,一旦执行,就会自动提交
 * >我们通过set autocommit= false 的方式 取消DML操作的自动提交
 * > 默认在关闭连接时,会自动的提交数据
 */
public class Transaction {
    //******************未考虑数据库事务情况下的转账操作**************************
    /*
     * 针对于数据表user_table来说：
     * AA用户给BB用户转账100
     *
     * update user_table set balance = balance - 100 where user = 'AA';
     * update user_table set balance = balance + 100 where user = 'BB';
     */

    @Test
    public void testUpdate() {
        String sql1 = "update user_table set balance = balance - 100 where user = ?";
        update(sql1, "AA");

        //模拟网络异常
//        System.out.println(10 / 0);

        String sql2 = "update user_table set balance = balance + 100 where user = ?";
        update(sql2, "BB");
        System.out.println("转账成功");
    }

    // 通用的增删改操作---version 1.0
    public int update(String sql, Object... args) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            //1, 获取数据库连接
            conn = JDBCUtils.getConnection();
            //2 预编译sql语句 返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);
            //填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            //执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //修改其为自动提交数据
            //主要针对于使用数据库连接池的使用
            try {
                conn.setAutoCommit(true);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            //关闭资源
            JDBCUtils.closeResource(conn, ps);
        }
        return 0;
    }


    //********************考虑数据库事务后的转账操作*********************


    @Test
    public void testUpdateWithTx() {
        Connection conn = null;
        try {
            conn = JDBCUtils.getConnection();

            //1.取消自动提交
            conn.setAutoCommit(false);

            String sql1 = "update user_table set balance = balance - 100 where user = ?";
            update1(conn, sql1, "AA");

            //模拟网路异常
//            System.out.println(10 / 0);

            String sql2 = "update user_table set balance = balance + 100 where user = ?";
            update1(conn, sql2, "BB");

            System.out.println("转账成功");

            //2 提交数据
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();

            //3 数据回滚
            try {
                conn.rollback();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }


        } finally {
            JDBCUtils.closeResource(conn, null);
        }

    }

    // 通用的增删改操作---version 2.0
    public int update1(Connection conn, String sql, Object... args) {
        PreparedStatement ps = null;
        try {

            //1 预编译sql语句 返回PreparedStatement的实例
            ps = conn.prepareStatement(sql);
            //2 填充占位符
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            //3 执行
            return ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            //4 关闭资源
            JDBCUtils.closeResource(null, ps);
        }

        return 0;
    }

    //*****************************************************

    @Test
    public  void testTransactionSelect() throws Exception {
        Connection conn = JDBCUtils.getConnection();
        //获取当前连接的隔离级别
        System.out.println(conn.getTransactionIsolation());
        //设置数据库的隔离级别：
        conn.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        System.out.println(conn.getTransactionIsolation());
        conn.setAutoCommit(false);

        String sql = "select user,password,balance from user_table where user = ?";
        User user = getInstance(conn, User.class, sql, "CC");

        System.out.println(user);
    }


    @Test
    public void testTransactionUpdate() throws Exception{
        Connection conn = JDBCUtils.getConnection();

        //取消自动提交数据
        conn.setAutoCommit(false);
        String sql = "update user_table set balance = ? where user = ?";
        update1(conn, sql, 5000,"CC");

        Thread.sleep(15000);
        System.out.println("修改结束");
    }





    //通用查询数据库 用于返回数据库表中的一条数据（version 2.0：考虑上事务）
    public <T> T getInstance(Connection conn, Class<T> clazz, String sql, Object... args) throws Exception {
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            rs = ps.executeQuery();
            //获取结果集的元数据
            ResultSetMetaData rsmd = rs.getMetaData();

            //通过ResultSetMetaData 获取到结果集的列数
            int columnCount = rsmd.getColumnCount();

            if (rs.next()) {
                T t = clazz.newInstance();
                for (int i = 0; i < columnCount; i++) {
                    Object columValue = rs.getObject(i + 1);

                    //获取每个列名
                    String columnLabel = rsmd.getColumnLabel(i + 1);

                    //// 给t对象指定的columnName属性，赋值为columValue：通过反射

                    // 给t对象指定的columnName属性，赋值为columValue：通过反射
                    Field field = clazz.getDeclaredField(columnLabel);
                    field.setAccessible(true);
                    field.set(t, columValue);
                }
                return t;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            JDBCUtils.closeResource(null, ps, rs);

        }
        return null;
    }

}
