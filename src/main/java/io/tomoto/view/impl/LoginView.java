package io.tomoto.view.impl;

import io.tomoto.service.impl.LoginService;
import io.tomoto.view.View;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

/**
 * Login frame.
 * @author Tomoto
 * <p>
 * 2020/11/5 11:23
 */
public class LoginView extends JFrame implements View {
    private static final Integer DEFAULT_WIDTH = 320;
    private static final Integer DEFAULT_HEIGHT = 320;

    private LoginService service;

    private LoginView() {
        // initialize frame information
        setTitle("登录界面");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWindowsLookAndFeel();

        // generate main panel
        JPanel mainPanel = new JPanel(new GridLayout(3, 1));

        JLabel titleLabel = new JLabel("番茄工资管理系统");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setFont(new Font(titleLabel.getFont().getFamily(), Font.BOLD, 24));
        mainPanel.add(titleLabel);

        //generate account and password input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 1));

        // generate identity selection panel
        JPanel radioButtonPanel = new JPanel(); // for identity selection
        ButtonGroup radioButtonGroup = new ButtonGroup(); // ditto
        JLabel radioButtonLabel = new JLabel("身份：");
        radioButtonLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        JRadioButton employeeButton = new JRadioButton("普通员工", true); // default selected
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
        JTextField accountField = new JTextField(16);
        accountPart.add(accountLabel);
        accountPart.add(accountField);
        inputPanel.add(accountPart);

        // generate password part panel
        JPanel passwordPart = new JPanel();
        JLabel passwordLabel = new JLabel("密码：");
        JPasswordField passwordField = new JPasswordField(16);
        passwordPart.add(passwordLabel);
        passwordPart.add(passwordField);
        inputPanel.add(passwordPart, BorderLayout.CENTER);
        mainPanel.add(inputPanel);

        JPanel confirmPanel = new JPanel();
        JButton confirmButton = new JButton("确定");
        confirmPanel.add(confirmButton);
        mainPanel.add(confirmPanel);

        confirmButton.addActionListener(event -> {
            Boolean isAdmin = adminButton.isSelected();
            String accountText = accountField.getText();
            String passwordText = new String(passwordField.getPassword());
            if (!accountText.isEmpty() && !passwordText.isEmpty()) {
                service = LoginService.getInstance(this);
                if (service.login(isAdmin, accountText, passwordText)) {
                    setVisible(false);
                }
            }
        });

        add(mainPanel);
        pack();
    }

    public static LoginView getInstance() {
        return Instance.INSTANCE;
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
