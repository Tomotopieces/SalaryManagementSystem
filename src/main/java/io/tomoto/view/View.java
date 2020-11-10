package io.tomoto.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.sql.SQLException;
import java.util.Set;

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
     * Set frame title, default close operation, size and 'look and view'.
     */
    void initFrameInfo();

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
     * Update the employee or salary display table.
     *
     * @param table       an table
     * @param data        new table data
     * @param columnNames the column names
     * @param scrollPane  the scrollPane
     */
    default void updateTable(JTable table, Object[][] data, String[] columnNames, JScrollPane scrollPane) {
        // Java Error : javax.swing.JTable$1 cannot be cast to javax.swing.table.DefaultTableModel
        // https://stackoverflow.com/a/34174372/12348320

        // add new rows
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setDataVector(data, columnNames);
        model.setRowCount(10);
//        model.fireTableDataChanged();
//        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        scrollPane.revalidate();
    }

    default Integer getSetMaxIndex(Set<?> set) {
        int size = set.size();
        return size % 10 == 0 ? size / 10 : size / 10 + 1;
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
