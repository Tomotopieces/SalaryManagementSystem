package io.tomoto.service.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.dao.impl.EmployeeDao;
import io.tomoto.dao.impl.SalaryDao;
import io.tomoto.service.Service;
import io.tomoto.view.impl.AdminView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import static io.tomoto.util.RegexUtil.ACCOUNT_PATTERN;
import static io.tomoto.util.RegexUtil.MONTH_PATTERN;
import static io.tomoto.util.RegexUtil.NO_PATTERN;
import static io.tomoto.util.RegexUtil.PASSWORD_PATTERN;
//import java.util.stream.Collectors;

/**
 * Service class for administrator. Please set the adminId before use.
 * <p>
 * Singleton.
 *
 * @author Tomoto
 * <p>
 * 2020/11/4 19:01
 */
public class AdminService implements Service {
    private static final Logger logger = LogManager.getLogger(AdminService.class);
    private final EmployeeDao employeeDao;
    private final SalaryDao salaryDao;

    private Integer adminId = 0;
    private AdminView view;

    private AdminService() {
        employeeDao = EmployeeDao.getInstance();
        salaryDao = SalaryDao.getInstance();
    }

    /**
     * Gets the only instance.
     *
     * @param view an Administrator view
     * @return the instance or null if employee with the id is not an administrator
     */
    public static AdminService getInstance(AdminView view) {
        Employee employee = Instance.INSTANCE.employeeDao.read(view.getId());
        if (employee == null || !employee.getAdmin()) { // if the employee is not an administrator
            logger.warn("Employee with id: " + view.getId() + " is not an administrator.");
            view.showHint("Employee with id: " + view.getId() + " do not exists or isn't an administrator");
            return null;
        }
        Instance.INSTANCE.view = view;
        Instance.INSTANCE.adminId = view.getId();
        return Instance.INSTANCE;
    }

    // Employee CRUD

    /**
     * Creates a new employee.
     *
     * @param no       employee number
     * @param account  employee account
     * @param isAdmin  whether the employee is an administrator
     * @param password employee account password
     * @param name     employee name
     * @param idNo     employee id card number
     * @param phone    employee phone number
     * @param email    employee e-mail address
     * @param gender   employee gender
     * @param birthday employee birthday, format as 'yyyy-MM-dd'
     * @return whether the creation successful or not
     */
    public Boolean createEmployee(String no, String account, Boolean isAdmin, String password,
                                  String name, String idNo, String phone, String email, String gender, String birthday) {
        Employee employee = new Employee(
                no, account, isAdmin, password,
                name, idNo, phone, email, gender, birthday,
                adminId);
        if (!ACCOUNT_PATTERN.matcher(account).matches()) { // if illegal account format
            logger.warn("Failed to create new employee because of illegal account format: " + account);
            view.showHint("账户名不合法！必须为六位任意字符并且不含空格。");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) { // if illegal password format
            logger.warn("Failed to create new employee because of illegal password format: " + password);
            view.showHint("密码不合法！必须为六位任意字符并且不含空格。");
            return false;
        } // TODO checking: unique idNo, unique phone, unique email

        if (!employeeDao.create(employee)) { // if no employee was created
            logger.warn("Failed to create new employee: " + employee + " for some reason.");
            view.showHint("账户创建失败！");
            return false;
        }
        logger.info("Administrator with id: " + adminId + " created a new employee(ignore id): " + employee);
        view.showHint("账号创建成功！");
        return true;
    }

    /**
     * Deletes an employee.
     *
     * @param employeeId employee id for delete.
     * @return whether the deletion successful or not
     */
    public Boolean deleteEmployee(Integer employeeId) {
        if (!employeeDao.delete(employeeId)) { // if no employee was deleted
            logger.warn("Failed to delete an employee with id: " + employeeId);
            view.showHint("账户删除失败！");
            return false;
        }
        logger.info(adminId);
        view.showHint("账户删除成功！");
        return true;
    }

    /**
     * Reads an employee by id.
     *
     * @param employeeId employee id for read.
     * @return the employee
     */
    public Employee readEmployee(Integer employeeId) {
        return employeeDao.read(employeeId);
    }

    /**
     * Reads employee(s) by info.
     *
     * @param info employee No or name
     * @return a set of employee
     */
    public Set<Employee> readEmployee(String info) {
        Set<Employee> result = readAllEmployees();
        if (NO_PATTERN.matcher(info).matches()) {
            result.removeIf(employee -> !employee.getNo().equals(info));
        } else {
            result.removeIf(employee -> !employee.getName().equals(info));
        }
        return result;
    }

    /**
     * Reads all employees.
     *
     * @return a set of employees
     */
    public Set<Employee> readAllEmployees() {
        return employeeDao.readAll();
    }

    /**
     * Updates an employee.
     *
     * @param id       employee id
     * @param no       employee number
     * @param account  employee account
     * @param isAdmin  whether the employee is an administrator
     * @param password employee account password
     * @param name     employee name
     * @param idNo     employee id card number
     * @param phone    employee phone number
     * @param email    employee e-mail address
     * @param gender   employee gender
     * @param birthday employee birthday, format as 'yyyy-MM-dd'
     * @return whether the update successful or not
     */
    public Boolean updateEmployee(Integer id, String no, String account, Boolean isAdmin, String password,
                                  String name, String idNo, String phone, String email, String gender, String birthday) {
        Employee employee = employeeDao.read(id);
        if (employee == null) { // if the employee does not exists
            logger.warn("Employee with id: " + id + " does not exists.");
            view.showHint("员工id不存在！");
            return false;
        }
        if (!employeeDao.update( // if no employee was updated
                employee.setNo(no).setAccount(account).setAdmin(isAdmin).setPassword(password)
                        .setName(name).setIdNo(idNo).setPhone(phone).setEmail(email).setGender(gender)
                        .setBirthday(Timestamp.valueOf(birthday))
                        .setUpdateOperatorId(adminId))) {
            logger.warn("Failed to update employee to: " + employee + " for some reason.");
            view.showHint("账户更新失败！");
            return false;
        }
        view.showHint("账户更新成功！");
        return true;
    }

    // Salary CRUD (without delete)

    /**
     * Creates a new salary slip.
     *
     * @param employeeId id of employee of the salary
     * @param base       basic salary
     * @param post       post salary
     * @param length     length of service salary
     * @param phone      communication subsidy
     * @param traffic    transportation subsidy
     * @param tax        personal income tax
     * @param security   social security
     * @param fund       housing fund
     * @param actually   actually salary
     * @param month      salary giving month, format as 'yyyyMM'
     * @return whether the creation successful or not
     */
    public Boolean createSalary(
            Integer employeeId,
            Double base, Double post, Double length, Double phone, Double traffic,
            Double tax, Double security, Double fund,
            Double actually, String month) {
        if (!MONTH_PATTERN.matcher(month).matches()) {
            logger.warn("Failed to create new salary because of wrong month format: " + month);
            view.showHint("创建工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return false;
        } else if (salaryDao.readAll().stream().anyMatch(salary ->
                salary.getEmployeeId().equals(employeeId) && // if the employee's
                        salary.getMonth().equals(month))) { // the month salary is already exists
            logger.warn("Failed to create new salary because the salary for employee with id: " + employeeId + " of " +
                    "month: " + month + " is already exists.");
            view.showHint("创建工资条失败！员工当月工资条已存在。");
            return false;
        }
        Salary salary = new Salary(
                employeeId,
                base, post, length, phone, traffic,
                tax, security, fund,
                actually, month,
                adminId);
        if (!salaryDao.create(salary)) { // if no salary was created
            logger.warn("Failed to create new salary: " + salary + " for some reason.");
            view.showHint("创建工资条失败！");
            return false;
        }
        logger.info("Administrator with id: " + adminId + " created a new salary(ignore id): " + salary);
        view.showHint("创建工资条成功！");
        return true;
    }

    /**
     * Reads salary(ies) by given info.
     *
     * @param info the employee name or account
     * @return a set of salary(ies)
     */
    public Set<Salary> readSalary(String info) {
        HashSet<Salary> result = new HashSet<>();

        // get employee(s)
        Set<Employee> employees = employeeDao.readAll();
        if /* info is an account */ (ACCOUNT_PATTERN.matcher(info).matches()) {
            employees.removeIf(e -> !e.getAccount().equals(info));
        } else /* if info is a name */ {
            employees.removeIf(e -> !e.getName().equals(info));
        }

        // get salary slip(s)
        Set<Salary> salaries = salaryDao.readAll();
        employees.forEach(employee ->
                salaries.forEach(salary -> {
                    if (salary.getEmployeeId().equals(employee.getId())) { // match salary month
                        result.add(salary);
                    }
                }));
        return result;
    }

    /**
     * Reads salary(ies) by given info and month.
     *
     * @param info  the employee name or account
     * @param month the month of salary, format as 'yyyyMM'
     * @return a set of salary(ies)
     */
    public Set<Salary> readSalary(String info, String month) {
        if (!MONTH_PATTERN.matcher(month).matches()) {
            logger.warn("Failed to read salary(ies) because of wrong month format: " + month);
            view.showHint("读取工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return null;
        }

        Set<Salary> result = new HashSet<>();

        // get employee(s)
        Set<Employee> employees = employeeDao.readAll();
        if /* info is an account */ (ACCOUNT_PATTERN.matcher(info).matches()) {
            employees.removeIf(e -> !e.getAccount().equals(info));
        } else /* if info is a name */ {
            employees.removeIf(e -> !e.getName().equals(info));
        }

        // get salary slip(s)
        Set<Salary> salaries = salaryDao.readAll();
        employees.forEach(employee ->
                salaries.forEach(salary -> {
                    if (salary.getEmployeeId().equals(employee.getId()) && // match employee id
                            salary.getMonth().equals(month)) { // match salary month
                        result.add(salary);
                    }
                }));
        return result;
    }

    /**
     * Reads salary(ies) by given info and month period.
     *
     * @param info the employee name or account
     * @param from the begin month of salary, format as 'yyyyMM'
     * @param to   the end month of salary, format as 'yyyyMM'
     * @return a set of salary(ies)
     */
    public Set<Salary> readSalary(String info, String from, String to) {
        if (!MONTH_PATTERN.matcher(from).matches() || MONTH_PATTERN.matcher(to).matches()) {
            logger.warn("Failed to read salary(ies) because of wrong month format: " + from + " " + to);
            view.showHint("读取工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return null;
        }

        Set<Salary> result = new HashSet<>();

        // get employee(s)
        Set<Employee> employees = employeeDao.readAll();
        if /* info is an account */ (ACCOUNT_PATTERN.matcher(info).matches()) {
            employees.removeIf(e -> !e.getAccount().equals(info));
        } else /* if info is a name */ {
            employees.removeIf(e -> !e.getName().equals(info));
        }

        // get salary slip(s)
        Set<Salary> salaries = salaryDao.readAll();
        employees.forEach(employee ->
                salaries.forEach(salary -> {
                    if (salary.getEmployeeId().equals(employee.getId()) && // match employee id
                            salary.getMonth().compareTo(from) >= 0 && // match salary begin month
                            salary.getMonth().compareTo(to) <= 0) { // match salary end month
                        result.add(salary);
                    }
                }));
        return result;
    }

    /**
     * Reads all salary(ies).
     *
     * @return a set of salary(ies)
     */
    public Set<Salary> readAllSalaries() {
        return salaryDao.readAll();
    }

    /**
     * Updates a salary slip.
     *
     * @param id         salary slip id
     * @param employeeId id of employee of the salary
     * @param base       basic salary
     * @param post       post salary
     * @param length     length of service salary
     * @param phone      communication subsidy
     * @param traffic    transportation subsidy
     * @param tax        personal income tax
     * @param security   social security
     * @param fund       housing fund
     * @param actually   actually salary
     * @param month      salary giving month, format as 'yyyyMM'
     * @return whether the update successful or not
     */
    public Boolean updateSalary(
            Integer id, Integer employeeId,
            Double base, Double post, Double length, Double phone, Double traffic,
            Double tax, Double security, Double fund,
            Double actually, String month) {
        if (!MONTH_PATTERN.matcher(month).matches()) {
            logger.warn("Failed to update salary because of wrong month format: " + month);
            view.showHint("更新工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return false;
        }
        Salary salary = salaryDao.read(id);
        if (!salaryDao.update( // if no salary slip was updated
                salary.setEmployeeId(employeeId)
                        .setBase(base).setPost(post).setLength(length).setPhone(phone).setTraffic(traffic)
                        .setTax(tax).setSecurity(security).setFund(fund)
                        .setActually(actually).setMonth(month)
                        .setUpdateOperatorId(id))) {
            logger.warn("Failed to update salary to: " + salary + " for some reason.");
            view.showHint("更新工资条失败！");
            return false;
        }
        view.showHint("更新工资条成功！");
        return true;
    }

    @Override
    public void close() throws SQLException {
        employeeDao.close();
        logger.info("io.tomoto.service.impl.AdminService.employeeDao closed.");
        salaryDao.close();
        logger.info("io.tomoto.service.impl.AdminService.salaryDao closed.");
    }

    public AdminService setAdminId(Integer adminId) {
        this.adminId = adminId;
        return this;
    }

    /**
     * Singleton instance class.
     */
    private static class Instance {
        public static final AdminService INSTANCE = new AdminService();
    }
}