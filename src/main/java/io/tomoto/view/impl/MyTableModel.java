package io.tomoto.view.impl;

import javax.swing.table.DefaultTableModel;

/**
 * TableModel with specific column names and not editable.
 *
 * @author Tomoto
 * <p>
 * 2020/11/9 17:02
 */
class MyTableModel extends DefaultTableModel {
    private final Object[] columnNames;

    public MyTableModel(Object[][] data, Object[] columnNames) {
        super(data, columnNames);
        this.columnNames = columnNames;
    }

    @Override
    public String getColumnName(int column) {
        return columnNames[column].toString();
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }
}
