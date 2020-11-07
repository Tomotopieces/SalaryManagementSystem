package io.tomoto.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Util class for database.
 * @author Tomoto
 * <p>
 * 2020/11/1 21:56
 */
public class DatabaseUtil {
    private static final Logger logger = LogManager.getLogger(DatabaseUtil.class);

    /**
     * Gets a connection from the properties specified in the  file database.properties.
     *
     * @return the database connection
     */
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream in = DatabaseUtil.class.getResourceAsStream("/database.properties")) {
            props.load(in);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }

        String drivers = props.getProperty("jdbc.drivers");
        if (drivers != null) {
            System.setProperty("jdbc.drivers", drivers);
        } else {
            logger.warn("Can not read drivers");
        }
        String url = props.getProperty("jdbc.url");
        String username = props.getProperty("jdbc.username");
        String password = props.getProperty("jdbc.password");

        return DriverManager.getConnection(url, username, password);
    }
}
