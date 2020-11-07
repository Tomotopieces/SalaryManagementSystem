package io.tomoto.service;

import java.sql.SQLException;

/**
 * Service class interface.
 *
 * @author Tomoto
 * <p>
 * 2020/11/4 17:55
 */
public interface Service extends AutoCloseable {
    /**
     * Closes DAOs.
     */
    @Override
    void close() throws SQLException;
}
