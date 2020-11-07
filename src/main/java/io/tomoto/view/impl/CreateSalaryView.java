package io.tomoto.view.impl;

import io.tomoto.view.SubView;

import javax.swing.*;
import java.awt.*;

/**
 * @author Tomoto
 * <p>
 * 2020/11/6 20:35
 */
public class CreateSalaryView extends JFrame implements SubView {
    private static final Integer DEFAULT_WIDTH = 480;
    private static final Integer DEFAULT_HEIGHT = 320;

    private final JTextField employeeIdField;
    private final JTextField baseField;
    private final JTextField postField;
    private final JTextField lengthField;
    private final JTextField phoneField;
    private final JTextField trafficField;
    private final JTextField taxField;
    private final JTextField securityField;
    private final JTextField fundField;
    private final JTextField actuallyField;
    private final JTextField monthField;

    public CreateSalaryView(AdminView view) {
        // initialize frame information
        setTitle("创建新工资条");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        setWindowsLookAndFeel();

        JPanel mainPanel = new JPanel(new GridLayout(5, 1));

        JPanel line1 = new JPanel();

        JPanel employeeIdPart = new JPanel();
        JLabel employeeIdLabel = new JLabel("员工id");
        employeeIdField = new JTextField(8);
        employeeIdPart.add(employeeIdLabel);
        employeeIdPart.add(employeeIdField);
        line1.add(employeeIdPart);

        JPanel basePart = new JPanel();
        JLabel baseLabel = new JLabel("基本工资");
        baseField = new JTextField(8);
        basePart.add(baseLabel);
        basePart.add(baseField);
        line1.add(basePart);

        JPanel postPart = new JPanel();
        JLabel postLabel = new JLabel("岗位工资");
        postField = new JTextField(8);
        postPart.add(postLabel);
        postPart.add(postField);
        line1.add(postPart);

        JPanel line2 = new JPanel();

        JPanel lengthPart = new JPanel();
        JLabel lengthLabel = new JLabel("工龄工资");
        lengthField = new JTextField(8);
        lengthPart.add(lengthLabel);
        lengthPart.add(lengthField);
        line2.add(lengthPart);

        JPanel phonePart = new JPanel();
        JLabel phoneLabel = new JLabel("通信补贴");
        phoneField = new JTextField(8);
        phonePart.add(phoneLabel);
        phonePart.add(phoneField);
        line2.add(phonePart);

        JPanel trafficPart = new JPanel();
        JLabel trafficLabel = new JLabel("交通补贴");
        trafficField = new JTextField(8);
        trafficPart.add(trafficLabel);
        trafficPart.add(trafficField);
        line2.add(trafficPart);

        JPanel line3 = new JPanel();

        JPanel taxPart = new JPanel();
        JLabel taxLabel = new JLabel("个税代缴");
        taxField = new JTextField(8);
        taxPart.add(taxLabel);
        taxPart.add(taxField);
        line3.add(taxPart);

        JPanel securityPart = new JPanel();
        JLabel securityLabel = new JLabel("社保代缴");
        securityField = new JTextField(8);
        securityPart.add(securityLabel);
        securityPart.add(securityField);
        line3.add(securityPart);

        JPanel fundPart = new JPanel();
        JLabel fundLabel = new JLabel("住房公积金");
        fundField = new JTextField(8);
        fundPart.add(fundLabel);
        fundPart.add(fundField);
        line3.add(fundPart);

        JPanel line4 = new JPanel();

        JPanel actuallyPart = new JPanel();
        JLabel actuallyLabel = new JLabel("实际工资");
        actuallyField = new JTextField(8);
        actuallyPart.add(actuallyLabel);
        actuallyPart.add(actuallyField);
        line4.add(actuallyPart);

        JPanel monthPart = new JPanel();
        JLabel monthLabel = new JLabel("工资月份");
        monthField = new JTextField(8);
        monthPart.add(monthLabel);
        monthPart.add(monthField);
        line4.add(monthPart);

        JPanel line5 = new JPanel();

        JButton confirmButton = new JButton("确定");
        line5.add(confirmButton);

        confirmButton.addActionListener(event -> {
            if (fieldsNotEmpty(employeeIdField,
                    baseField, postField, lengthField, phoneField, trafficField,
                    taxField, securityField, fundField,
                    actuallyField, monthField)) {
                if (view.getService().createSalary(
                        Integer.parseInt(employeeIdField.getText()),
                        Double.parseDouble(baseField.getText()),
                        Double.parseDouble(postField.getText()),
                        Double.parseDouble(lengthField.getText()),
                        Double.parseDouble(phoneField.getText()),
                        Double.parseDouble(trafficField.getText()),
                        Double.parseDouble(taxField.getText()),
                        Double.parseDouble(securityField.getText()),
                        Double.parseDouble(fundField.getText()),
                        Double.parseDouble(actuallyField.getText()),
                        monthField.getText()
                )) {
                    setVisible(false);
                } else {
                    showHint("不能存在空值！");
                }
            }
        });

        mainPanel.add(line1);
        mainPanel.add(line2);
        mainPanel.add(line3);
        mainPanel.add(line4);
        mainPanel.add(line5);

        add(mainPanel);
        pack();
    }

    @Override
    public void clear() {
        employeeIdField.setText("");
        baseField.setText("");
        postField.setText("");
        lengthField.setText("");
        phoneField.setText("");
        trafficField.setText("");
        taxField.setText("");
        securityField.setText("");
        fundField.setText("");
        actuallyField.setText("");
        monthField.setText("");
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }
}
