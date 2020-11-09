package io.tomoto.view.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.service.impl.EmployeeService;
import io.tomoto.view.HintTextField;
import io.tomoto.view.View;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static io.tomoto.util.DateUtil.DATE_FORMAT;

/**
 * Employee main frame.
 *
 * @author Tomoto
 * <p>
 * 2020/11/5 15:15
 */
public class EmployeeView extends JFrame implements View {
    private static final Integer DEFAULT_WIDTH = 480;
    private static final Integer DEFAULT_HEIGHT = 320;
    private static final Integer DEFAULT_WIDTH_2 = 1080;
    private static final Integer DEFAULT_HEIGHT_2 = 640;
    private static final String[] SALARY_TABLE_COLUMN_NAMES_CHINESE = {
            "工资条id", "员工id",
            "基本工资", "岗位工资", "工龄工资", "通讯补贴", "交通补贴",
            "个税代缴", "社保代缴", "住房公积金",
            "实际工资", "到手工资", "工资月份",
    };

    private final EmployeeService service;
    private final Integer id; // employee id
    private final JTabbedPane mainPane = new JTabbedPane();
    private final ResetPasswordView resetPasswordView = new ResetPasswordView(this); // password result frame
    private Set<Salary> salarySet = new HashSet<>(); // results of 'read'
    private JTable salaryTable; // the table for show the results of salarySet
    private JScrollPane salaryTableScrollPane;

    public EmployeeView(Integer id) {
        // initialize frame fields
        this.id = id;
        service = EmployeeService.getInstance(this);
        initFrameInfo(); // initialize frame information
        assert service != null;
        // add tabs
        mainPane.addTab("个人信息", generatePersonalInfoTab(service.readEmployee())); // add personal info tab
        mainPane.addTab("工资查询", generateSalaryReadTab()); // add salary info tab
        // set main pane after tabs addition
        setComponentsFont(mainPane, VIEW_FONT); // set all component font to MS Song, plain style, sile 12
        mainPane.addChangeListener(e -> setSize(getPreferredSize())); // set tab switch listener, reset frame size

        add(mainPane);
        pack();
    }

    @Override
    public void initFrameInfo() {
        setTitle("欢迎，" + service.readEmployee().getName() + "。");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(getPreferredSize());
        setResizable(false);
        setWindowsLookAndFeel();
    }

    /**
     * A tab with all information of an employee.
     * <p>
     * This tab show employee's information, allow employee to reset password.
     *
     * @param employee the employee
     * @return the tab panel
     */
    private JPanel generatePersonalInfoTab(Employee employee) {
        JPanel infoTabPanel = new JPanel(new GridLayout(5, 2));
        // show personal information of the employee
        infoTabPanel.add(new JLabel("工号：" + employee.getNo()));
        infoTabPanel.add(new JLabel("账号：" + employee.getAccount()));
        infoTabPanel.add(new JLabel("管理员：" + (employee.getAdmin() ? "是" : "否")));
        infoTabPanel.add(new JLabel("姓名：" + employee.getName()));
        infoTabPanel.add(new JLabel("性别：" + employee.getGender()));
        infoTabPanel.add(new JLabel("手机号：" + employee.getPhone()));
        infoTabPanel.add(new JLabel("邮箱：" + employee.getEmail()));
        infoTabPanel.add(new JLabel("身份证：" + employee.getIdNo()));
        infoTabPanel.add(new JLabel("生日：" + DATE_FORMAT.format(employee.getBirthday())));
        // generate password reset button
        JPanel buttonPanel = new JPanel();
        JButton resetPasswordButton = new JButton("修改密码");
        buttonPanel.add(resetPasswordButton);
        infoTabPanel.add(buttonPanel);
        infoTabPanel.setBorder(BorderFactory.createEtchedBorder());
        // set button listener, clear 'reset frame' then show up
        resetPasswordButton.addActionListener(event ->
                EventQueue.invokeLater(() -> {
                    resetPasswordView.clear();
                    resetPasswordView.setVisible(true);
                }));
        return infoTabPanel;
    }

    /**
     * A tab with a month input field, a read button and a result table.
     * <p>
     * This tab allow employee to query salary slips.
     *
     * @return the panel
     */
    private JPanel generateSalaryReadTab() {
        JPanel salaryTabPanel = new JPanel();
        JPanel readPanel = new JPanel();
        JLabel readInputLabel = new JLabel("月份：");
        JTextField readMonthInputField1 = new HintTextField(25, "起始月份(yyyyMM)/留空"); // input 'from'
        JLabel readInputLabel2 = new JLabel(" - ");
        JTextField readMonthInputField2 = new HintTextField(25, "结束月份(yyyyMM)/留空"); // input 'to'
        JButton readButton = new JButton("查询");
        readPanel.add(readInputLabel);
        readPanel.add(readMonthInputField1);
        readPanel.add(readInputLabel2);
        readPanel.add(readMonthInputField2);
        readPanel.add(readButton);
        salaryTabPanel.add(readPanel);
        // generate salary result table
        salaryTable = new JTable(new MyTableModel(salaryToArray(), SALARY_TABLE_COLUMN_NAMES_CHINESE));
        salaryTable.setPreferredScrollableViewportSize(salaryTable.getPreferredSize());
        salaryTableScrollPane = new JScrollPane(salaryTable);
        salaryTabPanel.add(salaryTableScrollPane);
        // set button listener, update result
        readButton.addActionListener(event -> {
            String from = readMonthInputField1.getText();
            String to = readMonthInputField2.getText();
            Set<Salary> result = service.readSalary(from, to);
            if (result != null) {
                salarySet = result;
                updateTable(salaryTable, salaryToArray(), SALARY_TABLE_COLUMN_NAMES_CHINESE, salaryTableScrollPane);
                pack();
            }
        });
        return salaryTabPanel;
    }

    private Object[][] salaryToArray() {
        Object[][] objects = new Object[salarySet.size()][];
        Iterator<Salary> iterator = salarySet.iterator();
        for (int i = 0; i < salarySet.size(); i++) {
            objects[i] = new Object[17];
            Object[] values = objects[i];
            Salary next = iterator.next();
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
            values[13] = service.readOperatorName(next.getCreateOperatorId());
            values[14] = next.getCreateTime();
            values[15] = service.readOperatorName(next.getUpdateOperatorId());
            values[16] = next.getUpdateTime();
        }
        return objects;
    }

    @Override
    public Dimension getPreferredSize() {
        if (mainPane.getSelectedIndex() == 0) {
            return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        } else {
            return new Dimension(DEFAULT_WIDTH_2, DEFAULT_HEIGHT_2);
        }
    }

    @Override
    public void close() throws SQLException {
        service.close();
    }

    /**
     * Gets the viewer employee id.
     *
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Gets the Employee service.
     *
     * @return the service
     */
    public EmployeeService getService() {
        return service;
    }
}
