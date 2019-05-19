package dao;

import model.Habit;
import model.User;
import utils.DatabaseUtil;
import utils.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HabitDao {
    public boolean addHabit(Habit habit) {
        Connection connection = null;
        PreparedStatement pst = null;
        boolean flag = true;
        String sql = "insert into Habit(user_id, name, icon, color, category, weekday, create_time, clockin_days, flag_auto)" +
                " values(?,?,?,?,?,?,?,?,?)";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, habit.getUserId());
            pst.setString(2,habit.getName());
            pst.setInt(3,habit.getIcon());
            pst.setInt(4,habit.getColor());
            pst.setInt(5, habit.getCategory());
            pst.setInt(6, habit.getWeekday());
            pst.setString(7, DateUtil.getNowDateTime(""));
            pst.setInt(8, 0);
            pst.setBoolean(9, habit.isFlag_auto());
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

    public boolean deleteById(int id) {
        //删除一条习惯
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "delete from Habit where id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, id);
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

    public static void main(String[] args) {
        /*HabitDao a = new HabitDao();
        Habit h = new Habit();
        h.setUserId("User");
        h.setName("name");
        h.setIcon(1);
        h.setColor(2);
        h.setCategory(3);
        h.setWeekday(4);
        h.setFlag_auto(true);
        a.addHabit(h);*/
        System.out.println(DateUtil.getNowTime());
        System.out.println(DateUtil.getNowDateTime("yyyyMMdd"));
    }
    /*
    public List<Comment> findCommentByPicId(int phoId) {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Comment> list = new ArrayList<>();
        String sql = "select * from comment where phoid = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, phoId);
            rs = pst.executeQuery();
            Comment comment;
            while(rs.next()) {
                comment = new Comment();
                comment.setId(rs.getInt("id"));
                UserDaoImpl impl = new UserDaoImpl();
                User user = impl.findById(rs.getInt("userid"));
                comment.setUsername(user.getName());
                comment.setContent(rs.getString("comment"));
                comment.setDate(rs.getString("date"));
                comment.setPicid(rs.getInt("phoid"));
                comment.setUserid(rs.getInt("userid"));
                list.add(comment);
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
        }
        return list;
    }*/

    public Habit findById(int id) {
        Habit habit = new Habit();
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select * from Habit where id = ?";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            if(id <= 0) {
                habit= null;
            }
            else {
                pst = connection.prepareStatement(sql);
                pst.setInt(1, id);
                rs = pst.executeQuery();
                if(rs.next()) {
                    habit.setId(rs.getInt("id"));
                    habit.setName(rs.getString("name"));
                    habit.setIcon(rs.getInt("icon"));
                    habit.setColor(rs.getInt("color"));
                    habit.setCategory(rs.getInt("category"));
                    habit.setWeekday(rs.getInt("weekday"));
                    habit.setCreateTime(rs.getString("create_time"));
                    habit.setClockinDays(rs.getInt("clockin_days"));
                    habit.setFlag_auto(rs.getBoolean("flag_auto"));
    ????????????????????????????????????????
                }
                else {
                    habit = null;
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            habit = null;
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(rs, pst, connection);
        }
        return habit;
    }
}
