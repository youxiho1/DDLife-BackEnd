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
            if(DateUtil.getDeltaDate(plan.getDeadline(), DateUtil.getNowDateTime("yyyyMMdd")) > 0) {
                //不能把deadline调到过去
                return -9;  //状态码-9，不能把deadline调整到过去
            }
            Connection connection = null;
            PreparedStatement pst = null;
            String sql = "update Plan set title = ?, icon = ?, desp = ?, deadline = ?, flag_finish = ?, finish_time = ?, create_time = ? where id = ? and user_id = ?";
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
                pst.setString(7, DateUtil.getNowDateTime(""));
                pst.setInt(8, plan.getId());
                pst.setString(9, plan.getUserId());
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
            pst.setString(2, DateUtil.getNowDateTime("yyyyMMdd"));
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
            //String front = "";
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
                if(plan.getDeadline().equals(datePlan.getDate())) {
                    //这一个计划和上一个计划的deadline是同一个天，应该属于同一个DatePlan
                    datePlan.addPlan(plan);
                } else {
                    //这一个计划和上一个计划的deadline是不同的，应该新建一个DatePlan
                    if(!datePlan.getDate().equals("init")) {
                        unfinished.add(datePlan);
                    }
                    datePlan = new DatePlan();
                    datePlan.setDate(plan.getDeadline());
                    datePlan.addPlan(plan);
                }
            }
            if(!datePlan.getDate().equals("init") && !unfinished.contains(datePlan)) {
                //不能没查到东西，现在集合里面不能有（以免重新添加）
                unfinished.add(datePlan);
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
        allPlans.setUnfinished(unfinished);
        try {
            connection = DatabaseUtil.getConnection();
            connection.setAutoCommit(false);
            pst = connection.prepareStatement(sql2);
            pst.setString(1, userId);
            rs = pst.executeQuery();
            DatePlan datePlan = new DatePlan();
            datePlan.setDate("init");
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
                if(plan.getDeadline().equals(datePlan.getDate())) {
                    //这一个计划和上一个计划的deadline是同一个天，应该属于同一个DatePlan
                    datePlan.addPlan(plan);
                } else {
                    //这一个计划和上一个计划的deadline是不同的，应该新建一个DatePlan
                    if(!datePlan.getDate().equals("init")) {
                        finished.add(datePlan);
                    }
                    datePlan = new DatePlan();
                    datePlan.setDate(plan.getDeadline());
                    datePlan.addPlan(plan);
                }
            }
            if(!datePlan.getDate().equals("init") && !finished.contains(datePlan)) {
                //不能没查到东西，现在集合里面不能有（以免重新添加）
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

    public static void main(String[] args) {
        PlanDao dao = new PlanDao();
        //AddPlan测试
        /*Plan plan = new Plan();
        plan.setUserId("user2");
        plan.setIcon(1);
        plan.setTitle("title");
        plan.setDesp("");
        plan.setDeadline(DateUtil.getNowDateTime("yyyyMMdd"));
        dao.addPlan(plan);
        dao.addPlan(plan);*/
        //findTodayPlan测试(混合了delay的测试)
        /*TodayPlans todayPlans = dao.findTodayPlan("user");
        todayPlans = dao.findTodayPlan("user2");*/
        //hasPermission测试
        /*boolean flag = dao.hasPermission(2, "user2");
        System.out.println(flag);
        flag = dao.hasPermission(2, "user");
        System.out.println(flag);*/
        //deletePlan测试
        /*boolean flag = dao.deletePlan(2, "user" );
        System.out.println(flag);
        flag = dao.deletePlan(3, "user2");
        System.out.println(flag);
        flag = dao.deletePlan(10, "user2");
        System.out.println(flag);*/
        //isFinish测试
        /*boolean flag = dao.isFinish(2, "user");
        System.out.println(flag);
        flag = dao.isFinish(2, "user2");
        System.out.println(flag);
        flag = dao.isFinish(4, "user");
        System.out.println(flag);
        flag = dao.isFinish(4, "user2");
        System.out.println(flag);*/
        //findById测试
        /*Plan plan = dao.findById(7, "user");
        if(plan == null) {
            System.out.println("NULL");
        } else {
            System.out.println(plan.getDeadline());
        }
        plan = dao.findById(2, "user");
        if(plan == null) {
            System.out.println("NULL");
        } else {
            System.out.println(plan.getDeadline());
        }
        plan = dao.findById(2, "user2");
        if(plan == null) {
            System.out.println("NULL");
        } else {
            System.out.println(plan.getDeadline());
        }*/
        //updatePlan测试
        /*Plan plan = new Plan();
        plan.setId(10);
        plan.setUserId("user2");
        plan.setIcon(999);
        plan.setTitle("999");
        plan.setDesp("999");
        plan.setDeadline(DateUtil.getNowDateTime("yyyyMMdd"));
        int result = dao.updatePlan(plan);
        System.out.println(result);
        plan.setId(4);
        plan.setUserId("user");
        result = dao.updatePlan(plan);
        System.out.println(result);
        plan.setUserId("user2");
        plan.setDeadline("20170505");
        result = dao.updatePlan(plan);
        System.out.println(result);
        plan.setDeadline("20170506");
        result = dao.updatePlan(plan);
        System.out.println(result);
        plan.setDeadline("20200103");
        result = dao.updatePlan(plan);
        System.out.println(result);*/

        //finish & cancelFinish测试
        /*int result = dao.finish(4, "user2");
        System.out.println(result);
        result = dao.finish(3, "user2");
        System.out.println(result);
        result = dao.finish(2, "user2");
        System.out.println(result);
        result = dao.finish(5, "user");
        System.out.println(result);
        result = dao.finish(5, "user2");
        System.out.println(result);*/
        /*int result = dao.cancelFinish(4, "user2");
        System.out.println(result);
        result = dao.cancelFinish(3, "user2");
        System.out.println(result);
        result = dao.cancelFinish(2, "user2");
        System.out.println(result);
        result = dao.cancelFinish(5, "user");
        System.out.println(result);
        result = dao.cancelFinish(5, "user2");
        System.out.println(result);*/
        //findAllPlan测试
        AllPlans allPlans = dao.findAllPlan("user2");
        AllPlans allPlans1 = dao.findAllPlan("user");
    }
}
