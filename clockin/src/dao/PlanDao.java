//package dao;
//
//import model.Plan;
//import model.Plans;
//import model.Record;
//import utils.DatabaseUtil;
//import utils.DateUtil;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class PlanDao {
//    public PlanDao() {}
//
//    public boolean addPlan(Plan plan) {
//        Connection connection = null;
//        PreparedStatement pst = null;
//        boolean flag = true;
//        String sql = "insert into Plan(user_id, title, icon, desp, deadline, flag_finish, create_time)" +
//                " values(?,?,?,?,?,?,?)";
//        connection = DatabaseUtil.getConnection();
//        try {
//            connection.setAutoCommit(false);
//            pst = connection.prepareStatement(sql);
//            pst.setString(1, plan.getUserId());
//            pst.setString(2, plan.getTitle());
//            pst.setInt(3, plan.getIcon());
//            pst.setString(4, plan.getDesp());
//            pst.setString(5, plan.getDeadline());
//            pst.setBoolean(6, false);
//            pst.setString(7, DateUtil.getNowDateTime(""));
//            if(pst.executeUpdate() != 1) {
//                flag = false;
//            }
//            connection.commit();
//            connection.setAutoCommit(true);
//        } catch (SQLException e) {
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//            e.printStackTrace();
//            flag = false;
//        } finally {
//            DatabaseUtil.close(null, pst, connection);
//        }
//        return flag;
//    }
//
//    public boolean updatePlan(Plan plan) {
//        boolean flag = true;
//        Connection connection = null;
//        PreparedStatement pst = null;
//        String sql = "update Plan set title = ?, icon = ?, desp = ? where id = ? and user_id = ?";
//        try {
//            connection = DatabaseUtil.getConnection();
//            connection.setAutoCommit(false);
//            pst = connection.prepareStatement(sql);
//            pst.setString(1, plan.getTitle());
//            pst.setInt(2, plan.getIcon());
//            pst.setString(3, plan.getDesp());
//            pst.setInt(4, habit.getWeekday());
//            pst.setInt(5, habit.getId());
//            pst.setString(6, habit.getUserId());
//            if(pst.executeUpdate() != 1) {
//                flag = false;
//            }
//            connection.commit();
//            connection.setAutoCommit(true);
//        } catch (SQLException e) {
//            flag = false;
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseUtil.close(null, pst, connection);
//        }
//        return flag;
//    }
//
//    public boolean deletePlan(int planId, String userId) {
//        boolean flag = true;
//        Connection connection = null;
//        PreparedStatement pst = null;
//        String sql = "delete from Plan where id = ? and user_id = ?";
//        try {
//            connection = DatabaseUtil.getConnection();
//            connection.setAutoCommit(false);
//            pst = connection.prepareStatement(sql);
//            pst.setInt(1, planId);
//            pst.setString(2, userId);
//            if(pst.executeUpdate() != 1) {
//                flag = false;
//            }
//            connection.commit();
//            connection.setAutoCommit(true);
//        } catch (SQLException e) {
//            flag = false;
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseUtil.close(null, pst, connection);
//        }
//        return flag;
//    }
//
//    public int finish() {
//
//    }
//
//    public boolean updateDeadline() {
//
//    }
//
//    public Plans findTodayPlan(String userId) {
//
//    }
//
//    public Plans findAllPlan(String userId) {
//        Connection connection = null;
//        PreparedStatement pst = null;
//        ResultSet rs = null;
//        List<Plan> list1 = new ArrayList<>();
//        List<Plan> list2 = new ArrayList<>();
//        String sql = "select * from Plan where user_id = ? order by clockin_date";
//        try {
//            connection = DatabaseUtil.getConnection();
//            connection.setAutoCommit(false);
//            pst = connection.prepareStatement(sql);
//            pst.setInt(1, habitId);
//            pst.setString(2, userId);
//            pst.setString(3, min);
//            pst.setString(4, max);
//            rs = pst.executeQuery();
//            Record record;
//            while (rs.next()) {
//                record = new Record();
//                record.setId(rs.getInt("id"));
//                record.setHabitId(rs.getInt("habit_id"));
//                record.setClockin_date(rs.getString("clockin_date"));
//                record.setClockin_time(rs.getString("clockin_time"));
//                record.setDescription(rs.getString("description"));
//                list.add(record);
//            }
//            connection.commit();
//            connection.setAutoCommit(true);
//        } catch (SQLException e) {
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//            e.printStackTrace();
//        }
//        return list;
//    }
//
//    public boolean hasPermission(int planId, String userId) {
//        Connection connection = null;
//        PreparedStatement pst = null;
//        ResultSet rs = null;
//        String sql = "select * from Plan where id = ? and user_id = ?";
//        boolean flag = true;
//        connection = DatabaseUtil.getConnection();
//        try {
//            connection.setAutoCommit(false);
//            if(planId <= 0) {
//                flag = false;
//            }
//            else {
//                pst = connection.prepareStatement(sql);
//                pst.setInt(1, planId);
//                pst.setString(2, userId);
//                rs = pst.executeQuery();
//                flag = rs.next();
//            }
//            connection.commit();
//            connection.setAutoCommit(true);
//        } catch (SQLException e) {
//            flag = false;
//            try {
//                connection.rollback();
//            } catch (SQLException e1) {
//                e1.printStackTrace();
//            }
//            e.printStackTrace();
//        } finally {
//            DatabaseUtil.close(rs, pst, connection);
//        }
//        return flag;
//    }
//
//
//}
