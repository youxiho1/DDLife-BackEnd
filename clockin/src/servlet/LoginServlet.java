package servlet;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import dao.UserDao;
import model.Result;
import utils.MD5Util;
import utils.UserUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
    private String appid = "wxbdc188662f655d72";
    private String secretKey = "82a6eb99bd1b6000dd653d545b5046d4";

    public LoginServlet() {}

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Result result = new Result();
        try {
            String results = "";
            String code = request.getParameter("code");   //拿到微信小程序传过来的code
            if(code == null || code.length() <= 0) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                result.setStatus(-2);
                result.setDesp("code格式错误");
                response.getWriter().println(result.toJson());
            }
            System.out.println(code);
            String url = "https://api.weixin.qq.com/sns/jscode2session?appid=" + appid + "&secret=" + secretKey
                    + "&js_code=" + code + "&grant_type=authorization_code";   //接口地址
            System.out.println("url"+url);
            results = sendGetReq(url);// 发送http请求
            System.out.println("results"+results);
            JSONObject jsonObject = JSON.parseObject(results);
            String session = jsonObject.getString("session_key");
            String openid = jsonObject.getString("openid");
            if(session == null || session.length() <= 0) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                result.setStatus(-10);
                result.setDesp("登录失败");
                response.getWriter().println(result.toJson());
                return;
            }
            if(openid == null || openid.length() <= 0) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                result.setStatus(-10);
                result.setDesp("登录失败");
                response.getWriter().println(result.toJson());
                return;
            }
            System.out.println("session:" + session + ", openid:" + openid);
            session = MD5Util.encode(session);
            UserDao dao = new UserDao();
            dao.delete(openid);
            dao.add(openid, session);
            response.setContentType("application/json;charset=UTF-8");
            response.setHeader("catch-control", "no-catch");
            PrintWriter out = response.getWriter();
            out.write("{\"token\": \""+ session +"\"}");
            out.flush();
            out.close();
        } catch (Exception e) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            result.setStatus(-4);
            result.setDesp("登录失败");
            response.getWriter().println(result.toJson());
            e.printStackTrace();
        }
    }

    private String sendGetReq(String url) {
        String result = "";
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            java.util.Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
            System.out.println("result=" + result);
        } catch (Exception e) {
            System.out.println("发送GET请求出现异常！" + e);
            e.printStackTrace();
        } // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
