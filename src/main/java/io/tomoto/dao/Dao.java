package io.tomoto.dao;


import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import static io.tomoto.util.DaoUtil.setStatementParameter;

/**
 * DAO class interface.
 *
 * @author Tomoto
 * <p>
 * 2020/11/4 14:11
 */
public interface Dao<T, ID> extends AutoCloseable {
    /**
     * Creates a new entity.
     *
     * @param t new entity
     * @return false if the entity with this id is already exists.
     */
    Boolean create(T t);

    /**
     * Deletes an entity.
     *
     * @param id the entity id for delete
     * @return false if the entity with this id is not exists.
     */
    default Boolean delete(ID id) {
        if (read(id) == null) {
            getLogger().warn("No employee with id: " + id);
            return false;
        }
        String command = "DELETE FROM `employee` WHERE `id` = " + id + ";";
        try (Statement statement = getConnection().createStatement()) {
            if (statement.executeUpdate(command) == 0) { // if no data was deleted for some reason
                getLogger().warn("Command executed but no data was deleted: " + command);
                return false;
            } else {
                getLogger().debug("Command executed: " + command);
            }
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Reads an entity.
     *
     * @param id the entity id for read
     * @return the entity or null if not exists
     */
    default T read(ID id) {
        String command = "SELECT * FROM `" + getTableName() + "` WHERE `id` = " + id + ";";
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(command)) {
            getLogger().debug("Command executed: " + command);
            if (resultSet.next()) {
                return readFirst(resultSet);
            }
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Reads all entities.
     *
     * @return a set of entities.
     */
    default Set<T> readAll() {
        HashSet<T> result = new HashSet<>();
        String command = "SELECT * FROM `" + getTableName() + "`;";
        try (Statement statement = getConnection().createStatement();
             ResultSet resultSet = statement.executeQuery(command)) {
            getLogger().debug("Command executed: " + command);
            while (resultSet.next()) {
                result.add(readFirst(resultSet));
            }
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Reads the first entity in result set.
     * <p>
     * For simplify the code of 'read(ID) : T' and 'readAll() : Set&lt;T&gt;' method
     *
     * @param resultSet the result set
     * @return the first entity
     */
    T readFirst(ResultSet resultSet) throws SQLException;

    /**
     * Updates an entity.
     *
     * @param t new entity
     * @return whether the update successful or not
     */
    Boolean update(T t);

    /**
     * Updates the property of an entity.
     *
     * @param operatorId   the operator id
     * @param entityId     an entity id
     * @param propertyName an property name
     * @param property     new property value
     * @param <R>          property type
     * @return whether the update successful or not
     */
    default <R> Boolean update(Integer operatorId, Integer entityId, String propertyName, R property) {
        String command = "UPDATE `" + getTableName() + "` SET `" +
                propertyName + "` = ?, `updateOperatorId` = ? WHERE id = ?;";
        try (PreparedStatement statement = getConnection().prepareStatement(command)) {
            if (!setStatementParameter(statement, 1, property)) {
                return false;
            }
            statement.setInt(2, operatorId);
            statement.setInt(3, entityId);
            if (statement.executeUpdate() == 0) {
                getLogger().warn("Failed to update.");
                return false;
            } else {
                getLogger().debug("Command executed: " + command);
            }
        } catch (SQLException e) {
            getLogger().error(e.getMessage());
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Gets the table name.
     *
     * @return the table name
     */
    String getTableName();

    /**
     * Gets the logger.
     *
     * @return the logger
     */
    Logger getLogger();

    /**
     * Gets the connection.
     *
     * @return the connection
     */
    Connection getConnection();

    /**
     * Close the database connection automatically.
     */
    @Override
    void close() throws SQLException;
}
