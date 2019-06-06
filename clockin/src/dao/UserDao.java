package dao;

import model.Plan;
import utils.DatabaseUtil;
import utils.DateUtil;
import utils.MD5Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public UserDao() {}

    public boolean add(String openid, String session) {
        Connection connection = null;
        PreparedStatement pst = null;
        boolean flag = true;
        String sql = "insert into User(openid, session)" +
                " values(?,?)";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, openid);
            pst.setString(2, session);
            if(pst.executeUpdate() != 1) {
                flag = false;
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            flag = false;
        } finally {
            DatabaseUtil.close(null, pst, connection);
        }
        return flag;
    }

    public String getOpenId(String session) {
        String openid = null;
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select openid from User where session = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, session);
            rs = pst.executeQuery();
            while(rs.next()) {
                openid = rs.getString("openid");
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            openid = null;
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return openid;
    }

    public boolean delete(String openid) {
        //该函数的返回值没有任何意义
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "delete from User where openid = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, openid);
            if(pst.executeUpdate() != 1) {
                flag = false;
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            flag = false;
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(null, pst, connection);
        }
        return flag;
    }
}
