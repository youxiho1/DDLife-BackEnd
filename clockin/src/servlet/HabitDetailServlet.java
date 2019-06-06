package servlet;

import com.alibaba.fastjson.JSONObject;
import dao.HabitDao;
import model.Habit;
import model.Result;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/habit/detail")
public class HabitDetailServlet extends HttpServlet {
    public HabitDetailServlet() {}

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
            int id = Integer.parseInt(req.getParameter("id"));
            if(id <= 0) {
                result.setDesp("id参数格式错误");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
            HabitDao dao = new HabitDao();
            Habit habit = dao.findById(id, userId);
            if(habit == null) {
                result.setStatus(-8);
                result.setDesp("不存在该id对应的习惯");
                resp.getWriter().println(result.toJson());
                return;
            }
            resp.getWriter().println(JSONObject.toJSON(habit));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setStatus(-2);
            result.setDesp("参数格式错误");
            resp.getWriter().println(result.toJson());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            result.setStatus(-4);
            result.setDesp("未知错误");
            return;
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }
}
