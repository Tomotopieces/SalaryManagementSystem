package io.tomoto.util;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

/**
 * Util class for Dao classes.
 *
 * @author Tomoto
 * <p>
 * 2020/11/7 9:59
 */
public class DaoUtil {
    private static final Logger logger = LogManager.getLogger(DaoUtil.class);

    public static <T> Boolean setStatementParameter(PreparedStatement statement, Integer index, T value) throws SQLException {
        if (value instanceof Integer) {
            statement.setInt(index, (Integer) value);
        } else if (value instanceof Boolean) {
            statement.setBoolean(index, (Boolean) value);
        } else if (value instanceof Double) {
            statement.setDouble(index, (Double) value);
        } else if (value instanceof String) {
            statement.setString(index, (String) value);
        } else if (value instanceof Timestamp) {
            statement.setTimestamp(index, (Timestamp) value);
        } else {
            logger.error("Unexpected property type: " + value.getClass());
            return false;
        }
        return true;
    }
}
