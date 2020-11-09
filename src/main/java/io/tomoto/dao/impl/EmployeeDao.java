package io.tomoto.dao.impl;

import io.tomoto.dao.Dao;
import io.tomoto.dao.entity.Employee;
import io.tomoto.util.DatabaseUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DAO class for Employee entities.
 * <p>
 * Singleton.
 *
 * @author Tomoto
 * <p>
 * 2020/11/4 14:22
 */
public class EmployeeDao implements Dao<Employee, Integer> {
    private static final Logger logger = LogManager.getLogger(EmployeeDao.class);

    private static final String TABLE_NAME = "employee";

    private Connection connection;

    private EmployeeDao() {
        try {
            connection = DatabaseUtil.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    public static EmployeeDao getInstance() {
        return Instance.INSTANCE;
    }

    @Override
    public Boolean create(Employee employee) {
        String command = "INSERT INTO `employee` " +
                "(`no`, `account`, `admin`, `password`," +
                " `name`, `idNo`, `phone`, `email`, `gender`, `birthday`," +
                " `createOperatorId`, `updateOperatorId`) " +
                "VALUES " +
                "(?, ?, ?, ?," +
                " ?, ?, ?, ?, ?, ?," +
                " ?, ?);";
        try (PreparedStatement statement = connection.prepareStatement(command)) {
            // account information
            statement.setString(1, employee.getNo());
            statement.setString(2, employee.getAccount());
            statement.setBoolean(3, employee.getAdmin());
            statement.setString(4, employee.getPassword());
            // personal information
            statement.setString(5, employee.getName());
            statement.setString(6, employee.getIdNo());
            statement.setString(7, employee.getPhone());
            statement.setString(8, employee.getEmail());
            statement.setString(9, employee.getGender());
            statement.setTimestamp(10, employee.getBirthday());
            // operator information
            statement.setInt(11, employee.getCreateOperatorId());
            statement.setInt(12, employee.getUpdateOperatorId());

            if (statement.executeUpdate() == 0) {
                logger.warn("Command executed but no data was created: " + command);
                return false;
            } else {
                logger.debug("Command executed: " + command);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Reads an employee by info.
     *
     * @param info a No or an account
     * @return the employee or null if not exists
     */
    public Employee read(String info) {
        String command;
        if (info.length() == 6) { // account
            command = "SELECT * FROM `employee` WHERE `account` = '" + info + "';";
        } else { // No
            command = "SELECT * FROM `employee` WHERE `no` = '" + info + "';";
        }
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(command)) {
            logger.debug("Command executed: " + command);
            if (resultSet.next()) {
                return readFirst(resultSet);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Employee readFirst(ResultSet resultSet) throws SQLException {
        return new Employee(
                resultSet.getInt("id"), resultSet.getString("no"), resultSet.getString("account"),
                resultSet.getBoolean("admin"), resultSet.getString("password"), resultSet.getString("name"),
                resultSet.getString("idNo"), resultSet.getString("phone"), resultSet.getString("email"),
                resultSet.getString("gender"), resultSet.getTimestamp("birthday"), resultSet.getTimestamp("createTime"),
                resultSet.getTimestamp("updateTime"), resultSet.getInt("createOperatorId"),
                resultSet.getInt("updateOperatorId"));
    }

    @Override
    @Deprecated
    public Boolean update(Employee employee) {
        if (read(employee.getId()) == null) {
            logger.warn("No employee with id: " + employee.getId());
            return false;
        }
        String command = "UPDATE `employee` SET " +
                "`no` = ?, `account` = ?, `admin` = ?, `password` = ?," +
                " `name` = ?, `idNo` = ?, `phone` = ?, `email` = ?, `gender` = ?, `birthday` = ?," +
                " `updateTime` = ?, `updateOperatorId` = ?" +
                " WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(command)) {
            // account information
            statement.setString(1, employee.getNo());
            statement.setString(2, employee.getAccount());
            statement.setBoolean(3, employee.getAdmin());
            statement.setString(4, employee.getPassword());
            // personal information
            statement.setString(5, employee.getName());
            statement.setString(6, employee.getIdNo());
            statement.setString(7, employee.getPhone());
            statement.setString(8, employee.getEmail());
            statement.setString(9, employee.getGender());
            statement.setTimestamp(10, employee.getBirthday());
            // operator information
            statement.setTimestamp(11, employee.getUpdateTime());
            statement.setInt(12, employee.getUpdateOperatorId());
            // where
            statement.setInt(13, employee.getId());

            int i = statement.executeUpdate();
            if (i == 0) { // if no data was updated for some reason
                logger.warn("Command executed but no data was updated: " + command);
                return false;
            } else {
                logger.debug("Command executed: " + command);
            }
        } catch (SQLException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
            return false;
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
        logger.info("io.tomoto.dao.impl.EmployeeDao.connection closed");
    }

    /**
     * Singleton instance class.
     */
    private static class Instance {
        public static final EmployeeDao INSTANCE = new EmployeeDao();
    }
}
