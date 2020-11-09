package io.tomoto.view.impl;

import io.tomoto.service.impl.LoginService;
import io.tomoto.view.SubView;
import io.tomoto.view.View;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Login frame.
 *
 * @author Tomoto
 * <p>
 * 2020/11/5 11:23
 */
public class LoginView extends JFrame implements View {
    private static final Integer DEFAULT_WIDTH = 320;
    private static final Integer DEFAULT_HEIGHT = 320;

    private final LoginService service;

    private Boolean isAdmin = false;
    private JTextField accountField;
    private JPasswordField passwordField;

    private LoginView() {
        // initialize frame fields
        service = LoginService.getInstance(this);
        initFrameInfo(); // initialize frame information
        JPanel mainPanel = new JPanel(new GridLayout(3, 1)); // generate main panel
        mainPanel.add(generateTitleLabel()); // add title part
        mainPanel.add(generateInputPanel()); // add input part
        mainPanel.add(generateConfirmPanel()); // add confirm part

        add(mainPanel);
        pack();
    }

    /**
     * Invoke this method to get the only instance of LoginView.
     */
    public static LoginView getInstance() {
        return Instance.INSTANCE;
    }

    @Override
    public void initFrameInfo() {
        setTitle("登录界面");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // not exit the program when close
        setSize(getPreferredSize());
        setResizable(false);
        setWindowsLookAndFeel();
    }

    private JLabel generateTitleLabel() {
        JLabel titleLabel = new JLabel("番茄工资管理系统");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getFamily(), Font.BOLD, 24));
        return titleLabel;
    }

    private JPanel generateInputPanel() {
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));
        // generate identity selection panel
        JPanel radioButtonPanel = new JPanel(); // for identity selection
        ButtonGroup radioButtonGroup = new ButtonGroup(); // ditto
        JLabel radioButtonLabel = new JLabel("身份：");
        radioButtonLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JRadioButton employeeButton = new JRadioButton("普通员工", true /* default selected */);
        JRadioButton adminButton = new JRadioButton("系统管理员");
        radioButtonPanel.add(radioButtonLabel);
        radioButtonPanel.add(employeeButton);
        radioButtonPanel.add(adminButton);
        radioButtonGroup.add(employeeButton);
        radioButtonGroup.add(adminButton);
        inputPanel.add(radioButtonPanel);
        // generate account part panel
        JPanel accountPart = new JPanel();
        JLabel accountLabel = new JLabel("账号：");
        accountField = new JTextField(16);
        accountPart.add(accountLabel);
        accountPart.add(accountField);
        inputPanel.add(accountPart);
        // generate password part panel
        JPanel passwordPart = new JPanel();
        JLabel passwordLabel = new JLabel("密码：");
        passwordField = new JPasswordField(16);
        passwordPart.add(passwordLabel);
        passwordPart.add(passwordField);
        inputPanel.add(passwordPart, BorderLayout.CENTER);
        // add button listener
        adminButton.addActionListener(event -> isAdmin = true);
        employeeButton.addActionListener(event -> isAdmin = false);
        return inputPanel;
    }

    private JPanel generateConfirmPanel() {
        JPanel confirmPanel = new JPanel();
        JButton confirmButton = new JButton("确定");
        confirmPanel.add(confirmButton);
        confirmButton.addActionListener(event -> {
            Boolean isAdmin = this.isAdmin;
            String accountText = accountField.getText();
            String passwordText = new String(passwordField.getPassword());
            if (!accountText.isEmpty() && !passwordText.isEmpty()) {
                if (service.login(isAdmin, accountText, passwordText)) {
                    setVisible(false);
                }
            }
        });
        return confirmPanel;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    @Override
    public void close() throws SQLException {
        service.close();
    }

    private static class Instance {
        public static final LoginView INSTANCE = new LoginView();
    }
}
