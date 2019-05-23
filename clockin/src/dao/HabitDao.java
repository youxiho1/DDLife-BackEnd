package dao;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import model.Habit;
import model.Habits;
import model.Record;
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
        String sql = "insert into Habit(user_id, name, icon, category, weekday, create_time, clockin_days)" +
                " values(?,?,?,?,?,?,?)";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, habit.getUserId());
            pst.setString(2,habit.getName());
            pst.setInt(3,habit.getIcon());
            pst.setInt(4, habit.getCategory());
            pst.setInt(5, habit.getWeekday());
            pst.setString(6, DateUtil.getNowDateTime(""));
            pst.setInt(7, 0);
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

    public boolean deleteById(int id, String userId) {
        //删除一条习惯
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "delete from Habit where id = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, userId);
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

    public boolean updateHabit(Habit habit) {
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "update Habit set name = ?, icon = ?, category = ?, weekday = ? where id = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, habit.getName());
            pst.setInt(2, habit.getIcon());
            pst.setInt(3, habit.getCategory());
            pst.setInt(4, habit.getWeekday());
            pst.setInt(5, habit.getId());
            pst.setString(6, habit.getUserId());
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

    public int clockIn(int id, boolean op, String userId) {
        //用来打卡,op=false为取消打卡，op为true为打卡
        int flag = 1;       //flag = 1代表打卡成功或取消打卡成功
        HabitDao dao = new HabitDao();
        Habit habit = dao.findById(id, userId);
        if(habit == null) {
            return -5;       //flag = -5代表该id的Habit不存在
        }
        int weekday = habit.getWeekday();
        int dayOfWeek = DateUtil.getDayOfWeek();
        int temp = 1 << (7 - dayOfWeek);
        if((temp & weekday) != temp) {
            return -6;      //flag = -6 今天不应该打这个卡
        }
        if(op) {
            //仅打卡需要判断时间
            int category = habit.getCategory();
            String time = DateUtil.getNowTime();
            int hour = Integer.parseInt(time.substring(0, 2));
            switch (category) {
                case 1:
                    if(hour < 5 || hour > 10) {
                        return -7;
                    }
                    break;
                case 2:
                    if(hour < 11 || hour > 16) {
                        return -7;
                    }
                    break;
                case 3:
                    if(hour >= 5 && hour <= 16) {
                        return -7;
                    }
                    break;
            }
        }
        Connection connection = null;
        PreparedStatement pst = null;
        String sql;
        if(op) {
            sql = "update Habit set clockin_days = clockin_days + 1 where id = ? and user_id = ?";
        } else {
            sql = "update Habit set clockin_days = clockin_days - 1 where id = ? and user_id = ?";
        }
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, id);
            pst.setString(2, userId);
            if(pst.executeUpdate() != 1) {
                flag = -1;
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            flag = -1;
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
        HabitDao a = new HabitDao();
        //AddHabit测试
        /*Habit h = new Habit();
        h.setUserId("user");
        h.setName("name");
        h.setIcon(1);
        h.setColor(2);
        h.setCategory(3);
        h.setWeekday(4);
        h.setFlag_auto(true);
        a.addHabit(h);
        h.setUserId("user2");
        h.setName("name2");
        a.addHabit(h);*/

        //deleteById测试
        /*boolean flag = a.deleteById(5, "user");
        System.out.println(flag);*/

        //updateHabit测试
        /*Habit n = new Habit();
        n.setUserId("user");
        n.setId(7);
        n.setName("k");
        n.setIcon(9);
        n.setColor(9);
        n.setCategory(9);
        n.setFlag_auto(false);
        n.setWeekday(9);
        boolean flag = a.updateHabit(n);
        System.out.println(flag);*/

        //clockin测试
        /*boolean flag = a.clockIn(7, true, "user2");
        System.out.println(flag);*/

        //findAllHabitByUserId测试
        /*List<Habit> list = a.findAllHabitByUserId("user2", 3);
        for (Habit habit:list
            ) {
            System.out.println(habit.getId());
        }*/

        //findById测试
        /*Habit habit = a.findById(6, "user2");
        System.out.println(habit);*/

        //findTodayHabit
        Habits habits = a.findAllHabitByUserId("user2", -1);
        System.out.println(JSONObject.toJSON(habits));


//        System.out.println(DateUtil.getDayOfWeek());
        //System.out.println(DateUtil.getNowTime());
        //System.out.println(DateUtil.getNowDateTime("yyyyMMdd"));
    }

    public Habits findAllHabitByUserId(String userId, int category) {
        //查询所有习惯时使用，-1代表所有习惯
        Habits habits = new Habits();
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Habit> list0 = new ArrayList<>();
        List<Habit> list1 = new ArrayList<>();
        List<Habit> list2 = new ArrayList<>();
        List<Habit> list3 = new ArrayList<>();
        String sql = "select * from Habit where user_id = ? and category = ? order by create_time asc";
        if(category == -1) {
            sql = "select * from Habit where user_id = ? order by create_time asc";
        }
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, userId);
            if(category != -1) {
                pst.setInt(2, category);
            }
            rs = pst.executeQuery();
            Habit habit;
            while(rs.next()) {
                habit = new Habit();
                habit.setId(rs.getInt("id"));
                habit.setName(rs.getString("name"));
                habit.setIcon(rs.getInt("icon"));
                habit.setClockinDays(rs.getInt("clockin_days"));
                habit.setCreateTime(rs.getString("create_time"));
                habit.setWeekday(rs.getInt("weekday"));
                habit.setCategory(rs.getInt("category"));
                switch (habit.getCategory()) {
                    case 0:
                        list0.add(habit);
                        break;
                    case 1:
                        list1.add(habit);
                        break;
                    case 2:
                        list2.add(habit);
                        break;
                    case 3:
                        list3.add(habit);
                        break;
                }
            }
            habits.setAllTime(list0);
            habits.setMorning(list1);
            habits.setNoon(list2);
            habits.setEvening(list3);
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
        return habits;
    }

    public Habit findById(int id, String userId) {
        //查详情时使用该方法，需要判断是否为空
        Habit habit = new Habit();
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select * from Habit where id = ? and user_id = ?";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            if(id <= 0) {
                habit= null;
            }
            else {
                pst = connection.prepareStatement(sql);
                pst.setInt(1, id);
                pst.setString(2, userId);
                rs = pst.executeQuery();
                if(rs.next()) {
                    habit.setId(rs.getInt("id"));
                    habit.setName(rs.getString("name"));
                    habit.setIcon(rs.getInt("icon"));
                    habit.setCategory(rs.getInt("category"));
                    habit.setWeekday(rs.getInt("weekday"));
                    habit.setCreateTime(rs.getString("create_time"));
                    habit.setClockinDays(rs.getInt("clockin_days"));
                    RecordDao dao = new RecordDao();
                    int days = dao.getInsistDays(habit.getId(), userId);
                    habit.setInsistDays(days);
                    boolean today = dao.isTodayClockIn(id, userId);
                    habit.setFlag_today(today);
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

    public Habits findTodayHabit(String userId) {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        Habits habits = new Habits();
        List<Habit> list0 = new ArrayList<>();
        List<Habit> list1 = new ArrayList<>();
        List<Habit> list2 = new ArrayList<>();
        List<Habit> list3 = new ArrayList<>();
        String sql = "select * from Habit where user_id = ? order by category asc, create_time asc";
        int dayOfWeek = DateUtil.getDayOfWeek();
        int temp = 1 << (7 - dayOfWeek);
        //System.out.println(temp);
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, userId);
            rs = pst.executeQuery();
            Habit habit;
            while(rs.next()) {
                habit = new Habit();
                habit.setWeekday(rs.getInt("weekday"));
//                System.out.print(habit.getWeekday() + "    ");
//                System.out.println(habit.getWeekday() & temp);
                if((habit.getWeekday() & temp) == temp) {
                    habit.setId(rs.getInt("id"));
                    habit.setName(rs.getString("name"));
                    habit.setIcon(rs.getInt("icon"));
                    habit.setClockinDays(rs.getInt("clockin_days"));
                    habit.setCreateTime(rs.getString("create_time"));
                    habit.setCategory(rs.getInt("category"));
                    RecordDao dao = new RecordDao();
                    boolean flagToday = dao.isTodayClockIn(habit.getId(), userId);
                    int days = dao.getInsistDays(habit.getId(), userId);
                    habit.setFlag_today(flagToday);
                    habit.setInsistDays(days);
                    switch (habit.getCategory()) {
                        case 0:
                            list0.add(habit);
                            break;
                        case 1:
                            list1.add(habit);
                            break;
                        case 2:
                            list2.add(habit);
                            break;
                        case 3:
                            list3.add(habit);
                            break;
                    }
                }
            }
            habits.setAllTime(list0);
            habits.setMorning(list1);
            habits.setNoon(list2);
            habits.setEvening(list3);
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
        return habits;
    }

    public boolean habitPermission(int habitId, String userId) {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select * from Habit where id = ? and user_id = ?";
        boolean flag = true;
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            if(habitId <= 0) {
                flag = false;
            }
            else {
                pst = connection.prepareStatement(sql);
                pst.setInt(1, habitId);
                pst.setString(2, userId);
                rs = pst.executeQuery();
                if(rs.next()) {
                    flag = true;
                }
                else {
                    flag = false;
                }
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
            DatabaseUtil.close(rs, pst, connection);
        }
        return flag;
    }
}
