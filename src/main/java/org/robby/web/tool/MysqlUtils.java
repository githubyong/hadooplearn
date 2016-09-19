package org.robby.web.tool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by yong on 2016/9/18.
 */
public class MysqlUtils {

    public static Connection getConnection(String url,String driver, String username, String password) throws Exception {
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
