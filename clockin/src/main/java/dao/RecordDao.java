package dao;

import model.Habit;
import model.Record;
import utils.DatabaseUtil;
import utils.DateUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RecordDao {
    public boolean addRecord(Record record) {
        Connection connection = null;
        PreparedStatement pst = null;
        boolean flag = true;
        String sql = "insert into Record(user_id, habit_id, description, clockin_time, clockin_date)" +
                " values(?,?,?,?,?)";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, record.getUserId());
            pst.setInt(2, record.getHabitId());
            pst.setString(3, record.getDescription());
            pst.setString(4, DateUtil.getNowTime());
            pst.setString(5, DateUtil.getNowDateTime("yyyyMMdd"));
            if(pst.executeUpdate() != 1) {
                flag = false;
            } else {

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

    public boolean deleteById(int id) {
        //删除今天某一条习惯的打卡
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String today = DateUtil.getNowDateTime("yyyyMMdd");
        String sql = "delete from Record where id = ? and clockin_date = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, today);
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
