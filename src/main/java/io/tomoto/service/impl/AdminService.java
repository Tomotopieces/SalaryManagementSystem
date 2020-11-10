package io.tomoto.service.impl;

import io.tomoto.dao.entity.Employee;
import io.tomoto.dao.entity.Salary;
import io.tomoto.dao.impl.EmployeeDao;
import io.tomoto.dao.impl.SalaryDao;
import io.tomoto.service.Service;
import io.tomoto.view.impl.AdminView;
import javafx.util.Pair;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import static io.tomoto.util.RegexUtil.ACCOUNT_PATTERN;
import static io.tomoto.util.RegexUtil.EMAIL_PATTERN;
import static io.tomoto.util.RegexUtil.ID_NO_PATTERN;
import static io.tomoto.util.RegexUtil.MONTH_PATTERN;
import static io.tomoto.util.RegexUtil.NO_PATTERN;
import static io.tomoto.util.RegexUtil.PASSWORD_PATTERN;
import static io.tomoto.util.RegexUtil.PHONE_PATTERN;

/**
 * Service class for administrator.
 * <p>
 * Can read / create / update / delete employees and read / create / update salaries.
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
        Employee employee = Instance.INSTANCE.employeeDao.read(view.getUserId());
        if (employee == null || !employee.getAdmin()) { // if the employee is not an administrator
//            logger.warn("Employee with id: " + view.getId() + " is not an administrator.");
            view.showHint("Employee with id: " + view.getUserId() + " do not exists or isn't an administrator");
            return null;
        }
        Instance.INSTANCE.view = view;
        Instance.INSTANCE.adminId = view.getUserId();
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
        if (!employeeReasonabilityCheck(employee)) {
            return false;
        }

        if (!employeeDao.create(employee)) { // if no employee was created
            view.showHint("账户创建失败！");
            return false;
        }
        view.showHint("账户创建成功！");
        return true;
    }

    /**
     * Checks legitimacy and uniqueness of some property in the employee.
     *
     * @param employee an employee for check
     * @return whether reasonable or not
     */
    private Boolean employeeReasonabilityCheck(Employee employee) {
        // legal check
        Map<Pair<Pattern, String>, String> helperHintMap = new HashMap<>();
        helperHintMap.put(new Pair<>(NO_PATTERN, employee.getNo()), "工号不合法！必须为e开头后跟三位数字。");
        helperHintMap.put(new Pair<>(ACCOUNT_PATTERN, employee.getAccount()), "账户名不合法！必须为六位任意字符并且不含空格。");
        helperHintMap.put(new Pair<>(PASSWORD_PATTERN, employee.getPassword()), "密码不合法！必须为六位任意字符并且不含空格。");
        helperHintMap.put(new Pair<>(ID_NO_PATTERN, employee.getIdNo()), "身份证号不合法！" + employee.getIdNo());
        helperHintMap.put(new Pair<>(EMAIL_PATTERN, employee.getEmail()), "邮箱不合法！" + employee.getEmail());
        helperHintMap.put(new Pair<>(PHONE_PATTERN, employee.getPhone()), "手机号不合法！" + employee.getPhone());
        for (Map.Entry<Pair<Pattern, String>, String> entry : helperHintMap.entrySet()) {
            Pair<Pattern, String> pair = entry.getKey();
            if (!pair.getKey().matcher(pair.getValue()).matches()) {
                view.showHint(entry.getValue());
                return false;
            }
        }

        // unique check
        Map<String, String> nameValueMap = new HashMap<>();
        nameValueMap.put("no", employee.getNo());
        nameValueMap.put("account", employee.getAccount());
        nameValueMap.put("idNo", employee.getIdNo());
        nameValueMap.put("phone", employee.getPhone());
        nameValueMap.put("email", employee.getEmail());
        for (Map.Entry<String, String> entry : nameValueMap.entrySet()) {
            String propertyName = entry.getKey();
            String value = entry.getValue();
            if (employeeDao.read(propertyName, value) != null) {
                view.showHint("该属性值已存在！" + propertyName + ": " + value);
                return false;
            }
        }

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
//        logger.info(adminId);
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
        Set<Employee> employees = readAllEmployees();
        if (NO_PATTERN.matcher(info).matches()) {
            employees.removeIf(employee -> !employee.getNo().equals(info));
        } else {
            employees.removeIf(employee -> !employee.getName().equals(info));
        }
        return employees;
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
     * Update an employee.
     *
     * @param id           the employee id
     * @param propertyName a propertry name
     * @param value        new value value
     * @param <T>          the value type
     * @return whether the update successful or not
     */
    public <T> Boolean updateEmployee(Integer id, String propertyName, T value) {
        if (!employeeDao.read(propertyName, value).isEmpty()) {
            view.showHint("更新员工失败！该属性值已存在！" + propertyName + ": " + value);
        }
        Boolean successful = employeeDao.update(adminId, id, propertyName, value);
        if (!successful) {
            view.showHint("更新员工失败！");
        } else {
            view.showHint("更新员工成功！");
        }
        return successful;
    }

    // Employee import and export

    /**
     * Import employees from the file.
     *
     * @param file the file for import
     */
    public void importEmployees(File file) {
        try (XSSFWorkbook excel = new XSSFWorkbook(new FileInputStream(file))) {
            Sheet sheet = excel.getSheetAt(0);
            int count = 0;
            for (Row cells : sheet) {
                Iterator<Cell> cellIterator = cells.iterator();
                Employee employee = new Employee()
                        .setNo(cellIterator.next().getStringCellValue())
                        .setAccount(cellIterator.next().getStringCellValue())
                        .setAdmin(cellIterator.next().getBooleanCellValue())
                        .setPassword(cellIterator.next().getStringCellValue())
                        .setName(cellIterator.next().getStringCellValue())
                        .setIdNo(cellIterator.next().getStringCellValue())
                        .setPhone(cellIterator.next().getStringCellValue())
                        .setEmail(cellIterator.next().getStringCellValue())
                        .setGender(cellIterator.next().getStringCellValue())
                        .setBirthday(Timestamp.valueOf(cellIterator.next().getStringCellValue() + " 00:00:00"))
                        .setCreateOperatorId(Double.valueOf(cellIterator.next().getNumericCellValue()).intValue())
                        .setUpdateOperatorId(Double.valueOf(cellIterator.next().getNumericCellValue()).intValue());
                if (!employeeReasonabilityCheck(employee)) {
                    break;
                }

                Boolean creationResult;
                if (cellIterator.hasNext()) {
                    employee
                            .setCreateTime(Timestamp.valueOf(cellIterator.next().getStringCellValue()))
                            .setUpdateTime(Timestamp.valueOf(cellIterator.next().getStringCellValue()));
                    creationResult = employeeDao.createWithTime(employee);
                } else {
                    creationResult = employeeDao.create(employee);
                }
                if (!creationResult) {
                    view.showHint("创建新员工失败！员工信息：" + employee);
                } else {
                    count++;
                }
            }
            view.showHint("成功导入了 " + count + "条新员工信息！");
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            Double actually, Double fin, String month) {
        if (!MONTH_PATTERN.matcher(month).matches()) {
//            logger.warn("Failed to create new salary because of wrong month format: " + month);
            view.showHint("创建工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return false;
        } else if (salaryDao.readAll().stream().anyMatch(salary ->
                salary.getEmployeeId().equals(employeeId) && // if the employee's
                        salary.getMonth().equals(month))) { // the month salary is already exists
//            logger.warn("Failed to create new salary because the salary for employee with id: " + employeeId + " of " +
//                    "month: " + month + " is already exists.");
            view.showHint("创建工资条失败！员工当月工资条已存在。");
            return false;
        }
        Salary salary = new Salary(
                employeeId,
                base, post, length, phone, traffic,
                tax, security, fund,
                actually, fin, month,
                adminId);
        if (!salaryDao.create(salary)) { // if no salary was created
//            logger.warn("Failed to create new salary: " + salary + " for some reason.");
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
     * @param info the employee name or No
     * @return a set of salary(ies)
     */
    public Set<Salary> readSalaryByInfo(String info) {
        // get employee(s)
        Set<Employee> employees = employeeDao.readAll();
        if /* info is an employee No */ (NO_PATTERN.matcher(info).matches()) {
            employees.removeIf(e -> !e.getNo().equals(info));
        } else /* if info is a name */ {
            employees.removeIf(e -> !e.getName().equals(info));
        }

        // get salary slip(s)
        HashSet<Salary> result = new HashSet<>();
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
    public Set<Salary> readSalaryByInfoFrom(String info, String month) {
        if (!MONTH_PATTERN.matcher(month).matches()) {
//            logger.warn("Failed to read salary(ies) because of wrong month format: " + month);
            view.showHint("读取工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return null;
        }

        // get employee(s)
        Set<Employee> employees = employeeDao.readAll();
        if /* info is an employee No */ (NO_PATTERN.matcher(info).matches()) {
            employees.removeIf(e -> !e.getNo().equals(info));
        } else /* if info is a name */ {
            employees.removeIf(e -> !e.getName().equals(info));
        }

        // get salary slip(s)
        Set<Salary> result = new HashSet<>();
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
    public Set<Salary> readSalaryByInfoFromTo(String info, String from, String to) {
        if (!MONTH_PATTERN.matcher(from).matches() || !MONTH_PATTERN.matcher(to).matches()) {
//            logger.warn("Failed to read salary(ies) because of wrong month format: " + from + " " + to);
            view.showHint("读取工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return null;
        }

        // get employee(s)
        Set<Employee> employees = employeeDao.readAll();
        if /* info is an employee No */ (NO_PATTERN.matcher(info).matches()) {
            employees.removeIf(e -> !e.getNo().equals(info));
        } else /* if info is a name */ {
            employees.removeIf(e -> !e.getName().equals(info));
        }

        // get salary slip(s)
        Set<Salary> result = new HashSet<>();
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
     * Reads salary(ies) by given month.
     *
     * @param month the month of salary
     * @return a set of salary(ies)
     */
    public Set<Salary> readSalaryByMonth(String month) {
        if (!MONTH_PATTERN.matcher(month).matches()) {
//            logger.warn("Failed to read salary(ies) because of wrong month format: " + month);
            view.showHint("读取工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return null;
        }

        // get salary slip(s)
        Set<Salary> salaries = salaryDao.readAll();
        salaries.removeIf(salary -> !salary.getMonth().equals(month));
        return salaries;
    }

    /**
     * Reads salary(ies) by given month period.
     *
     * @param from the begin month of salary
     * @param to   the end month of salary
     * @return a set of salary(ies)
     */
    public Set<Salary> readSalaryByFromTo(String from, String to) {
        if (!MONTH_PATTERN.matcher(from).matches() || !MONTH_PATTERN.matcher(to).matches()) {
//            logger.warn("Failed to read salary(ies) because of wrong month format: " + from + " " + to);
            view.showHint("读取工资条失败！月份格式不合法，必须为：'yyyyMM'。");
            return null;
        }
        // get salary slip(s)
        Set<Salary> salaries = salaryDao.readAll();
        salaries.removeIf(salary -> salary.getMonth().compareTo(from) < 0 || salary.getMonth().compareTo(to) > 0);
        return salaries;
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
     * Match the actually 'read function'.
     *
     * @param info the employee name or account
     * @param from the begin month of salary, format as 'yyyyMM'
     * @param to   the end month of salary, format as 'yyyyMM'
     * @return a set of salary(ies)
     */
    public Set<Salary> readSalary(String info, String from, String to) {
        if (!info.isEmpty()) {
            if (!from.isEmpty()) {
                if (!to.isEmpty()) {
                    return readSalaryByInfoFromTo(info, from, to);
                } else {
                    return readSalaryByInfoFrom(info, from);
                }
            } else {
                return readSalaryByInfo(info);
            }
        } else {
            if (!from.isEmpty()) {
                if (!to.isEmpty()) {
                    return readSalaryByFromTo(from, to);
                } else {
                    return readSalaryByMonth(from);
                }
            } else if (!to.isEmpty()) {
                view.showHint("不能只填写结束月份！");
                return null;
            } else {
                return readAllSalaries();
            }
        }
    }

    /**
     * Update a salary slip.
     *
     * @param id           the salary id
     * @param propertyName an property name
     * @param property     new property value
     * @param <T>          the property type
     * @return whether the update successful or not
     */
    public <T> Boolean updateSalary(Integer id, String propertyName, T property) {
        Boolean successful = salaryDao.update(adminId, id, propertyName, property);
        if (!propertyName.equals("month")) { // update actually and fin at the same time if the updated wasn't 'month'
            Salary salary = salaryDao.read(id);
            Double actually = salary.getBase() + salary.getPost() + salary.getLength()
                    + salary.getPhone() + salary.getTraffic();
            Double fin = actually + salary.getTax() + salary.getSecurity() + salary.getFund();
            salaryDao.update(adminId, id, "actually", actually);
            salaryDao.update(adminId, id, "fin", fin);
        }
        if (!successful) {
            view.showHint("更新工资条失败！");
        } else {
            view.showHint("更新工资条成功！");
        }
        return successful;
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
