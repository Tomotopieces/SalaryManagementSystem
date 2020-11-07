package io.tomoto.dao.impl;

import io.tomoto.dao.Dao;
import io.tomoto.dao.entity.Salary;
import io.tomoto.util.DatabaseUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * DAO class for salary entities.
 * <p>
 * Singleton.
 *
 * @author Tomoto
 * <p>
 * 2020/11/4 16:13
 */
public class SalaryDao implements Dao<Salary, Integer> {
    private static final Logger logger = LogManager.getLogger(Salary.class);

    private static final String TABLE_NAME = "salary";
    private Connection connection;

    private SalaryDao() {
        try {
            connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static SalaryDao getInstance() {
        return Instance.INSTANCE;
    }

    @Override
    public Boolean create(Salary salary) {
        String command = "INSERT INTO `" + TABLE_NAME + "` " +
                "(`employeeId`," +
                " `base`, `post`, `length`, `phone`, `traffic`," +
                " `tax`, `security`, `fund`," +
                " `actually`, `month`," +
                " `createOperatorId`, `updateOperatorId`) " +
                "VALUES " +
                "(?," +
                " ?, ?, ?, ?, ?," +
                " ?, ?, ?," +
                " ?, ?," +
                " ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(command)) {
            // salary part
            statement.setInt(1, salary.getEmployeeId());
            statement.setDouble(2, salary.getBase());
            statement.setDouble(3, salary.getPost());
            statement.setDouble(4, salary.getLength());
            statement.setDouble(5, salary.getPhone());
            statement.setDouble(6, salary.getTraffic());
            // cost
            statement.setDouble(7, salary.getTax());
            statement.setDouble(8, salary.getSecurity());
            statement.setDouble(9, salary.getFund());
            // actually salary
            statement.setDouble(10, salary.getActually());
            statement.setString(11, salary.getMonth());
            // operator information
            statement.setInt(12, salary.getCreateOperatorId());
            statement.setInt(13, salary.getUpdateOperatorId());

            if (statement.executeUpdate() == 0) {
                logger.warn("Command executed but no data was created: " + command);
                return false;
            } else {
                logger.info("Command executed: " + command);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public Salary readFirst(ResultSet resultSet) throws SQLException {
        return new Salary(
                resultSet.getInt("id"), resultSet.getInt("employeeId"),
                resultSet.getDouble("base"), resultSet.getDouble("post"), resultSet.getDouble("length"),
                resultSet.getDouble("phone"), resultSet.getDouble("traffic"), resultSet.getDouble("tax"),
                resultSet.getDouble("security"), resultSet.getDouble("fund"), resultSet.getDouble("actually"),
                resultSet.getString("month"), resultSet.getTimestamp("createTime"), resultSet.getTimestamp("updateTime"),
                resultSet.getInt("createOperatorId"),resultSet.getInt("updateOperatorId"));
    }

    @Override
    public Boolean update(Salary salary) {
        if (read(salary.getId()) == null) {
            logger.warn("No employee with id: " + salary.getId());
            return false;
        }
        String command = "UPDATE `" + TABLE_NAME + "` SET " +
                "`base` = ?, `post` = ?, `length` = ?, `phone` = ?, `traffic` = ?" +
                " `tax` = ?, `security` = ?, `fund` = ?," +
                " `actually` = ?, `month` = ?" +
                " `updateTime` = ?, `updateOperatorId` = ? " +
                "WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(command)) {
            // salary part
            statement.setDouble(1, salary.getBase());
            statement.setDouble(2, salary.getPost());
            statement.setDouble(3, salary.getLength());
            statement.setDouble(4, salary.getPhone());
            statement.setDouble(5, salary.getTraffic());
            // cost
            statement.setDouble(6, salary.getTax());
            statement.setDouble(7, salary.getSecurity());
            statement.setDouble(8, salary.getFund());
            // actually salary
            statement.setDouble(9, salary.getActually());
            statement.setString(10, salary.getMonth());
            // operator information
            statement.setTimestamp(11, salary.getUpdateTime());
            statement.setInt(12, salary.getUpdateOperatorId());
            // where
            statement.setInt(13, salary.getId());

            if (statement.executeUpdate() == 0) { // if no data was updated for some reason
                logger.warn("Command executed but no data was updated: " + command);
                return false;
            } else {
                logger.info("Command executed: " + command);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public Connection getConnection() {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        connection.close();
        logger.info("io.tomoto.dao.impl.SalaryDao.connection closed.");
    }

    /**
     * Singleton instance class.
     */
    private static class Instance {
        public static final SalaryDao INSTANCE = new SalaryDao();
    }
}
