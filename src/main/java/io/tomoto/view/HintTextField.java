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
    private final String hint;
    private boolean showingHint;

//    public HintTextField(final String hint) {
//        super(hint);
//        this.foreground = getForeground();
//        setForeground(Color.GRAY);
//        this.hint = hint;
//        this.showingHint = true;
//        super.addFocusListener(this);
//    }

    public HintTextField(Integer size, final String hint) {
        super(null, hint, size);
        this.foreground = getForeground();
        setForeground(Color.GRAY);
        this.hint = hint;
        this.showingHint = true;
        super.addFocusListener(this);
    }

    @Override
    public void focusGained(FocusEvent e) {
        if (this.getText().isEmpty()) {
            setForeground(foreground);
            super.setText("");
            showingHint = false;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        if (this.getText().isEmpty()) {
            super.setForeground(Color.GRAY);
            super.setText(hint);
            showingHint = true;
        }
    }

    @Override
    public String getText() {
        return showingHint ? "" : super.getText();
    }
}
