package io.tomoto.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * A hint text field will show the hint while lost focus.
 * <p>
 * <a href="https://stackoverflow.com/a/1739037/12348320">Java JTextField with input hint -StackOverflow</a>
 *
 *
 * @author Tomoto
 * <p>
 * 2020/11/9 11:23
 */
public class HintTextField extends JTextField implements FocusListener {
    private final Color foreground;
    private String hint;
    private boolean showingHint;

    public HintTextField(Integer size, final String hint) {
        super(null, hint, size);
        this.foreground = getForeground();
        super.setForeground(Color.GRAY);
        this.hint = hint;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setForeground(foreground);
            super.setText("");
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            setText("");
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }

    @Override
    public void setText(String t) {
        if (t.equals("")) {
            super.setForeground(Color.GRAY);
            super.setText(hint);
            showingHint = true;
        } else {
            super.setForeground(foreground);
            super.setText(t);
            showingHint = false;
        }
    }

    public HintTextField setHint(String hint) {
        this.hint = hint;
        return this;
    }
}
