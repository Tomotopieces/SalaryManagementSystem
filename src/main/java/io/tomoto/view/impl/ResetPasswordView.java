package io.tomoto.view.impl;

import io.tomoto.view.SubView;

import javax.swing.*;
import java.awt.*;

/**
 * Password reset frame.
 * <p>
 * Sub frame of Employee frame.
 *
 * @author Tomoto
 * <p>
 * 2020/11/5 16:29
 */
public class ResetPasswordView extends JFrame implements SubView {
    private static final Integer DEFAULT_WIDTH = 320;
    private static final Integer DEFAULT_HEIGHT = 240;

    private final JPasswordField oldPasswordField;
    private final JPasswordField newPasswordField;
    private final JPasswordField confirmPasswordField;

    EmployeeView view;

    public ResetPasswordView(EmployeeView view) {
        this.view = view;
        setTitle("修改密码");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setWindowsLookAndFeel();
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setLayout(new GridLayout(4, 1));

        JPanel oldPasswordPart = new JPanel();
        JLabel oldPasswordLabel = new JLabel("旧密码：");
        oldPasswordField = new JPasswordField(16);
        oldPasswordPart.add(oldPasswordLabel);
        oldPasswordPart.add(oldPasswordField);

        JPanel newPasswordPart = new JPanel();
        JLabel newPasswordLabel = new JLabel("新密码：");
        newPasswordField = new JPasswordField(16);
        newPasswordPart.add(newPasswordLabel);
        newPasswordPart.add(newPasswordField);

        JPanel confirmPasswordPart = new JPanel();
        JLabel confirmPasswordLabel = new JLabel("确认新密码：");
        confirmPasswordField = new JPasswordField(16);
        confirmPasswordPart.add(confirmPasswordLabel);
        confirmPasswordPart.add(confirmPasswordField);

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("确定");
        confirmButton.addActionListener(event -> {
            if (view.getService().resetPassword(
                    new String(oldPasswordField.getPassword()),
                    new String(newPasswordField.getPassword()),
                    new String(confirmPasswordField.getPassword()))) {
                this.setVisible(false);
            }
        });
        buttonPanel.add(confirmButton);

        add(oldPasswordPart);
        add(newPasswordPart);
        add(confirmPasswordPart);
        add(buttonPanel);
        pack();
    }

    /**
     * Clear all password text field.
     */
    public void clear() {
        oldPasswordField.setText("");
        newPasswordField.setText("");
        confirmPasswordField.setText("");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
