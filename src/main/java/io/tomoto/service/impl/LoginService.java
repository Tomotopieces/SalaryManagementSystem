package io.tomoto.service.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.impl.EmployeeDao;
import io.tomoto.service.Service;
import io.tomoto.view.View;
import io.tomoto.view.impl.AdminView;
import io.tomoto.view.impl.EmployeeView;

import java.awt.*;
import java.sql.SQLException;

/**
 * Account login service class.
 *
 * @author Tomoto
 * <p>
 * 2020/11/5 20:29
 */
public class LoginService implements Service {
    private View loginView;
    private View memberView;

    private LoginService() {
    }

    public static LoginService getInstance(View view) {
        Instance.INSTANCE.loginView = view;
        return Instance.INSTANCE;
    }

    public Boolean login(Boolean asAdmin, String account, String password) {
        Employee employee = EmployeeDao.getInstance().read(account);
        if (employee == null) {
            loginView.showHint("账户不存在！");
            return false;
        } else if (!employee.getPassword().equals(password)) {
            loginView.showHint("密码错误！");
            return false;
        }
        if (asAdmin) {
            EventQueue.invokeLater(() -> {
                memberView = new AdminView(employee.getId());
                ((AdminView) memberView).setVisible(true);
            });
        } else {
            EventQueue.invokeLater(() -> {
                memberView = new EmployeeView(employee.getId());
                ((EmployeeView) memberView).setVisible(true);
            });
        }
        return true;
    }

    @Override
    public void close() throws SQLException {
        loginView.close();
        memberView.close();
    }

    private static class Instance {
        public static final LoginService INSTANCE = new LoginService();
    }
}
