package io.tomoto.view;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * View class interface.
 *
 * @author Tomoto
 * <p>
 * 2020/11/5 10:29
 */
public interface View extends AutoCloseable {
    /**
     * Plain, size 12, MS Song.
     * <p>
     * Support Chinese characters.
     */
    Font VIEW_FONT = new Font("MS Song", Font.PLAIN, 12);

    /**
     * Sets windows look and feel.
     */
    default void setWindowsLookAndFeel() {
        try { // set windows look and feel
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException | InstantiationException |
                IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets all components font to the specific one.
     *
     * @param mainContainer the main container
     */
    default void setComponentsFont(Container mainContainer, Font font) {
        for (Component component : mainContainer.getComponents()) {
            component.setFont(font);
        }
    }

    /**
     * Pops up a message dialog with hint.
     *
     * @param hint the hint
     */
    default void showHint(String hint) {
        JOptionPane.showMessageDialog(null, hint);
    }

    /**
     * Closes service.
     */
    @Override
    void close() throws SQLException;
}
