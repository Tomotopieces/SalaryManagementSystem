package io.tomoto.dao.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Salary entity class.
 *
 * @author Tomoto
 * <p>
 * 2020/11/4 15:53
 */
public class Salary {
    // salary slip id
    private Integer id;
    private Integer employeeId;
    // salary part
    private Double base;
    private Double post;
    private Double length;
    private Double phone;
    private Double traffic;
    // cost
    private Double tax;
    private Double security;
    private Double fund;
    // actually salary
    private Double actually;
    private String month;
    // operator information
    private Timestamp createTime;
    private Timestamp updateTime;
    private Integer createOperatorId;
    private Integer updateOperatorId;

    public Salary() {
    }

    public Salary(Integer id,
                  Integer employeeId, Double base, Double post, Double length, Double phone, Double traffic,
                  Double tax, Double security, Double fund,
                  Double actually, String month,
                  Timestamp createTime, Timestamp updateTime, Integer createOperatorId, Integer updateOperatorId) {
        this.id = id;
        this.employeeId = employeeId;
        this.base = base;
        this.post = post;
        this.length = length;
        this.phone = phone;
        this.traffic = traffic;
        this.tax = tax;
        this.security = security;
        this.fund = fund;
        this.actually = actually;
        this.month = month;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.createOperatorId = createOperatorId;
        this.updateOperatorId = updateOperatorId;
    }

    public Salary(Integer employeeId, Double base, Double post, Double length, Double phone, Double traffic,
                  Double tax, Double security, Double fund,
                  Double actually, String month,
                  Integer createOperatorId) {
        this.employeeId = employeeId;
        this.id = 0;
        this.base = base;
        this.post = post;
        this.length = length;
        this.phone = phone;
        this.traffic = traffic;
        this.tax = tax;
        this.security = security;
        this.fund = fund;
        this.actually = actually;
        this.month = month;
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        this.createTime = now;
        this.updateTime = now;
        this.createOperatorId = createOperatorId;
        this.updateOperatorId = createOperatorId;
    }

    public Integer getId() {
        return id;
    }

    public Salary setId(Integer id) {
        this.id = id;
        return this;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public Salary setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
        return this;
    }

    public Double getBase() {
        return base;
    }

    public Salary setBase(Double base) {
        this.base = base;
        return this;
    }

    public Double getPost() {
        return post;
    }

    public Salary setPost(Double post) {
        this.post = post;
        return this;
    }

    public Double getLength() {
        return length;
    }

    public Salary setLength(Double length) {
        this.length = length;
        return this;
    }

    public Double getPhone() {
        return phone;
    }

    public Salary setPhone(Double phone) {
        this.phone = phone;
        return this;
    }

    public Double getTraffic() {
        return traffic;
    }

    public Salary setTraffic(Double traffic) {
        this.traffic = traffic;
        return this;
    }

    public Double getTax() {
        return tax;
    }

    public Salary setTax(Double tax) {
        this.tax = tax;
        return this;
    }

    public Double getSecurity() {
        return security;
    }

    public Salary setSecurity(Double security) {
        this.security = security;
        return this;
    }

    public Double getFund() {
        return fund;
    }

    public Salary setFund(Double fund) {
        this.fund = fund;
        return this;
    }

    public Double getActually() {
        return actually;
    }

    public Salary setActually(Double actually) {
        this.actually = actually;
        return this;
    }

    public String getMonth() {
        return month;
    }

    public Salary setMonth(String month) {
        this.month = month;
        return this;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public Salary setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
        return this;
    }

    public Timestamp getUpdateTime() {
        return updateTime;
    }

    public Salary setUpdateTime(Timestamp updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    public Integer getCreateOperatorId() {
        return createOperatorId;
    }

    public Salary setCreateOperatorId(Integer createOperatorId) {
        this.createOperatorId = createOperatorId;
        return this;
    }

    public Integer getUpdateOperatorId() {
        return updateOperatorId;
    }

    public Salary setUpdateOperatorId(Integer updateOperatorId) {
        this.updateOperatorId = updateOperatorId;
        return this;
    }

    @Override
    public String toString() {
        return "Salary{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", base=" + base +
                ", post=" + post +
                ", length=" + length +
                ", phone=" + phone +
                ", traffic=" + traffic +
                ", tax=" + tax +
                ", security=" + security +
                ", fund=" + fund +
                ", actually=" + actually +
                ", month='" + month + '\'' +
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
        Salary salary = (Salary) o;
        return Objects.equals(id, salary.id) &&
                Objects.equals(employeeId, salary.employeeId) &&
                Objects.equals(base, salary.base) &&
                Objects.equals(post, salary.post) &&
                Objects.equals(length, salary.length) &&
                Objects.equals(phone, salary.phone) &&
                Objects.equals(traffic, salary.traffic) &&
                Objects.equals(tax, salary.tax) &&
                Objects.equals(security, salary.security) &&
                Objects.equals(fund, salary.fund) &&
                Objects.equals(actually, salary.actually) &&
                Objects.equals(month, salary.month) &&
                Objects.equals(createTime, salary.createTime) &&
                Objects.equals(updateTime, salary.updateTime) &&
                Objects.equals(createOperatorId, salary.createOperatorId) &&
                Objects.equals(updateOperatorId, salary.updateOperatorId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeeId, base, post, length, phone, traffic, tax, security, fund, actually, month, createTime, updateTime, createOperatorId, updateOperatorId);
    }
}
