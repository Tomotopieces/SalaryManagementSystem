package io.tomoto.view.impl;

import io.tomoto.view.SubView;

import javax.swing.*;
import java.awt.*;

/**
 * Create new employee frame.
 *
 * @author Tomoto
 * <p>
 * 2020/11/6 14:26
 */
public class CreateEmployeeView extends JFrame implements SubView {
    private static final Integer DEFAULT_WIDTH = 480;
    private static final Integer DEFAULT_HEIGHT = 320;

    private final JTextField noField;
    private final JTextField accountField;
    private final JTextField passwordField;
    private final JRadioButton adminTrueButton;
    private final JTextField nameField;
    private final JRadioButton maleButton;
    private final JTextField idNoField;
    private final JTextField birthdayField;
    private final JTextField phoneField;
    private final JTextField emailField;

    public CreateEmployeeView(AdminView view) {
        // initialize frame information
        setTitle("创建新员工");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWindowsLookAndFeel();

        JPanel mainPanel = new JPanel(new GridLayout(6, 1));

        JPanel line1 = new JPanel();

        JPanel noPart = new JPanel();
        JLabel noLabel = new JLabel("工号：");
        noField = new JTextField(8);
        noPart.add(noLabel);
        noPart.add(noField);
        line1.add(noPart);

        JPanel accountPart = new JPanel();
        JLabel accountLabel = new JLabel("账户：");
        accountField = new JTextField(8);
        accountPart.add(accountLabel);
        accountPart.add(accountField);
        line1.add(accountPart);

        JPanel passwordPart = new JPanel();
        JLabel passwordLabel = new JLabel("密码");
        passwordField = new JTextField(8);
        passwordPart.add(passwordLabel);
        passwordPart.add(passwordField);
        line1.add(passwordPart);

        JPanel line2 = new JPanel();

        JPanel adminPanel = new JPanel();
        JLabel adminLabel = new JLabel("管理员：");
        ButtonGroup adminButtonGroup = new ButtonGroup();
        adminTrueButton = new JRadioButton("是");
        JRadioButton adminFalseButton = new JRadioButton("否");
        adminFalseButton.setSelected(true);
        adminButtonGroup.add(adminTrueButton);
        adminButtonGroup.add(adminFalseButton);
        adminPanel.add(adminLabel);
        adminPanel.add(adminTrueButton);
        adminPanel.add(adminFalseButton);
        line2.add(adminPanel);

        JPanel line3 = new JPanel();

        JPanel namePart = new JPanel();
        JLabel nameLabel = new JLabel("姓名：");
        nameField = new JTextField(8);
        namePart.add(nameLabel);
        namePart.add(nameField);
        line3.add(namePart);

        JPanel genderPanel = new JPanel();
        JLabel genderLabel = new JLabel("性别：");
        ButtonGroup genderButtonGroup = new ButtonGroup();
        maleButton = new JRadioButton("男");
        maleButton.setSelected(true);
        JRadioButton femaleButton = new JRadioButton("女");
        genderButtonGroup.add(maleButton);
        genderButtonGroup.add(femaleButton);
        genderPanel.add(genderLabel);
        genderPanel.add(maleButton);
        genderPanel.add(femaleButton);
        line3.add(genderPanel);

        JPanel line4 = new JPanel();

        JPanel idNoPart = new JPanel();
        JLabel idNoLabel = new JLabel("身份证：");
        idNoField = new JTextField(20);
        idNoPart.add(idNoLabel);
        idNoPart.add(idNoField);
        line4.add(idNoPart);

        JPanel birthdayPart = new JPanel();
        JLabel birthdayLabel = new JLabel("生日：");
        birthdayField = new JTextField(15);
        birthdayPart.add(birthdayLabel);
        birthdayPart.add(birthdayField);
        line4.add(birthdayPart);

        JPanel line5 = new JPanel();

        JPanel phonePart = new JPanel();
        JLabel phoneLabel = new JLabel("手机号：");
        phoneField = new JTextField(15);
        phonePart.add(phoneLabel);
        phonePart.add(phoneField);
        line5.add(phonePart);

        JPanel emailPart = new JPanel();
        JLabel emailLabel = new JLabel("邮箱：");
        emailField = new JTextField(20);
        emailPart.add(emailLabel);
        emailPart.add(emailField);
        line5.add(emailPart);

        JPanel line6 = new JPanel();

        JButton confirmButton = new JButton("确定");
        line6.add(confirmButton);

        confirmButton.addActionListener(event -> {
            if (fieldsNotEmpty(noField, accountField, passwordField,
                    nameField, idNoField, birthdayField, phoneField, emailField)) {
                if (view.getService().createEmployee(
                        noField.getText(), accountField.getText(),
                        adminTrueButton.isSelected(), passwordField.getText(),
                        nameField.getText(), idNoField.getText(), phoneField.getText(), emailField.getText(),
                        maleButton.isSelected() ? "男" : "女", birthdayField.getText())) {
                    setVisible(false);
                }
            } else {
                showHint("不能存在空值！");
            }
        });

        mainPanel.add(line1);
        mainPanel.add(line2);
        mainPanel.add(line3);
        mainPanel.add(line4);
        mainPanel.add(line5);
        mainPanel.add(line6);

        add(mainPanel);
        pack();
    }

    @Override
    public void clear() {
        noField.setText("");
        accountField.setText("");
        passwordField.setText("");
        adminTrueButton.setText("");
        nameField.setText("");
        maleButton.setText("");
        idNoField.setText("");
        birthdayField.setText("");
        phoneField.setText("");
        emailField.setText("");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
