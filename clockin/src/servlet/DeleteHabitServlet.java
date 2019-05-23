package servlet;

import dao.HabitDao;
import dao.RecordDao;
import model.Result;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/habit/deletehabit")
public class DeleteHabitServlet extends HttpServlet {
    public DeleteHabitServlet() {}

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
                result.setStatus(-2);
                result.setDesp("habitId参数格式非法");
                resp.getWriter().println(result.toJson());
                return;
            }
            HabitDao dao = new HabitDao();
            boolean flag = dao.deleteById(habitId, userId);
            if(!flag) {
                result.setStatus(-1);
                result.setDesp("数据库操作失败");
                resp.getWriter().println(result.toJson());
                return;
            } else {
                RecordDao dao2 = new RecordDao();
                flag = dao2.deleteByHabitId(habitId, userId);
                result.setStatus(1);
                result.setDesp("删除成功");
                resp.getWriter().println(result.toJson());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setStatus(-2);
            result.setDesp("habitId参数格式非法");
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
