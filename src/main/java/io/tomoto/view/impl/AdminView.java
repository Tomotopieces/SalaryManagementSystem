package io.tomoto.view.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.service.impl.AdminService;
import io.tomoto.view.HintTextField;
import io.tomoto.view.View;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static io.tomoto.util.DateUtil.DATE_FORMAT;
import static javax.swing.JOptionPane.YES_NO_OPTION;
import static javax.swing.JOptionPane.YES_OPTION;

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
    private static final Integer DEFAULT_HEIGHT_2 = 480;
    private static final Integer DEFAULT_WIDTH_3 = 1360; // for salary management tab
    private static final Integer DEFAULT_HEIGHT_3 = 480;
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
            "id", "员工工号",
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

    private JTextField employeeReadInputField;
    private JScrollPane employeeTableScrollPane;
    private JTable employeeTable;
//    private Set<Employee> employeeSet = new TreeSet<>(Comparator.comparing(Employee::getId));
    private Set<Employee> employeeSet;
    private HintTextField employeePageField;
    private Integer employeeTabCurrentIndex = 1;

    private JTextField salaryReadInfoInputField;
    private JTextField salaryReadMonthInputField1;
    private JTextField salaryReadMonthInputField2;
    private JScrollPane salaryTableScrollPane;
    private JTable salaryTable;
    private Set<Salary> salarySet;
    private HintTextField salaryPageField;
    private Integer salaryTabCurrentIndex = 1;

    private JFileChooser chooser;

    public AdminView(Integer id) {
        // initialize frame fields
        this.id = id;
        service = AdminService.getInstance(this);
        assert service != null;
        employeeSet = service.readAllEmployees();
        salarySet = service.readAllSalaries();
        chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("Excel files", "xls", "xlsx"));
        initFrameInfo(); // initialize frame information
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
        employeeReadInputField = new HintTextField(16, "工号/姓名/留空"); // input employee name or No
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
        //generate file IO panel
        JPanel fileIOPanel = new JPanel();
        JButton employeeImportButton = new JButton("导入");
        JButton employeeExportButton = new JButton("导出");
        fileIOPanel.add(employeeImportButton);
        fileIOPanel.add(employeeExportButton);
        employeeManagementTabPanel.add(fileIOPanel);
        // generate employee management table
        employeeTable = new JTable(new MyTableModel(employeesToArray(employeeTabCurrentIndex), EMPLOYEE_TABLE_COLUMN_NAMES_CHINESE));
        employeeTable.setAutoCreateRowSorter(true);
        employeeTable.setPreferredScrollableViewportSize(employeeTable.getPreferredSize());
        employeeTableScrollPane = new JScrollPane(employeeTable);
        employeeTableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                "Result"));
        employeeManagementTabPanel.add(employeeTableScrollPane);
        // generate page turn button panel
        JPanel turnPagePanel = new JPanel();
        JButton firstPageButton = new JButton("首页");
        firstPageButton.setEnabled(false);
        JButton previousPageButton = new JButton("上一页");
        previousPageButton.setEnabled(false);
        employeePageField = new HintTextField(4, employeeTabCurrentIndex + "/" + getSetMaxIndex(employeeSet));
        JButton jumpPageButton = new JButton("翻至该页");
        JButton nextPageButton = new JButton("下一页");
        JButton lastPageButton = new JButton("尾页");
        if (getSetMaxIndex(employeeSet) == 1) {
            nextPageButton.setEnabled(false);
            lastPageButton.setEnabled(false);
        }
        turnPagePanel.add(firstPageButton);
        turnPagePanel.add(previousPageButton);
        turnPagePanel.add(employeePageField);
        turnPagePanel.add(jumpPageButton);
        turnPagePanel.add(nextPageButton);
        turnPagePanel.add(lastPageButton);
        employeeManagementTabPanel.add(turnPagePanel);
        // add CRUD button listeners
        employeeReadButton.addActionListener(event -> {
            employeeTabCurrentIndex = 1;
            salaryPageField.setText("");
            salaryPageField.setHint(1 + "/" + getSetMaxIndex(employeeSet));
            readEmployee(employeeTabCurrentIndex);
            firstPageButton.setEnabled(false);
            previousPageButton.setEnabled(false);
            if (getSetMaxIndex(employeeSet) == 1) {
                nextPageButton.setEnabled(false);
                lastPageButton.setEnabled(false);
            }
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
            int i = JOptionPane.showConfirmDialog(null, "确定删除该行员工数据吗？", "删除确认", YES_NO_OPTION);
            if (i == YES_OPTION) {
                if (service.deleteEmployee((Integer) employeeTable.getValueAt(row, 0))) {
                    readEmployee(employeeTabCurrentIndex);
                }
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
            if (newValue != null && service.updateEmployee(
                    (Integer) employeeTable.getValueAt(row, 0),
                    EMPLOYEE_TABLE_COLUMN_NAMES_ORIGIN[column],
                    newValue)) {
                readEmployee(employeeTabCurrentIndex);
            }
        });
        // add file IO button listeners
        employeeImportButton.addActionListener(event -> {
            chooser.setCurrentDirectory(new File("D:/"));
            chooser.showOpenDialog(this);
            service.importEmployees(chooser.getSelectedFile());
            readEmployee(employeeTabCurrentIndex);
        });
        // add page turn button listeners
        firstPageButton.addActionListener(event -> {
            updateEmployeeTablePage(1);
            firstPageButton.setEnabled(false);
            previousPageButton.setEnabled(false);
            nextPageButton.setEnabled(true);
            lastPageButton.setEnabled(true);
            employeePageField.setHint(employeeTabCurrentIndex + "/" + getSetMaxIndex(employeeSet));
        });
        previousPageButton.addActionListener(event -> {
            updateEmployeeTablePage(employeeTabCurrentIndex - 1);
            if (employeeTabCurrentIndex.equals(1)) {
                firstPageButton.setEnabled(false);
                previousPageButton.setEnabled(false);
            }
            nextPageButton.setEnabled(true);
            lastPageButton.setEnabled(true);
            employeePageField.setHint(employeeTabCurrentIndex + "/" + getSetMaxIndex(employeeSet));
        });
        jumpPageButton.addActionListener(event -> {
            String pageText = salaryPageField.getText();
            if (pageText.isEmpty()) {
                return;
            }
            int index = Integer.parseInt(pageText);
            Integer maxIndex = getSetMaxIndex(employeeSet);
            if (index <= 1) {
                updateEmployeeTablePage(1);
                firstPageButton.setEnabled(false);
                previousPageButton.setEnabled(false);
                nextPageButton.setEnabled(true);
                lastPageButton.setEnabled(true);
            } else if (index >= maxIndex) {
                updateEmployeeTablePage(maxIndex);
                nextPageButton.setEnabled(false);
                lastPageButton.setEnabled(false);
                firstPageButton.setEnabled(true);
                previousPageButton.setEnabled(true);
            } else {
                updateEmployeeTablePage(index);
            }
            employeePageField.setText("");
            employeePageField.setHint(employeeTabCurrentIndex + "/" + maxIndex);
        });
        nextPageButton.addActionListener(event -> {
            updateEmployeeTablePage(employeeTabCurrentIndex + 1);
            Integer maxIndex = getSetMaxIndex(employeeSet);
            if (employeeTabCurrentIndex.equals(maxIndex)) {
                nextPageButton.setEnabled(false);
                lastPageButton.setEnabled(false);
            }
            firstPageButton.setEnabled(true);
            previousPageButton.setEnabled(true);
            employeePageField.setHint(employeeTabCurrentIndex + "/" + maxIndex);
        });
        lastPageButton.addActionListener(event -> {
            Integer maxIndex = getSetMaxIndex(employeeSet);
            updateEmployeeTablePage(maxIndex);
            nextPageButton.setEnabled(false);
            lastPageButton.setEnabled(false);
            if (getSetMaxIndex(employeeSet) != 1) {
                firstPageButton.setEnabled(true);
                previousPageButton.setEnabled(true);
            }
            employeePageField.setHint(employeeTabCurrentIndex + "/" + maxIndex);
        });
        return employeeManagementTabPanel;
    }

    public void readEmployee(Integer index) {
        String info = employeeReadInputField.getText();
        if (!info.isEmpty()) { // match read mode
            employeeSet = service.readEmployee(info); // read by employee No or name
        } else {
            employeeSet = service.readAllEmployees(); // read all employees when the info is empty
        }
        updateTable(employeeTable, employeesToArray(index), EMPLOYEE_TABLE_COLUMN_NAMES_CHINESE, employeeTableScrollPane);
        pack();
    }

    public void updateEmployeeTablePage(Integer index) {
        Integer maxIndex = getSetMaxIndex(employeeSet);
        employeeTabCurrentIndex = index > maxIndex ? maxIndex : index; // reset to max if index bigger than max
        employeePageField.setHint(employeeTabCurrentIndex + "/" + maxIndex);
        readSalary(index);
    }

    private JPanel generateSalaryManagementTab() {
        // this tab allow administrator to manage salaries
        JPanel salaryManagementTabPanel = new JPanel();
        // generate CRUD panel
        JPanel salaryCrudPanel = new JPanel();
        JLabel salaryReadLabel1 = new JLabel("员工：");
        salaryReadInfoInputField = new HintTextField(16, "工号/姓名/留空"); // input employee name or No
        JLabel salaryReadLabel2 = new JLabel("月份：");
        salaryReadMonthInputField1 = new HintTextField(25, "起始月份(yyyyMM)/留空"); // input month 'from'
        JLabel salaryReadLabel3 = new JLabel(" - ");
        salaryReadMonthInputField2 = new HintTextField(25, "结束月份(yyyyMM)/留空"); // input month 'to'
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
        // generate salary management table
        MyTableModel model = new MyTableModel(salaryToArray(salaryTabCurrentIndex), SALARY_TABLE_COLUMN_NAMES_CHINESE);
        model.setRowCount(10);
        salaryTable = new JTable(model);
        salaryTable.setAutoCreateRowSorter(true);
        salaryTable.setPreferredScrollableViewportSize(salaryTable.getPreferredSize());
        salaryTableScrollPane = new JScrollPane(salaryTable);
        salaryTableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), "Result"));
        salaryManagementTabPanel.add(salaryTableScrollPane);
        // generate page turn button panel
        JPanel turnPagePanel = new JPanel();
        JButton firstPageButton = new JButton("首页");
        firstPageButton.setEnabled(false);
        JButton previousPageButton = new JButton("上一页");
        previousPageButton.setEnabled(false);
        salaryPageField = new HintTextField(4, salaryTabCurrentIndex + "/" + getSetMaxIndex(salarySet));
        JButton jumpPageButton = new JButton("翻至该页");
        JButton nextPageButton = new JButton("下一页");
        JButton lastPageButton = new JButton("尾页");
        if (getSetMaxIndex(salarySet) == 1) {
            nextPageButton.setEnabled(false);
            lastPageButton.setEnabled(false);
        }
        turnPagePanel.add(firstPageButton);
        turnPagePanel.add(previousPageButton);
        turnPagePanel.add(salaryPageField);
        turnPagePanel.add(jumpPageButton);
        turnPagePanel.add(nextPageButton);
        turnPagePanel.add(lastPageButton);
        salaryManagementTabPanel.add(turnPagePanel);
        // add CRUD button listeners
        salaryReadButton.addActionListener(event -> {
            salaryTabCurrentIndex = 1;
            salaryPageField.setText("");
            Integer maxIndex = getSetMaxIndex(salarySet);
            salaryPageField.setHint(1 + "/" + maxIndex);
            readSalary(salaryTabCurrentIndex);
            firstPageButton.setEnabled(false);
            previousPageButton.setEnabled(false);
            if (maxIndex == 1) {
                nextPageButton.setEnabled(false);
                lastPageButton.setEnabled(false);
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
            if ((column > 9 && column != 12) || column < 1) { // 12(month) is editable
                showHint("此项数据无法编辑！");
                return;
            }
            String newValue = JOptionPane.showInputDialog(null, "输入新值", salaryTable.getValueAt(row, column));
            if (service.updateSalary(
                    (Integer) salaryTable.getValueAt(row, 0),
                    SALARY_TABLE_COLUMN_NAMES_ORIGIN[column],
                    newValue)) {
                readSalary(salaryTabCurrentIndex);
            }
        });
        // add page turn button listeners
        firstPageButton.addActionListener(event -> {
            salaryTabCurrentIndex = 1;
            updateSalaryTablePage(1);
            firstPageButton.setEnabled(false);
            previousPageButton.setEnabled(false);
            nextPageButton.setEnabled(true);
            lastPageButton.setEnabled(true);
            salaryPageField.setHint(salaryTabCurrentIndex + "/" + getSetMaxIndex(salarySet));
        });
        previousPageButton.addActionListener(event -> {
            salaryTabCurrentIndex -= 1;
            updateSalaryTablePage(salaryTabCurrentIndex);
            if (salaryTabCurrentIndex.equals(1)) {
                firstPageButton.setEnabled(false);
                previousPageButton.setEnabled(false);
            }
            nextPageButton.setEnabled(true);
            lastPageButton.setEnabled(true);
            salaryPageField.setHint(salaryTabCurrentIndex + "/" + getSetMaxIndex(salarySet));
        });
        jumpPageButton.addActionListener(event -> {
            String pageText = salaryPageField.getText();
            if (pageText.isEmpty()) {
                return;
            }
            salaryTabCurrentIndex = Integer.parseInt(pageText);
            Integer maxIndex = getSetMaxIndex(salarySet);
            if (salaryTabCurrentIndex <= 1) {
                updateSalaryTablePage(1);
                firstPageButton.setEnabled(false);
                previousPageButton.setEnabled(false);
                nextPageButton.setEnabled(true);
                lastPageButton.setEnabled(true);
            } else if (salaryTabCurrentIndex >= maxIndex) {
                updateSalaryTablePage(maxIndex);
                nextPageButton.setEnabled(false);
                lastPageButton.setEnabled(false);
                firstPageButton.setEnabled(true);
                previousPageButton.setEnabled(true);
            } else {
                updateSalaryTablePage(salaryTabCurrentIndex);
            }
            salaryPageField.setText("");
            salaryPageField.setHint(salaryTabCurrentIndex + "/" + maxIndex);
        });
        nextPageButton.addActionListener(event -> {
            salaryTabCurrentIndex += 1;
            updateSalaryTablePage(salaryTabCurrentIndex);
            Integer maxIndex = getSetMaxIndex(salarySet);
            if (salaryTabCurrentIndex.equals(maxIndex)) {
                nextPageButton.setEnabled(false);
                lastPageButton.setEnabled(false);
            }
            firstPageButton.setEnabled(true);
            previousPageButton.setEnabled(true);
            salaryPageField.setHint(salaryTabCurrentIndex + "/" + maxIndex);
        });
        lastPageButton.addActionListener(event -> {
            salaryTabCurrentIndex = getSetMaxIndex(salarySet);
            updateSalaryTablePage(salaryTabCurrentIndex);
            nextPageButton.setEnabled(false);
            lastPageButton.setEnabled(false);
            firstPageButton.setEnabled(true);
            previousPageButton.setEnabled(true);
            salaryPageField.setHint(salaryTabCurrentIndex + "/" + salaryTabCurrentIndex);
        });
        return salaryManagementTabPanel;
    }

    public void readSalary(Integer index) {
        String info = salaryReadInfoInputField.getText();
        String from = salaryReadMonthInputField1.getText();
        String to = salaryReadMonthInputField2.getText();
        // match 'read' pattern
        Set<Salary> result = service.readSalary(info, from, to);
        if (result != null) {
            salarySet = result;
            updateTable(salaryTable, salaryToArray(index), SALARY_TABLE_COLUMN_NAMES_CHINESE, salaryTableScrollPane);
            pack();
        }
    }

    public void updateSalaryTablePage(Integer index) {
        Integer maxIndex = getSetMaxIndex(salarySet);
        salaryTabCurrentIndex = Math.min(index, maxIndex); // reset to max if index bigger than max
        salaryPageField.setHint(salaryTabCurrentIndex + "/" + maxIndex);
        readSalary(index);
    }

    private Object[][] employeesToArray(Integer index) {
        int rows = index.equals(getSetMaxIndex(employeeSet)) ? employeeSet.size() - (index - 1) * 10 : 10;
        Object[][] objects = new Object[rows][]; // rows
        Iterator<Employee> iterator = employeeSet.iterator();
        for (int i = 0; i < (index - 1) * 10; i++) {
            iterator.next();
        }
        for (int i = (index - 1) * 10; i < 10; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            Employee next = iterator.next();
            objects[i] = new Object[15]; // columns
            Object[] values = objects[i];
            values[0] = next.getId();
            values[1] = next.getNo();
            values[2] = next.getAccount();
            values[3] = next.getAdmin() ? "是" : "否";
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

    private Object[][] salaryToArray(Integer index) {
        int rows = index.equals(getSetMaxIndex(salarySet)) ? salarySet.size() - (index - 1) * 10 : 10;
        Object[][] objects = new Object[rows][];
        Iterator<Salary> iterator = salarySet.iterator();
        for (int i = 0; i < (index - 1) * 10; i++) {
            iterator.next();
        }
        for (int i = 0; i < 10; i++) {
            if (!iterator.hasNext()) {
                break;
            }
            Salary next = iterator.next();
            objects[i] = new Object[17];
            Object[] values = objects[i];
            values[0] = next.getId();
            values[1] = service.readEmployee(next.getEmployeeId()).getNo();
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
