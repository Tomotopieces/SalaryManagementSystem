package io.tomoto.dao.entity;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Employee entity class.
 * @author Tomoto
 * <p>
 * 2020/11/4 14:10
 */
public class Employee {
    // account information
    private Integer id;
    private String no;
    private String account;
    private Boolean admin;
    private String password;
    // personal information
    private String name;
    private String idNo;
    private String phone;
    private String email;
    private String gender;
    private Timestamp birthday;
    // operator information
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer createOperatorId;
    private Integer updateOperatorId;

    public Employee() {
    }

    public Employee(Integer id, String no, String account, Boolean admin, String password,
                    String name, String idNo, String phone, String email, String gender, Timestamp birthday,
                    Timestamp createTime, Timestamp updateTime, Integer createOperatorId, Integer updateOperatorId) {
        this.id = id;
        this.no = no;
        this.account = account;
        this.admin = admin;
        this.password = password;
        this.name = name;
        this.idNo = idNo;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.birthday = birthday;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.createOperatorId = createOperatorId;
        this.updateOperatorId = updateOperatorId;
    }

    public Employee(String no, String account, Boolean admin, String password,
                    String name, String idNo, String phone, String email, String gender, String birthday,
                    Integer createOperatorId) {
        this.id = 0;
        this.no = no;
        this.account = account;
        this.admin = admin;
        this.password = password;
        this.name = name;
        this.idNo = idNo;
        this.phone = phone;
        this.email = email;
        this.gender = gender;
        this.birthday = Timestamp.valueOf(birthday + " 00:00:00");
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        this.createTime = now;
        this.updateTime = now;
        this.createOperatorId = createOperatorId;
        this.updateOperatorId = createOperatorId;
    }

    public Integer getId() {
        return id;
    }

    public Employee setId(Integer id) {
        this.id = id;
        return this;
    }

    public String getNo() {
        return no;
    }

    public Employee setNo(String no) {
        this.no = no;
        return this;
    }

    public String getAccount() {
        return account;
    }

    public Employee setAccount(String account) {
        this.account = account;
        return this;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public Employee setAdmin(Boolean admin) {
        this.admin = admin;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public Employee setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getName() {
        return name;
    }

    public Employee setName(String name) {
        this.name = name;
        return this;
    }

    public String getIdNo() {
        return idNo;
    }

    public Employee setIdNo(String idNo) {
        this.idNo = idNo;
        return this;
    }

    public String getPhone() {
        return phone;
    }

    public Employee setPhone(String phone) {
        this.phone = phone;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Employee setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getGender() {
        return gender;
    }

    public Employee setGender(String gender) {
        this.gender = gender;
        return this;
    }

    public Timestamp getBirthday() {
        return birthday;
    }

    public Employee setBirthday(Timestamp birthday) {
        this.birthday = birthday;
        return this;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Employee setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
        return this;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public Employee setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getCreateOperatorId() {
        return createOperatorId;
    }

    public Employee setCreateOperatorId(Integer createOperatorId) {
        this.createOperatorId = createOperatorId;
        return this;
    }

    public Integer getUpdateOperatorId() {
        return updateOperatorId;
    }

    public Employee setUpdateOperatorId(Integer updateOperatorId) {
        this.updateOperatorId = updateOperatorId;
        return this;
    }

    @Override
    public String toString() {
        return "Employee{" +
                "id=" + id +
                ", no='" + no + '\'' +
                ", account='" + account + '\'' +
                ", admin=" + admin +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", idNo='" + idNo + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", gender='" + gender + '\'' +
                ", birthday=" + birthday +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", createOperatorId=" + createOperatorId +
                ", updateOperatorId=" + updateOperatorId +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(id, employee.id) &&
                Objects.equals(no, employee.no) &&
                Objects.equals(account, employee.account) &&
                Objects.equals(admin, employee.admin) &&
                Objects.equals(password, employee.password) &&
                Objects.equals(name, employee.name) &&
                Objects.equals(idNo, employee.idNo) &&
                Objects.equals(phone, employee.phone) &&
                Objects.equals(email, employee.email) &&
                Objects.equals(gender, employee.gender) &&
                Objects.equals(birthday, employee.birthday) &&
                Objects.equals(createTime, employee.createTime) &&
                Objects.equals(updateTime, employee.updateTime) &&
                Objects.equals(createOperatorId, employee.createOperatorId) &&
                Objects.equals(updateOperatorId, employee.updateOperatorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, no, account, admin, password,
                name, idNo, phone, email, gender, birthday,
                createTime, updateTime, createOperatorId, updateOperatorId);
    }
}
