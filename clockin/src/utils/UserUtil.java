package utils;

import dao.UserDao;

public class UserUtil {
    public static String token2userid(String token) {
        //去校验，如果有问题返回空字符串
        UserDao dao = new UserDao();
        String openid = dao.getOpenId(token);
        return openid;
    }

//    public static String userid2token(String userid, String session) {
//        return MD5Util.encode(userid)+session;
//    }
}
