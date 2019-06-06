package servlet;

import dao.PlanDao;
import model.Plan;
import model.Result;
import utils.DateUtil;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/plan/updateplan")
public class AddPlanServlet extends HttpServlet {
    public AddPlanServlet() {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=UTF-8");
        Result result = new Result();
        String token = null;
        token = req.getParameter("token");
        if(token == null || token.length() == 0) {
            result.setStatus(-3);
            result.setDesp("身份信息校验错误");
            resp.getWriter().println(result.toJson());
            return;
        }
        String userId = UserUtil.token2userid(token);
        if(userId == null || userId.length() == 0) {
            result.setStatus(-3);
            result.setDesp("身份信息校验错误");
            resp.getWriter().println(result.toJson());
            return;
        }
        int id = 0;
        try {
            id = Integer.parseInt(req.getParameter("id"));
            if(id < 0) {
                result.setDesp("id参数格式错误");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
            String title = req.getParameter("title");
            if(title == null || title.length() <= 0) {
                result.setDesp("title参数格式错误");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
            int icon = Integer.parseInt(req.getParameter("icon"));
            if(icon <= 0) {
                result.setDesp("icon参数格式错误");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
            String desp = req.getParameter("desp");
            if(desp != null && desp.length() >= 34) {
                result.setStatus(-2);
                result.setDesp("desp参数格式错误");
                resp.getWriter().println(result.toJson());
                return;
            }
            String deadline = req.getParameter("deadline");
            if(deadline == null || deadline.length() <= 0) {
                result.setStatus(-2);
                result.setDesp("deadline参数格式错误");
                resp.getWriter().println(result.toJson());
                return;
            }
            Plan plan = new Plan();
            plan.setUserId(userId);
            plan.setId(id);
            plan.setIcon(icon);
            plan.setDesp(desp);
            plan.setTitle(title);
            plan.setDeadline(deadline);
            if(id == 0) {
                if(DateUtil.getDeltaDate(plan.getDeadline(), DateUtil.getNowDateTime("yyyyMMdd")) > 0) {
                    //不能把deadline调到过去
                    result.setStatus(-9);
                    result.setDesp("你不能把deadline选择在过去的一天");
                    resp.getWriter().println(result.toJson());
                    return;
                }
                PlanDao dao = new PlanDao();
                boolean flag = dao.addPlan(plan);
                if(flag) {
                    result.setStatus(1);
                    result.setDesp("添加习惯成功");
                    resp.getWriter().println(result.toJson());
                } else {
                    result.setStatus(-1);
                    result.setDesp("数据库操作失败");
                    resp.getWriter().println(result.toJson());
                }
            } else {
                PlanDao dao = new PlanDao();
                int flag = dao.updatePlan(plan);
                result.setStatus(flag);
                switch (flag) {
                    case 1:
                        result.setDesp("修改习惯成功");
                        break;
                    case -1:
                        result.setDesp("数据库操作失败");
                        break;
                    case -8:
                        result.setDesp("该用户不存在id为这样的计划");
                        break;
                    case -9:
                        result.setDesp("不能把deadline调整到过去");
                        break;

                }
                resp.getWriter().println(result.toJson());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setDesp("参数格式错误");
            result.setStatus(-2);
            resp.getWriter().println(result.toJson());
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus(-4);
            result.setDesp("未知错误");
            resp.getWriter().println(result.toJson());
        }
    }
}
