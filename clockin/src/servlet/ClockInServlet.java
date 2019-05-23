package servlet;

import dao.HabitDao;
import dao.RecordDao;
import model.Record;
import model.Result;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/habit/clockin")
public class ClockInServlet extends HttpServlet {
    public ClockInServlet() {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        HttpSession session = req.getSession();
        String token = (String) session.getAttribute("token");
        String userId = UserUtil.token2userid(token);
        Result result = new Result();
        if(userId == null || userId.length() == 0) {
            result.setStatus(-3);
            result.setDesp("身份信息校验错误");
            resp.getWriter().println(result.toJson());
            return;
        }
        try {
            int habitId = Integer.parseInt(req.getParameter("habitId"));
            if(habitId <= 0) {
                result.setDesp("habitId参数格式非法");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
            RecordDao dao = new RecordDao();
            boolean flag = dao.isTodayClockIn(habitId, userId);
            int kind = Integer.parseInt(req.getParameter("kind"));
            if(kind == 0) {
                //取消打卡
                if(!flag) {
                    result.setStatus(-5);
                    result.setDesp("权限错误或今日该习惯未打卡");
                    resp.getWriter().println(result.toJson());
                    return;
                }
                HabitDao dao2 = new HabitDao();
                int status = dao2.clockIn(habitId, false, userId);
                if(status == -1) {
                    result.setStatus(-1);
                    result.setDesp("数据库操作失败");
                    resp.getWriter().println(result.toJson());
                } else if(status == -5) {
                    result.setStatus(-8);
                    result.setDesp("该id的习惯不存在");
                    resp.getWriter().println(result.toJson());
                } else if(status == -6) {
                    result.setStatus(-6);
                    result.setDesp("这不是今天的习惯");
                    resp.getWriter().println(result.toJson());
                } else {
                    flag = dao.deleteTodayById(habitId, userId);
                    if(!flag) {
                        result.setStatus(-1);
                        result.setDesp("数据库操作失败");
                        resp.getWriter().println(result.toJson());
                        return;
                    }
                    result.setStatus(1);
                    result.setDesp("取消打卡成功");
                    resp.getWriter().println(result.toJson());
                }
            } else if(kind == 1) {
                //打卡
                if(flag) {
                    result.setStatus(-5);
                    result.setDesp("权限错误或今日该习惯已经打卡");
                    resp.getWriter().println(result.toJson());
                    return;
                }
                String description = req.getParameter("desp");
                if (description == null || description.length() == 0) {
                    description = "";
                }
                if(description.length() > 20) {
                    result.setStatus(-2);
                    result.setDesp("desp参数格式错误");
                    resp.getWriter().println(result.toJson());
                    return;
                }
                HabitDao dao2 = new HabitDao();
                int status = dao2.clockIn(habitId, true, userId);
                if(status == -1) {
                    result.setStatus(-1);
                    result.setDesp("数据库操作失败");
                    resp.getWriter().println(result.toJson());
                } else if(status == -5) {
                    result.setStatus(-8);
                    result.setDesp("该id的习惯不存在");
                    resp.getWriter().println(result.toJson());
                } else if(status == -6) {
                    result.setStatus(-6);
                    result.setDesp("这不是今天的习惯");
                    resp.getWriter().println(result.toJson());
                } else if(status == -7) {
                    result.setStatus(-7);
                    result.setDesp("当前不在打卡时间段内");
                    resp.getWriter().println(result.toJson());
                }
                else {
                    Record record = new Record();
                    record.setUserId(userId);
                    record.setDescription(description);
                    record.setHabitId(habitId);
                    flag = dao.addRecord(record);
                    if(!flag) {
                        result.setStatus(-1);
                        result.setDesp("数据库操作失败");
                        resp.getWriter().println(result.toJson());
                        return;
                    }
                    result.setStatus(1);
                    result.setDesp("打卡成功");
                    resp.getWriter().println(result.toJson());
                }
            } else {
                result.setDesp("kind参数格式非法");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setStatus(-2);
            result.setDesp("参数格式非法");
            resp.getWriter().println(result.toJson());
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus(-4);
            result.setDesp("未知错误");
            resp.getWriter().println(result.toJson());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
