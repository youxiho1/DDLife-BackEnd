package servlet;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import dao.HabitDao;
import model.Habit;
import model.Habits;
import model.Result;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/api/habit/gethabit")
public class GetHabitServlet extends HttpServlet {
    public GetHabitServlet() {}

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
            int kind = Integer.parseInt(req.getParameter("kind"));
            if(kind == 1) {
                //查询今日习惯
                HabitDao dao = new HabitDao();
                Habits habits = dao.findTodayHabit(userId);
                resp.getWriter().println(JSONObject.toJSON(habits));
            } else if(kind == 2) {
                //查询全部习惯
                HabitDao dao = new HabitDao();
                Habits habits = dao.findAllHabitByUserId(userId, -1);
                resp.getWriter().println(JSONObject.toJSON(habits));
            } else {
                result.setStatus(-2);
                result.setDesp("参数kind格式错误");
                resp.getWriter().println(result.toJson());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            result.setStatus(-2);
            result.setDesp("参数kind格式错误");
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

    public static void main(String[] args) {
        Habit habit = new Habit();
        habit.setUserId("111");
        List<Habit> list = new ArrayList<>();
        list.add(habit);
        habit.setUserId("222");
        list.add(habit);
        System.out.println(JSONArray.toJSON(list));
    }
}
