package io.tomoto.main;

import io.tomoto.view.impl.LoginView;

import javax.swing.*;
import java.awt.*;

/**
 * Client main class.
 * @author Tomoto
 * <p>
 * 2020/11/4 10:51
 */
public class Main {
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = LoginView.getInstance();
            frame.setVisible(true);
        });
    }
}
