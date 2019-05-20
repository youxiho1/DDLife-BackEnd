package dao;

import model.Habit;
import model.Record;
import utils.DatabaseUtil;
import utils.DateUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordDao {
    public boolean addRecord(Record record) {
        //判一下这个习惯是不是自己的？？？
        //调用之后应该update该条Habit的打卡天数
        HabitDao dao = new HabitDao();
        boolean permission = dao.habitPermission(record.getHabitId(), record.getUserId());
        if(!permission) {
            return false;
        }
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
            if (pst.executeUpdate() != 1) {
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

    public boolean deleteTodayById(int id, String userId) {
        //调用之后应该将habit的打卡天数减少1
        //删除今天某一条习惯的打卡
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String today = DateUtil.getNowDateTime("yyyyMMdd");
        String sql = "delete from Record where habit_id = ? and clockin_date = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, today);
            pst.setString(3, userId);
            if (pst.executeUpdate() != 1) {
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

    public int getInsistDays(int habitId, String userId) {
        //查询一条习惯的全部打卡记录，用来检查连续打卡情况
        //先检查该用户是否有权限，如果没有权限返回-1
        HabitDao dao = new HabitDao();
        boolean permission = dao.habitPermission(habitId, userId);
        if(!permission) {
            return -1;
        }
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Record> list = new ArrayList<>();
        int count = 0;
        String sql = "select * from Record where habit_id = ? and user_id = ? order by clockin_date desc";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, habitId);
            pst.setString(2, userId);
            rs = pst.executeQuery();
            String nowDate = DateUtil.getNowDateTime("yyyyMMdd");
            boolean firstFlag = true;
            String temp;
            while (rs.next()) {
                temp = rs.getString("clockin_date");
                if(firstFlag) {
                    if (DateUtil.getDeltaDate(temp, nowDate) == 0) {
                        count = 1;
                        continue;
                    }
                }
                firstFlag = false;
                if(DateUtil.getDeltaDate(temp, nowDate) == 1) {
                    count++;
                    nowDate = temp;
                } else {
                    break;
                }
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
        return count;
    }

    public boolean isTodayClockIn(int habitId, String userId) {
        int num = 0;
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select * from Record where habit_id = ? and clockin_date = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, habitId);
            pst.setString(2, DateUtil.getNowDateTime("yyyyMMdd"));
            pst.setString(3, userId);
            rs = pst.executeQuery();
            while(rs.next()) {
                num++;
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
        return num > 0;
    }

    public boolean deleteByHabitId(int habitId, String userId) {
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "delete from Record where habit_id = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, habitId);
            pst.setString(2, userId);
            if (pst.executeUpdate() == 0) {
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

    public List<Record> findRecordByMonth(int habitId, String userId, String month) {
        //查询一条习惯的某个月的全部打卡记录
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Record> list = new ArrayList<>();
        String min = month + "01";
        int daysOfMonth = DateUtil.getDaysByYearMonth(Integer.parseInt(month.substring(0, 4)), Integer.parseInt(month.substring(4)));
        String max = month + daysOfMonth;
        String sql = "select * from Record where habitId = ? and user_id = ? and clockin_date >= ? and clockin_date <= ? order by clockin_date";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, habitId);
            pst.setString(2, userId);
            pst.setString(3, min);
            pst.setString(4, max);
            rs = pst.executeQuery();
            Record record;
            while (rs.next()) {
                record = new Record();
                record.setId(rs.getInt("id"));
                record.setHabitId(rs.getInt("habit_id"));
                record.setClockin_date(rs.getString("clockin_date"));
                record.setClockin_time(rs.getString("clockin_time"));
                record.setDescription(rs.getString("description"));
                list.add(record);
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
    }

    public static void main(String[] args) {
        RecordDao dao = new RecordDao();
        //addRecord测试
        /*Record record = new Record();
        record.setHabitId(6);
        record.setClockin_time("time");
        record.setClockin_date("date");
        record.setDescription("Happiness");
        record.setUserId("user2");
        dao.addRecord(record);*/
        //boolean flag = dao.addRecord(record);
        //System.out.println(flag);

        //deleteTodayById测试
        /*boolean flag = dao.deleteTodayById(6, "user2");
        System.out.println(flag);*/

        //getInsistDays测试
        /*int days = dao.getInsistDays(6, "user2");
        System.out.println(days);*/

        //isTodayClockIn测试
        /*boolean flag = dao.isTodayClockIn(6, "user2");
        System.out.println(flag);*/

        //deleteByHabitId测试
        boolean flag = dao.deleteByHabitId(6, "user2");
        System.out.println(flag);

        //findRecordByMonth测试

    }
}
