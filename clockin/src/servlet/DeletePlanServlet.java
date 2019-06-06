package servlet;

import dao.PlanDao;
import model.Result;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/plan/deleteplan")
public class DeletePlanServlet extends HttpServlet {
    public DeletePlanServlet() {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
        try {
            int planId = Integer.parseInt(req.getParameter("planId"));
            if(planId <= 0) {
                result.setStatus(-2);
                result.setDesp("planId参数格式非法");
                resp.getWriter().println(result.toJson());
                return;
            }
            PlanDao dao = new PlanDao();
            boolean flag = dao.deletePlan(planId, userId);
            if(!flag) {
                result.setStatus(-1);
                result.setDesp("数据库操作失败");
                resp.getWriter().println(result.toJson());
                return;
            } else {
                result.setStatus(1);
                result.setDesp("删除成功");
                resp.getWriter().println(result.toJson());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setStatus(-2);
            result.setDesp("planId参数格式非法");
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
