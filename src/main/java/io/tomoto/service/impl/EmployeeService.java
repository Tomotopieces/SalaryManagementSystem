package io.tomoto.service.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.dao.impl.EmployeeDao;
import io.tomoto.dao.impl.SalaryDao;
import io.tomoto.service.Service;
import io.tomoto.view.impl.EmployeeView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.util.Set;

import static io.tomoto.util.RegexUtil.PASSWORD_PATTERN;

/**
 * Service class for employees.
 * <p>
 * Singleton.
 *
 * @author Tomoto
 * <p>
 * 2020/11/4 17:56
 */
public class EmployeeService implements Service {
    private static final Logger logger = LogManager.getLogger(EmployeeService.class);
    private final EmployeeDao employeeDao;
    private final SalaryDao salaryDao;
    private EmployeeView view;
    private Integer id;

    private EmployeeService() {
        employeeDao = EmployeeDao.getInstance();
        salaryDao = SalaryDao.getInstance();
    }

    /**
     * Gets the only instance.
     *
     * @param view an Employee view
     * @return the instance or null if the employee with the id does not exists
     */
    public static EmployeeService getInstance(EmployeeView view) {
        if (Instance.INSTANCE.employeeDao.read(view.getId()) == null) { // if the employee does not exists
            logger.warn("Employee with id: " + view.getId() + " does not exists.");
            view.showHint("用户不存在");
            return null;
        }
        Instance.INSTANCE.view = view;
        Instance.INSTANCE.id = view.getId();
        return Instance.INSTANCE;
    }

    /**
     * Reads the employee.
     *
     * @return the employee
     */
    public Employee readEmployee() {
        return employeeDao.read(id);
    }

    /**
     * Reads a salary slip.
     *
     * @param month the specific month of salary payment
     * @return the salary or null if not exists
     */
    public Salary readSalary(String month) {
        logger.info("Employee with id: " + id + " query the salary slip of month: " + month);
        Set<Salary> salaries = salaryDao.readAll();
        salaries.removeIf(salary -> !salary.getMonth().equals(month));
        return salaries.iterator().next();
    }

    /**
     * Reads all salary slips.
     *
     * @return all salary slips
     */
    public Set<Salary> readAllSalaries() {
        return salaryDao.readAll();
    }

    /**
     * Reset the password.
     *
     * @param oldPassword the old password for check
     * @param newPassword the new password
     */
    public Boolean resetPassword(String oldPassword, String newPassword, String confirmNewPassword) {
        Employee employee = employeeDao.read(id);
        if (!employee.getPassword().equals(oldPassword)) { // if old password wrong
            logger.warn("Wrong old password while reset the password for the employee with id: " + id);
            view.showHint("旧密码错误！");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(newPassword).matches()) { // if new password doesn't match the pattern
            logger.warn("Wrong password pattern while reset the password for the employee with id: " + id);
            view.showHint("新密码必须为6位且不含空格！");
            return false;
        } else if (!newPassword.equals(confirmNewPassword)) { // if two new passwords are not the same
            logger.warn("Two new passwords are not the same while reset the password for the employee with id: " + id);
            view.showHint("两次新密码不一致！");
            return false;
        }

        // if nothing wrong, update
        employee.setPassword(newPassword);
        employeeDao.update(employee);
        logger.info("Employee with id: " + id + " reset the password.");
        view.showHint("密码修改成功！");
        return true;
    }

    @Override
    public void close() throws SQLException {
        employeeDao.close();
        logger.info("io.tomoto.service.impl.EmployeeService.employeeDao closed.");
        salaryDao.close();
        logger.info("io.tomoto.service.impl.EmployeeService.salaryDao closed.");
    }

    /**
     * Singleton instance class.
     */
    private static class Instance {
        public static final EmployeeService INSTANCE = new EmployeeService();
    }
}
