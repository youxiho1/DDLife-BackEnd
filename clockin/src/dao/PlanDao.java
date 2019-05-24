package dao;

import model.*;
import utils.DatabaseUtil;
import utils.DateUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PlanDao {
    public PlanDao() {}

    public boolean addPlan(Plan plan) {
        Connection connection = null;
        PreparedStatement pst = null;
        boolean flag = true;
        String sql = "insert into Plan(user_id, title, icon, desp, deadline, flag_finish, create_time)" +
                " values(?,?,?,?,?,?,?)";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, plan.getUserId());
            pst.setString(2, plan.getTitle());
            pst.setInt(3, plan.getIcon());
            pst.setString(4, plan.getDesp());
            pst.setString(5, plan.getDeadline());
            pst.setBoolean(6, false);
            pst.setString(7, DateUtil.getNowDateTime(""));
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

    public int updatePlan(Plan plan) {
        int flag = 1;       //flag=1代表操作成功
        PlanDao dao = new PlanDao();
        Plan plan1 = dao.findById(plan.getId(), plan.getUserId());
        if(plan1 == null) {
            //状态码-8，该用户不存在id为这样的计划
            return -8;

        } else if(!(plan1.getDeadline().equals(plan.getDeadline()))) {
            //两者deadline不同
            Connection connection = null;
            PreparedStatement pst = null;
            String sql = "update Plan set title = ?, icon = ?, desp = ?, deadline = ?, flag_finish = ?, finish_time = ? where id = ? and user_id = ?";
            try {
                connection = DatabaseUtil.getConnection();
                connection.setAutoCommit(false);
                pst = connection.prepareStatement(sql);
                pst.setString(1, plan.getTitle());
                pst.setInt(2, plan.getIcon());
                pst.setString(3, plan.getDesp());
                pst.setString(4, plan.getDeadline());
                pst.setBoolean(5, false);
                pst.setString(6, "");
                pst.setInt(7, plan.getId());
                pst.setString(8, plan.getUserId());
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

        } else {
            //两者deadline相同
            Connection connection = null;
            PreparedStatement pst = null;
            String sql = "update Plan set title = ?, icon = ?, desp = ? where id = ? and user_id = ?";
            try {
                connection = DatabaseUtil.getConnection();
                connection.setAutoCommit(false);
                pst = connection.prepareStatement(sql);
                pst.setString(1, plan.getTitle());
                pst.setInt(2, plan.getIcon());
                pst.setString(3, plan.getDesp());
                pst.setInt(4, plan.getId());
                pst.setString(5, plan.getUserId());
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
        }
        return flag;
    }

    public boolean deletePlan(int planId, String userId) {
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "delete from Plan where id = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setInt(1, planId);
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

    public int finish(int planId, String userId) {
        int flag = 1;
        PlanDao dao = new PlanDao();
        Plan plan = dao.findById(planId, userId);
        if(plan == null) {
            return -8;      //flag = -8 该id的plan不存在
        }
        boolean now = dao.isFinish(planId, userId);
        if(now) {
            //应该执行的操作和实际执行的错误不符合
            return -5;
        }
        String today = DateUtil.getNowDateTime("yyyyMMdd");
        if(!today.equals(plan.getDeadline())) {
            //这不是今天应该完成的计划
            return -6;    //flag = -6 这不是今天该完成的计划
        }
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "update Plan set flag_finish = ?, finish_time = ? where id = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setBoolean(1, true);
            pst.setString(2, DateUtil.getNowDateTime(""));
            pst.setInt(3, planId);
            pst.setString(4, userId);
            if (pst.executeUpdate() != 1) {
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

    public int cancelFinish(int planId, String userId) {
        int flag = 1;
        PlanDao dao = new PlanDao();
        Plan plan = dao.findById(planId, userId);
        if(plan == null) {
            return -8;      //flag = -8 该id的plan不存在
        }
        boolean now = dao.isFinish(planId, userId);
        if(!now) {
            //应该执行的操作和实际执行的错误不符合
            return -5;
        }
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "update Plan set flag_finish = ?, finish_time = ?, deadline = ? where id = ? and user_id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setBoolean(1, false);
            pst.setString(2, DateUtil.getNowDateTime(""));
            pst.setString(3, DateUtil.getNowDateTime("yyyyMMdd"));
            pst.setInt(4, planId);
            pst.setString(5, userId);
            if (pst.executeUpdate() != 1) {
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

    public boolean delay(String userId) {
        //这个函数的返回值没有任何意义
        boolean flag = true;
        Connection connection = null;
        PreparedStatement pst = null;
        String sql = "update Plan set deadline = ?, finish_time = ?, flag_finish = ? where user_id = ? and flag_finish = ? and deadline < ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, DateUtil.getNowDateTime("yyyyMMdd"));
            pst.setString(2, "");
            pst.setBoolean(3, false);
            pst.setString(4, userId);
            pst.setBoolean(5, false);
            pst.setString(6, DateUtil.getNowDateTime("yyyyMMdd"));
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

    public TodayPlans findTodayPlan(String userId) {
        PlanDao dao = new PlanDao();
        dao.delay(userId);
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        TodayPlans todayPlans = new TodayPlans();
        List<Plan> unlist = new ArrayList<>();
        List<Plan> list = new ArrayList<>();
        String sql = "select * from Plan where user_id = ? and deadline = ? order by flag_finish desc, create_time asc";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, userId);
            pst.setString(2, DateUtil.getNowDateTime(""));
            rs = pst.executeQuery();
            Plan plan;
            while (rs.next()) {
                plan = new Plan();
                plan.setId(rs.getInt("id"));
                plan.setCreate_time(rs.getString("create_time"));
                plan.setDeadline(rs.getString("deadline"));
                plan.setDesp(rs.getString("desp"));
                plan.setFinish_time(rs.getString("finish_time"));
                plan.setIcon(rs.getInt("icon"));
                plan.setTitle(rs.getString("title"));
                plan.setFlag_finish(rs.getBoolean("flag_finish"));
                if(plan.isFlag_finish()) {
                    //已完成
                    list.add(plan);
                } else {
                    //未完成
                    unlist.add(plan);
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
        todayPlans.setUnfinished(unlist);
        todayPlans.setFinished(list);
        return todayPlans;
    }

    public AllPlans findAllPlan(String userId) {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        AllPlans allPlans = new AllPlans();
        List<DatePlan> unfinished = new ArrayList<>();
        List<DatePlan> finished = new ArrayList<>();
        String sql = "select * from Plan where user_id = ? and flag_finish = 0 order by deadline desc";
        String sql2 = "select * from Plan where user_id = ? and flag_finish = 1 order by deadline desc";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, userId);
            rs = pst.executeQuery();
            DatePlan datePlan = new DatePlan();
            datePlan.setDate("init");
            String front = "";
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setId(rs.getInt("id"));
                plan.setCreate_time(rs.getString("create_time"));
                plan.setDeadline(rs.getString("deadline"));
                plan.setDesp(rs.getString("desp"));
                plan.setFinish_time(rs.getString("finish_time"));
                plan.setIcon(rs.getInt("icon"));
                plan.setTitle(rs.getString("title"));
                plan.setFlag_finish(rs.getBoolean("flag_finish"));
                if(plan.getDeadline().equals(front)) {
                    //这一个计划和上一个计划的deadline是同一个天，应该属于同一个DatePlan
                    datePlan.getList().add(plan);
                } else {
                    //这一个计划和上一个计划的deadline是不同的，应该新建一个DatePlan
                    if(!datePlan.getDate().equals("init")) {
                        unfinished.add(datePlan);
                    }
                    front = datePlan.getDate();
                    datePlan = new DatePlan();
                    datePlan.setDate(plan.getDeadline());
                    List<Plan> list = new ArrayList<>();
                    datePlan.setList(list);
                    datePlan.getList().add(plan);
                }
            }

            //我觉得这个玩意绝对有盲点
            if(unfinished.size() == 0 && !datePlan.getDate().equals("init")) {
                unfinished.add(datePlan);
            } else if(unfinished.size() > 0 && !datePlan.getDate().equals("init") && !unfinished.get(unfinished.size() - 1).getDate().equals(datePlan.getDate())) {
                unfinished.add(datePlan);
            }
            connection.commit();
            connection.setAutoCommit(true);
            pst = connection.prepareStatement(sql2);
            pst.setString(1, userId);
            rs = pst.executeQuery();
            datePlan = new DatePlan();
            datePlan.setDate("init");
            front = "";
            while (rs.next()) {
                Plan plan = new Plan();
                plan.setId(rs.getInt("id"));
                plan.setCreate_time(rs.getString("create_time"));
                plan.setDeadline(rs.getString("deadline"));
                plan.setDesp(rs.getString("desp"));
                plan.setFinish_time(rs.getString("finish_time"));
                plan.setIcon(rs.getInt("icon"));
                plan.setTitle(rs.getString("title"));
                plan.setFlag_finish(rs.getBoolean("flag_finish"));
                if(plan.getDeadline().equals(front)) {
                    //这一个计划和上一个计划的deadline是同一个天，应该属于同一个DatePlan
                    datePlan.getList().add(plan);
                } else {
                    //这一个计划和上一个计划的deadline是不同的，应该新建一个DatePlan
                    if(!datePlan.getDate().equals("init")) {
                        finished.add(datePlan);
                    }
                    front = datePlan.getDate();
                    datePlan = new DatePlan();
                    datePlan.setDate(plan.getDeadline());
                    List<Plan> list = new ArrayList<>();
                    datePlan.setList(list);
                    datePlan.getList().add(plan);
                }
            }
            if(!finished.get(finished.size() - 1).getDate().equals(datePlan.getDate())) {
                finished.add(datePlan);
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
        allPlans.setFinished(finished);
        allPlans.setUnfinished(unfinished);
        return allPlans;
    }

    public boolean hasPermission(int planId, String userId) {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select * from Plan where id = ? and user_id = ?";
        boolean flag = true;
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            if(planId <= 0) {
                flag = false;
            }
            else {
                pst = connection.prepareStatement(sql);
                pst.setInt(1, planId);
                pst.setString(2, userId);
                rs = pst.executeQuery();
                flag = rs.next();
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

    public boolean isFinish(int planId, String userId) {
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select flag_finish, finish_time from Plan where user_id = ? and id = ?";
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql);
            pst.setString(1, userId);
            pst.setInt(2, planId);
            rs = pst.executeQuery();
            if (rs.next()) {
                Plan plan = new Plan();
                plan.setFinish_time(rs.getString("finish_time"));
                plan.setFlag_finish(rs.getBoolean("flag_finish"));
                return plan.isFlag_finish();
            }
            connection.commit();
            connection.setAutoCommit(true);
            return false;
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            return false;
        }
    }

    public Plan findById(int id, String userId) {
        Plan plan = new Plan();
        Connection connection = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = "select * from Plan where id = ? and user_id = ?";
        connection = DatabaseUtil.getConnection();
        try {
            connection.setAutoCommit(false);
            if(id <= 0) {
                plan = null;
            }
            else {
                pst = connection.prepareStatement(sql);
                pst.setInt(1, id);
                pst.setString(2, userId);
                rs = pst.executeQuery();
                if(rs.next()) {
                    plan.setId(rs.getInt("id"));
                    plan.setCreate_time(rs.getString("create_time"));
                    plan.setDeadline(rs.getString("deadline"));
                    plan.setDesp(rs.getString("desp"));
                    plan.setFinish_time(rs.getString("finish_time"));
                    plan.setIcon(rs.getInt("icon"));
                    plan.setTitle(rs.getString("title"));
                    plan.setFlag_finish(rs.getBoolean("flag_finish"));
                }
                else {
                    plan = null;
                }
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            plan = null;
            try {
                connection.rollback();
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            DatabaseUtil.close(rs, pst, connection);
        }
        return plan;
    }
}
