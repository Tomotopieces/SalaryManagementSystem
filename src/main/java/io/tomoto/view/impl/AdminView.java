package io.tomoto.view.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.service.impl.AdminService;
import io.tomoto.view.HintTextField;
import io.tomoto.view.View;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
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
    private static final Integer DEFAULT_WIDTH = 480; // for personal info tab
    private static final Integer DEFAULT_HEIGHT = 320;
    private static final Integer DEFAULT_WIDTH_2 = 1200; // for employee management tab
    private static final Integer DEFAULT_HEIGHT_2 = 640;
    private static final Integer DEFAULT_WIDTH_3 = 1360; // for salary management tab
    private static final Integer DEFAULT_HEIGHT_3 = 640;
    private static final String[] EMPLOYEE_TABLE_COLUMN_NAMES_CHINESE = {
            "id", "工号", "账户", "管理员", "密码",
            "姓名", "身份证", "手机号", "邮箱", "性别", "生日",
            "创建者", "创建时间", "更新者", "更新时间"
    };
    private static final String[] EMPLOYEE_TABLE_COLUMN_NAMES_ORIGIN = {
            "id", "no", "account", "admin", "password",
            "name", "idNo", "phone", "email", "gender", "birthday",
            "createOperatorId", "createTime", "updateOperatorId", "updateTime"
    };
    private static final String[] SALARY_TABLE_COLUMN_NAMES_CHINESE = {
            "id", "员工id",
            "基本工资", "岗位工资", "工龄工资", "通讯补贴", "交通补贴",
            "个税代缴", "社保代缴", "住房公积金",
            "实际工资", "到手工资", "工资月份",
            "创建者", "创建时间", "更新者", "更新时间"
    };
    private static final String[] SALARY_TABLE_COLUMN_NAMES_ORIGIN = {
            "id", "employeeId",
            "base", "post", "length", "phone", "traffic",
            "tax", "security", "fund",
            "actually", "fin", "month",
            "createOperatorId", "createTime", "updateOperatorId", "updateTime"
    };

    private final Integer id; // administrator id
    private final AdminService service;
    private final CreateEmployeeView createEmployeeView = new CreateEmployeeView(this);
    private final CreateSalaryView createSalaryView = new CreateSalaryView(this);

    private final JTabbedPane mainPane = new JTabbedPane();
    private JScrollPane employeeTableScrollPane;
    private JTable employeeTable;
    private JScrollPane salaryTableScrollPane;
    private JTable salaryTable;
    private Set<Employee> employeeSet = new HashSet<>();
    private Set<Salary> salarySet = new HashSet<>();

    public AdminView(Integer id) {
        // initialize frame fields
        this.id = id;
        service = AdminService.getInstance(this);
        initFrameInfo(); // initialize frame information
        assert service != null;
        // add tabs
        mainPane.addTab("个人信息", generatePersonalInfoTab(service.readEmployee(id)));
        mainPane.addTab("员工管理", generateEmployeeManagementTab());
        mainPane.addTab("工资管理", generateSalaryManagementTab());
        // set main pane
        mainPane.addChangeListener(e -> setSize(getPreferredSize()));
        setComponentsFont(mainPane, VIEW_FONT);

        add(mainPane);
        pack();
    }

    @Override
    public void initFrameInfo() {
        setTitle("欢迎，" + service.readEmployee(id).getName() + "。");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(getPreferredSize());
        setResizable(false);
        setWindowsLookAndFeel();
    }

    /**
     * This tab show admin information.
     *
     * @param admin the admin
     * @return the panel
     */
    private JPanel generatePersonalInfoTab(Employee admin) {
        JPanel infoTabPanel = new JPanel(new GridLayout(5, 2));
        // show personal information
        infoTabPanel.add(new JLabel("工号：" + admin.getNo()));
        infoTabPanel.add(new JLabel("账号：" + admin.getAccount()));
        infoTabPanel.add(new JLabel("管理员：" + (admin.getAdmin() ? "是" : "否")));
        infoTabPanel.add(new JLabel("姓名：" + admin.getName()));
        infoTabPanel.add(new JLabel("性别：" + admin.getGender()));
        infoTabPanel.add(new JLabel("手机号：" + admin.getPhone()));
        infoTabPanel.add(new JLabel("邮箱：" + admin.getEmail()));
        infoTabPanel.add(new JLabel("身份证：" + admin.getIdNo()));
        infoTabPanel.add(new JLabel("生日：" + new SimpleDateFormat("yyyy-MM-dd").format(admin.getBirthday())));
        return infoTabPanel;
    }

    /**
     * This tab allow administrator to manage employees.
     *
     * @return the panel
     */
    private JPanel generateEmployeeManagementTab() {
        JPanel employeeManagementTabPanel = new JPanel();
        // generate CRUD panel
        JPanel employeeCrudPanel = new JPanel();
        JLabel employeeReadLabel = new JLabel("员工：");
        JTextField employeeReadInputField = new HintTextField(16, "工号/姓名/留空"); // input employee name or No
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
        employeeTable = new JTable(new MyTableModel(employeesToArray(), EMPLOYEE_TABLE_COLUMN_NAMES_CHINESE));
        employeeTable.setPreferredScrollableViewportSize(employeeTable.getPreferredSize());
        employeeTableScrollPane = new JScrollPane(employeeTable);
        employeeTableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Result"));
        employeeManagementTabPanel.add(employeeTableScrollPane);
        // add button listeners
        employeeReadButton.addActionListener(event -> {
            String info = employeeReadInputField.getText();
            if (!info.isEmpty()) { // match read mode
                employeeSet = service.readEmployee(info); // read by employee No or name
            } else {
                employeeSet = service.readAllEmployees(); // read all employees when the info is empty
            }
            updateTable(employeeTable, employeesToArray(), EMPLOYEE_TABLE_COLUMN_NAMES_CHINESE, employeeTableScrollPane);
            pack();
        });
        employeeCreateButton.addActionListener(event ->
                EventQueue.invokeLater(() -> {
                    createEmployeeView.clear();
                    createEmployeeView.setVisible(true);
                }));
        employeeDeleteButton.addActionListener(event -> {
            int row = employeeTable.getSelectedRow();
            if (row == -1) {
                return;
            }
            if (service.deleteEmployee((Integer) employeeTable.getValueAt(row, 0))) {
                // what to do?
            }
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
                    (Integer) employeeTable.getValueAt(row, 0),
                    EMPLOYEE_TABLE_COLUMN_NAMES_ORIGIN[column],
                    newValue)) {
                employeeTable.setValueAt(newValue, row, column);
            }
        });
        return employeeManagementTabPanel;
    }

    private JPanel generateSalaryManagementTab() {
        // this tab allow administrator to manage salaries
        JPanel salaryManagementTabPanel = new JPanel();
        // generate CRUD panel
        JPanel salaryCrudPanel = new JPanel();
        JLabel salaryReadLabel1 = new JLabel("员工：");
        JTextField salaryReadInfoInputField = new HintTextField(16, "工号/姓名/留空"); // input employee name or No
        JLabel salaryReadLabel2 = new JLabel("月份：");
        JTextField salaryReadMonthInputField1 = new HintTextField(25, "起始月份(yyyyMM)/留空"); // input month 'from'
        JLabel salaryReadLabel3 = new JLabel(" - ");
        JTextField salaryReadMonthInputField2 = new HintTextField(25, "结束月份(yyyyMM)/留空"); // input month 'to'
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
        salaryTable = new JTable(new MyTableModel(salaryToArray(), SALARY_TABLE_COLUMN_NAMES_CHINESE));
        salaryTable.setPreferredScrollableViewportSize(salaryTable.getPreferredSize());
        salaryTableScrollPane = new JScrollPane(salaryTable);
        salaryTableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Result"));
        salaryManagementTabPanel.add(salaryTableScrollPane);
        // add button listeners
        salaryReadButton.addActionListener(event -> {
            String info = salaryReadInfoInputField.getText();
            String from = salaryReadMonthInputField1.getText();
            String to = salaryReadMonthInputField2.getText();
            // match 'read' pattern
            Set<Salary> result = service.readSalary(info, from, to);
            if (result != null) {
                salarySet = result;
                updateTable(salaryTable, salaryToArray(), SALARY_TABLE_COLUMN_NAMES_CHINESE, salaryTableScrollPane);
                pack();
            }
        });
        salaryCreateButton.addActionListener(event ->
                EventQueue.invokeLater(() -> {
                    createSalaryView.clear();
                    createSalaryView.setVisible(true);
                }));
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
                    (Integer) salaryTable.getValueAt(row, 0),
                    SALARY_TABLE_COLUMN_NAMES_ORIGIN[column],
                    newValue)) {
                salaryTable.setValueAt(newValue, row, column);
            }
        });
        return salaryManagementTabPanel;
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
            values[11] = service.readEmployee(next.getCreateOperatorId()).getName();
            values[12] = next.getCreateTime();
            values[13] = service.readEmployee(next.getUpdateOperatorId()).getName();
            values[14] = next.getUpdateTime();
        }
        return objects;
    }

    private Object[][] salaryToArray() {
        Object[][] objects = new Object[salarySet.size()][];
        Iterator<Salary> iterator = salarySet.iterator();
        for (int i = 0; i < salarySet.size(); i++) {
            Salary next = iterator.next();
            objects[i] = new Object[17];
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
            values[11] = next.getFin();
            values[12] = next.getMonth();
            values[13] = service.readEmployee(next.getCreateOperatorId()).getName();
            values[14] = next.getCreateTime();
            values[15] = service.readEmployee(next.getUpdateOperatorId()).getName();
            values[16] = next.getUpdateTime();
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

    public Integer getUserId() {
        return id;
    }

    public AdminService getService() {
        return service;
    }
}
