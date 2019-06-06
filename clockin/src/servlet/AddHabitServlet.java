package servlet;

import dao.HabitDao;
import model.Habit;
import model.Result;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

@WebServlet("/api/habit/updatehabit")
public class AddHabitServlet extends HttpServlet {

    public AddHabitServlet() {}

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
            String name = req.getParameter("name");
            name = URLDecoder.decode(name, "UTF-8");
            System.out.println(name);
            if(name == null || name.length() == 0) {
                result.setStatus(-2);
                result.setDesp("name参数格式非法");
                resp.getWriter().println(result.toJson());
                return;
            }
            int icon = Integer.parseInt(req.getParameter("icon"));
            if(icon < 0) {
                result.setDesp("icon参数格式错误");
                result.setStatus(-2);
                resp.getWriter().println(result.toJson());
                return;
            }
            int category = Integer.parseInt(req.getParameter("category"));
            if(category < 0 || category > 3) {
                //error
                result.setStatus(-2);
                result.setDesp("category参数格式非法");
                resp.getWriter().println(result.toJson());
                return;
            }
            int weekday = Integer.parseInt(req.getParameter("weekday"));
            Habit habit = new Habit();
            habit.setId(id);
            habit.setUserId(userId);
            habit.setName(name);
            habit.setIcon(icon);
            habit.setCategory(category);
            habit.setWeekday(weekday);
            HabitDao dao = new HabitDao();
            boolean success = false;
            if(id == 0) {
                //这是在创建新的习惯
                success = dao.addHabit(habit);
            } else {
                //这是要修改习惯
                success = dao.updateHabit(habit);
            }
            if(!success) {
                result.setStatus(-1);
                result.setDesp("数据库操作失败");
                resp.getWriter().println(result.toJson());
                return;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            //error
            result.setStatus(-2);
            result.setDesp("参数格式非法");
            resp.getWriter().println(result.toJson());
            return;
        } catch (Exception e) {
            e.printStackTrace();
            result.setDesp("未知错误");
            result.setStatus(-4);
            resp.getWriter().println(result.toJson());
            return;
        }
        result.setStatus(1);
        if (id == 0) {
            result.setDesp("添加习惯成功");
        } else {
            result.setDesp("修改习惯成功");
        }
        resp.getWriter().println(result.toJson());
    }

    public static void main(String[] args) {
        /*Result result = new Result();
        result.setStatus(-3);
        result.setDesp("未知错误");
        System.out.println(result.toJson());*/
        String name = "%E6%B5%8B%E8%AF%95Aa3";
        try {
            System.out.println(URLDecoder.decode(name, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }
}
