package io.tomoto.view.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.service.impl.EmployeeService;
import io.tomoto.view.View;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.Collections;
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
    private static final Integer DEFAULT_WIDTH_2 = 960;
    private static final Integer DEFAULT_HEIGHT_2 = 320;

    private static final String[] columnNames = {
            "id", "员工id",
            "基本工资", "岗位工资", "工龄工资", "通讯补贴", "交通补贴",
            "个税代缴", "社保代缴", "住房公积金",
            "实际工资", "工资月份"
    };

    private final EmployeeService service;
    private final Integer id; // employee id
    private final ResetPasswordView resetPasswordView = new ResetPasswordView(this);
    private final JTable salaryTable;
    private Set<Salary> salarySet = new HashSet<>();

    public EmployeeView(Integer id) {
        // initialize frame information
        this.id = id;
        service = EmployeeService.getInstance(this);
        assert service != null;
        Employee employee = service.readEmployee();
        setTitle("欢迎，" + employee.getName() + "。");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWindowsLookAndFeel();

        JTabbedPane mainPane = new JTabbedPane();

        // this tab show employee's information, allow employee to reset password
        JPanel infoTabPanel = new JPanel(new GridLayout(5, 2));
        // show personal information
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
        // add to index
        mainPane.addTab("个人信息", infoTabPanel);

        // this tab allow employee to query salary slips
        JPanel salaryTabPanel = new JPanel();
        JPanel queryPanel = new JPanel();
        JTextField queryInputField = new JTextField(16); // input month
        JButton queryButton = new JButton("查询");
        queryPanel.add(queryInputField);
        queryPanel.add(queryButton);
        salaryTabPanel.add(queryPanel);

        // generate salary result table
        salaryTable = new JTable(salaryToArray(), columnNames);
        salaryTable.setPreferredScrollableViewportSize(salaryTable.getPreferredSize());
        salaryTabPanel.add(new JScrollPane(salaryTable));
        mainPane.addTab("工资查询", salaryTabPanel);

        // set 'reset password button' listener, clear 'reset frame' then show up
        resetPasswordButton.addActionListener(event ->
                EventQueue.invokeLater(() -> {
                    resetPasswordView.clear();
                    resetPasswordView.setVisible(true);
                }));

        // set 'read button' listener, update result
        queryButton.addActionListener(event -> {
            String month = queryInputField.getText();
            if (!month.isEmpty()) {
                salarySet = Collections.singleton(service.readSalary(month));
                updateSalariesTable();
            } else {
                salarySet = service.readAllSalaries();
            }
        });

        // set tab listener, reset frame size
        mainPane.addChangeListener(e -> {
            if (mainPane.getSelectedIndex() == 0) {
                setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
            } else {
                setSize(DEFAULT_WIDTH_2, DEFAULT_HEIGHT_2);
            }
        });

        setComponentsFont(mainPane, VIEW_FONT);

        add(mainPane);
        pack();
    }

    private Object[][] salaryToArray() {
        Object[][] objects = new Object[salarySet.size()][];
        Iterator<Salary> iterator = salarySet.iterator();
        for (int i = 0; i < salarySet.size(); i++) {
            objects[i] = new Object[16];
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
            values[11] = next.getMonth();
            values[12] = next.getCreateOperatorId();
            values[13] = next.getCreateTime();
            values[14] = next.getUpdateOperatorId();
            values[15] = next.getUpdateTime();
        }
        return objects;
    }

    private void updateSalariesTable() {
        Object[] value = salaryToArray()[0];
        for (int i = 0; i < 12; i++) {
            salaryTable.setValueAt(value[i], 0, i);
        }
        salaryTable.repaint();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void close() throws SQLException {
        service.close();
    }

    public Integer getId() {
        return id;
    }

    public EmployeeService getService() {
        return service;
    }
}
