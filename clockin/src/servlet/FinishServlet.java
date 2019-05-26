package servlet;

import dao.PlanDao;
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

@WebServlet("/api/plan/finish")
public class FinishServlet extends HttpServlet {
    public FinishServlet() {}

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
            int planId = Integer.parseInt(req.getParameter("planId"));
            if(planId <= 0) {
                result.setDesp("planId参数格式非法");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
            PlanDao dao = new PlanDao();
            int kind = Integer.parseInt(req.getParameter("kind"));
            if(kind == 0) {
                //取消打卡
                int status = dao.cancelFinish(planId, userId);
                if(status == -1) {
                    result.setStatus(-1);
                    result.setDesp("数据库操作失败");
                    resp.getWriter().println(result.toJson());
                } else if(status == -8) {
                    result.setStatus(-8);
                    result.setDesp("该id的计划不存在");
                    resp.getWriter().println(result.toJson());
                } else if(status == -6) {
                    result.setStatus(-6);
                    result.setDesp("该计划的deadline不是今日");
                    resp.getWriter().println(result.toJson());
                } else if(status == -5) {
                    result.setStatus(-5);
                    result.setDesp("权限错误或该计划未完成");
                    resp.getWriter().println(result.toJson());
                    return;
                } else {
                    result.setStatus(1);
                    result.setDesp("取消完成该习惯成功");
                    resp.getWriter().println(result.toJson());
                }
            } else if(kind == 1) {
                //打卡
                int status = dao.finish(planId, userId);
                if(status == -1) {
                    result.setStatus(-1);
                    result.setDesp("数据库操作失败");
                    resp.getWriter().println(result.toJson());
                } else if(status == -8) {
                    result.setStatus(-8);
                    result.setDesp("该id的计划不存在");
                    resp.getWriter().println(result.toJson());
                } else if(status == -6) {
                    result.setStatus(-6);
                    result.setDesp("该计划的deadline不是今天");
                    resp.getWriter().println(result.toJson());
                } else if(status == -5) {
                    result.setStatus(-5);
                    result.setDesp("权限错误或该计划已经完成");
                    resp.getWriter().println(result.toJson());
                    return;
                } else {
                    result.setStatus(1);
                    result.setDesp("成功完成该习惯");
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
