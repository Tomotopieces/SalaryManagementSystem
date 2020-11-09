package io.tomoto.view;

import javax.swing.*;

/**
 * Sub frame of main frame.
 *
 * @author Tomoto
 * <p>
 * 2020/11/6 14:29
 */
public interface SubView extends View {
    /**
     * Sub view does not use resources, close nothing.
     */
    @Override
    default void close() {
    }

    /**
     * Checks empty field.
     *
     * @param fields fields for check
     * @return true if no empty filed
     */
    default Boolean noEmptyFields(JTextField... fields) {
        for (JTextField field : fields) {
            if (field.getText().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clear all text fields.
     */
    void clear();
}
