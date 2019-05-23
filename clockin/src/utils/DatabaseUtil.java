package utils;

import java.sql.*;

/**
 * 数据库操作工具类
 */
public class DatabaseUtil {

	//public static String URL = "jdbc:mysql://127.0.0.1:3306/clockin?useSSL=false&serverTimezone=UTC";
	public static String URL = "jdbc:mysql://localhost:3306/clockin?useSSL=false&serverTimezone=UTC";
	public static String USERNAME = "root";
	public static String PASSWORD = "7dgjdsj";
	public static String DRIVER = "com.mysql.cj.jdbc.Driver";
	//private static ResourceBundle rb = ResourceBundle.getBundle("util.db.db-config");

	private DatabaseUtil() {
	}

	static {
		/*URL = rb.getString("jdbc.url");
		USERNAME = rb.getString("jdbc.username");
		PASSWORD = rb.getString("jdbc.password");
		DRIVER = rb.getString("jdbc.driver");*/

		try {
			Class.forName(DRIVER);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取数据库连接的方法
	 * 
	 * @return
	 */
	public static Connection getConnection() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Get Database Connection failed.");
		}

		return conn;
	}

	/**
	 * 关闭数据库连接
	 * 
	 */
	public static void close(ResultSet rs, Statement stat, Connection conn) {
		
		try {
			if (rs != null)
				rs.close();
			if (stat != null)
				stat.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
