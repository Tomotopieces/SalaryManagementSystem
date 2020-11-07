package io.tomoto.view.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.service.impl.AdminService;
import io.tomoto.view.View;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static io.tomoto.util.DateUtil.DATE_FORMAT;

/**
 * Administrator main frame.
 *
 * @author Tomoto
 * <p>
 * 2020/11/5 19:50
 */
public class AdminView extends JFrame implements View {
    private static final Integer DEFAULT_WIDTH = 480;
    private static final Integer DEFAULT_HEIGHT = 320;
    private static final Integer DEFAULT_WIDTH_2 = 1200;
    private static final Integer DEFAULT_HEIGHT_2 = 320;
    private static final Integer DEFAULT_WIDTH_3 = 1260;
    private static final Integer DEFAULT_HEIGHT_3 = 320;

    private static final String[] EMPLOYEE_TABLE_COLUMN_NAMES = {
            "id", "工号", "账户", "管理员", "密码",
            "姓名", "身份证", "手机号", "邮箱", "性别", "生日",
            "创建者id", "创建时间", "更新者id", "更新时间"
    };
    private static final String[] SALARY_TABLE_COLUMN_NAMES = {
            "id", "员工id",
            "基本工资", "岗位工资", "工龄工资", "通讯补贴", "交通补贴",
            "个税代缴", "社保代缴", "住房公积金",
            "实际工资", "工资月份",
            "创建者id", "创建时间", "更新者id", "更新时间"
    };

    private final Integer id;
    private final AdminService service;
    private final CreateEmployeeView createEmployeeView = new CreateEmployeeView(this);
    private final CreateSalaryView createSalaryView = new CreateSalaryView(this);

    private final JTabbedPane mainPane = new JTabbedPane();
    private final JScrollPane employeeTableScrollPane;
    private final JTable employeeTable;
    private final JScrollPane salaryTableScrollPane;
    private final JTable salaryTable;
    private Set<Employee> employeeSet = new HashSet<>();
    private Set<Salary> salarySet = new HashSet<>();

    public AdminView(Integer id) {
        // initialize frame information
        this.id = id;
        service = AdminService.getInstance(this);
        assert service != null;
        Employee administrator = service.readEmployee(id);
        setTitle("欢迎，" + administrator.getName() + "。");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWindowsLookAndFeel();

        // this tab show administrator's information, allow administrator to reset password
        JPanel infoTabPanel = new JPanel(new GridLayout(5, 2));
        // show personal information
        infoTabPanel.add(new JLabel("工号：" + administrator.getNo()));
        infoTabPanel.add(new JLabel("账号：" + administrator.getAccount()));
        infoTabPanel.add(new JLabel("管理员：" + (administrator.getAdmin() ? "是" : "否")));
        infoTabPanel.add(new JLabel("姓名：" + administrator.getName()));
        infoTabPanel.add(new JLabel("性别：" + administrator.getGender()));
        infoTabPanel.add(new JLabel("手机号：" + administrator.getPhone()));
        infoTabPanel.add(new JLabel("邮箱：" + administrator.getEmail()));
        infoTabPanel.add(new JLabel("身份证：" + administrator.getIdNo()));
        infoTabPanel.add(new JLabel("生日：" + new SimpleDateFormat("yyyy-MM-dd").format(administrator.getBirthday())));
        mainPane.addTab("个人信息", infoTabPanel);

        // this tab allow administrator to manage employees
        JPanel employeeManagementTabPanel = new JPanel();
        // generate CRUD panel
        JPanel employeeCrudPanel = new JPanel();
        JLabel employeeReadLabel = new JLabel("工号/姓名/留空：");
        JTextField employeeReadInputField = new JTextField(16); // input employee name or No
        JButton employeeReadButton = new JButton("查询");
        JButton employeeCreateButton = new JButton("创建");
        JButton employeeDeleteButton = new JButton("删除");
        JButton employeeUpdateButton = new JButton("更新");
        employeeCrudPanel.add(employeeReadLabel);
        employeeCrudPanel.add(employeeReadInputField);
        employeeCrudPanel.add(employeeReadButton);
        employeeCrudPanel.add(employeeCreateButton);
        employeeCrudPanel.add(employeeDeleteButton);
        employeeCrudPanel.add(employeeUpdateButton);
        employeeManagementTabPanel.add(employeeCrudPanel);
        // generate employee management table
        employeeTable = new JTable(new MyTableModel(employeesToArray(), EMPLOYEE_TABLE_COLUMN_NAMES));
        employeeTable.setPreferredScrollableViewportSize(employeeTable.getPreferredSize());
        employeeTableScrollPane = new JScrollPane(employeeTable);
        employeeTableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Result"));
        employeeManagementTabPanel.add(employeeTableScrollPane);
        mainPane.addTab("员工管理", employeeManagementTabPanel);

        // this tab allow administrator to manage salaries
        JPanel salaryManagementTabPanel = new JPanel();
        // generate CRUD panel
        JPanel salaryCrudPanel = new JPanel();
        JLabel salaryReadLabel1 = new JLabel("工号：");
        JTextField salaryReadInfoInputField = new JTextField(16); // input employee name or No
        JLabel salaryReadLabel2 = new JLabel("月份：");
        JTextField salaryReadMonthInputField1 = new JTextField(16); // input month 'from'
        JLabel salaryReadLabel3 = new JLabel(" - ");
        JTextField salaryReadMonthInputField2 = new JTextField(16); // input month 'to'
        JButton salaryReadButton = new JButton("查询");
        JButton salaryCreateButton = new JButton("创建");
        JButton salaryUpdateButton = new JButton("更新");
        salaryCrudPanel.add(salaryReadLabel1);
        salaryCrudPanel.add(salaryReadInfoInputField);
        salaryCrudPanel.add(salaryReadLabel2);
        salaryCrudPanel.add(salaryReadMonthInputField1);
        salaryCrudPanel.add(salaryReadLabel3);
        salaryCrudPanel.add(salaryReadMonthInputField2);
        salaryCrudPanel.add(salaryReadButton);
        salaryCrudPanel.add(salaryCreateButton);
        salaryCrudPanel.add(salaryUpdateButton);
        salaryManagementTabPanel.add(salaryCrudPanel);
        //generate salary management table
        salaryTable = new JTable(new MyTableModel(salaryToArray(), SALARY_TABLE_COLUMN_NAMES));
        salaryTable.setPreferredScrollableViewportSize(salaryTable.getPreferredSize());
        salaryTableScrollPane = new JScrollPane(salaryTable);
        salaryTableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Result"));
        salaryManagementTabPanel.add(salaryTableScrollPane);
        mainPane.addTab("工资管理", salaryManagementTabPanel);

        // add button action listeners

        employeeReadButton.addActionListener(event -> {
            String info = employeeReadInputField.getText();
            if (!info.isEmpty()) {
                employeeSet = service.readEmployee(info);
            } else {
                employeeSet = service.readAllEmployees();
            }
            updateTable(employeeTable, employeesToArray(), EMPLOYEE_TABLE_COLUMN_NAMES, employeeTableScrollPane);
            pack();
        });

        salaryReadButton.addActionListener(event -> {
            String info = salaryReadInfoInputField.getText();
            String from = salaryReadMonthInputField1.getText();
            String to = salaryReadMonthInputField2.getText();
            if (!info.isEmpty()) {
                if (!from.isEmpty()) {
                    if (!to.isEmpty()) {
                        salarySet = service.readSalary(info, from, to);
                    } else {
                        salarySet = service.readSalary(info, from);
                    }
                } else {
                    salarySet = service.readSalary(info);
                }
            } else {
                salarySet = service.readAllSalaries();
            }
            updateTable(salaryTable, salaryToArray(), SALARY_TABLE_COLUMN_NAMES, salaryTableScrollPane);
            pack();
        });

        employeeCreateButton.addActionListener(event ->
                EventQueue.invokeLater(() -> {
                    createEmployeeView.clear();
                    createEmployeeView.setVisible(true);
                }));

        salaryCreateButton.addActionListener(event ->
                EventQueue.invokeLater(() -> {
                    createSalaryView.clear();
                    createSalaryView.setVisible(true);
                }));

        employeeDeleteButton.addActionListener(event -> {
            int row = employeeTable.getSelectedRow();
            if (row == -1) {
                return;
            }
            service.deleteEmployee((Integer) employeeTable.getValueAt(row, 0));
        });

        employeeUpdateButton.addActionListener(event -> {
            int row = employeeTable.getSelectedRow();
            int column = employeeTable.getSelectedColumn();
            if (row == -1 || column == -1) {
                return;
            }
            if (column > 10 || column == 0) {
                showHint("此项数据无法编辑！");
                return;
            }
            String newValue = JOptionPane.showInputDialog(null, "输入新值", employeeTable.getValueAt(row, column));
            if (service.updateEmployee(
                    (Integer) employeeTable.getValueAt(row, 0), // id
                    (String) employeeTable.getValueAt(row, 1), // No
                    (String) employeeTable.getValueAt(row, 2), // account
                    (Boolean) employeeTable.getValueAt(row, 3), // admin
                    (String) employeeTable.getValueAt(row, 4), // password
                    (String) employeeTable.getValueAt(row, 5), // name
                    (String) employeeTable.getValueAt(row, 6), // idNo
                    (String) employeeTable.getValueAt(row, 7), // phone
                    (String) employeeTable.getValueAt(row, 8), // email
                    (String) employeeTable.getValueAt(row, 9), // gender
                    employeeTable.getValueAt(row, 10) + " 00:00:00")) { // birthday
                employeeTable.setValueAt(newValue, row, column);
            }
        });

        salaryUpdateButton.addActionListener(event -> {
            int row = salaryTable.getSelectedRow();
            int column = salaryTable.getSelectedColumn();
            if (row == -1 || column == -1) {
                return;
            }
            if (column > 10 || column == 0) {
                showHint("此项数据无法编辑！");
                return;
            }
            String newValue = JOptionPane.showInputDialog(null, "输入新值", salaryTable.getValueAt(row, column));
            if (service.updateSalary(
                    (Integer) salaryTable.getValueAt(row, 0), // id
                    (Integer) salaryTable.getValueAt(row, 1), // employee id
                    (Double) salaryTable.getValueAt(row, 2), // basic salary
                    (Double) salaryTable.getValueAt(row, 2), // post salary
                    (Double) salaryTable.getValueAt(row, 2), // length of service salary
                    (Double) salaryTable.getValueAt(row, 2), // communication subsidy
                    (Double) salaryTable.getValueAt(row, 2), // transportation subsidy
                    (Double) salaryTable.getValueAt(row, 2), // personal income tax
                    (Double) salaryTable.getValueAt(row, 2), // social security
                    (Double) salaryTable.getValueAt(row, 2), // housing fund
                    (Double) salaryTable.getValueAt(row, 2), // actually salary
                    (String) salaryTable.getValueAt(row, 2))) { // month
                employeeTable.setValueAt(newValue, row, column);
            }
        });

        // set tab listener, reset frame size
        mainPane.addChangeListener(e -> {
            int index = mainPane.getSelectedIndex();
            if (index == 0) {
                setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            } else if (index == 1) {
                setSize(DEFAULT_WIDTH_2, DEFAULT_HEIGHT_2);
            } else {
                setSize(DEFAULT_WIDTH_3, DEFAULT_HEIGHT_3);
            }
        });

        // set font
        setComponentsFont(mainPane, VIEW_FONT);

        add(mainPane);
        pack();
    }

    private static void updateTable(JTable table, Object[][] data, String[] columnNames, JScrollPane scrollPane) {
        // Java Error : javax.swing.JTable$1 cannot be cast to javax.swing.table.DefaultTableModel
        // https://stackoverflow.com/a/34174372/12348320

        // add new rows
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        model.setDataVector(data, columnNames);
        model.fireTableDataChanged();
        table.setPreferredScrollableViewportSize(table.getPreferredSize());
        table.repaint();
        scrollPane.revalidate();
    }

    private Object[][] employeesToArray() {
        Object[][] objects = new Object[employeeSet.size()][]; // rows
        Iterator<Employee> iterator = employeeSet.iterator();
        for (int i = 0; i < employeeSet.size(); i++) {
            Employee next = iterator.next();
            objects[i] = new Object[15]; // columns
            Object[] values = objects[i];
            values[0] = next.getId();
            values[1] = next.getNo();
            values[2] = next.getAccount();
            values[3] = next.getAdmin();
            values[4] = next.getPassword();
            values[5] = next.getName();
            values[6] = next.getIdNo();
            values[7] = next.getPhone();
            values[8] = next.getEmail();
            values[9] = next.getGender();
            values[10] = DATE_FORMAT.format(next.getBirthday());
            values[11] = next.getCreateOperatorId();
            values[12] = next.getCreateTime();
            values[13] = next.getUpdateOperatorId();
            values[14] = next.getUpdateTime();
        }
        return objects;
    }

    private Object[][] salaryToArray() {
        Object[][] objects = new Object[salarySet.size()][];
        Iterator<Salary> iterator = salarySet.iterator();
        for (int i = 0; i < salarySet.size(); i++) {
            Salary next = iterator.next();
            objects[i] = new Object[16];
            Object[] values = objects[i];
            values[0] = next.getId();
            values[1] = next.getEmployeeId();
            values[2] = next.getBase();
            values[3] = next.getPost();
            values[4] = next.getLength();
            values[5] = next.getPhone();
            values[6] = next.getTraffic();
            values[7] = next.getTax();
            values[8] = next.getSecurity();
            values[9] = next.getFund();
            values[10] = next.getActually();
            values[11] = next.getMonth();
            values[12] = next.getCreateOperatorId();
            values[13] = next.getCreateTime();
            values[14] = next.getUpdateOperatorId();
            values[15] = next.getUpdateTime();
        }
        return objects;
    }

    @Override
    public Dimension getPreferredSize() {
        int index = mainPane.getSelectedIndex();
        switch (index) {
            default:
            case 0:
                return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            case 1:
                return new Dimension(DEFAULT_WIDTH_2, DEFAULT_HEIGHT_2);
            case 2:
                return new Dimension(DEFAULT_WIDTH_3, DEFAULT_HEIGHT_3);
        }
    }

    @Override
    public void close() throws SQLException {
        service.close();
    }

    public Integer getId() {
        return id;
    }

    public AdminService getService() {
        return service;
    }

    /**
     * TableModel with specific column names and not editable.
     */
    private static class MyTableModel extends DefaultTableModel {
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
}
