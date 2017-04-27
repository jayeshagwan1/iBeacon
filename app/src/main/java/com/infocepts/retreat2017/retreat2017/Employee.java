package com.infocepts.retreat2017.retreat2017;

import java.util.Date;

/**
 * Created by jjagwan on 31-01-2017.
 */

public class Employee {
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    private String employeeName;
    private String employeeId;

    public Date getDatetimestamp() {
        return datetimestamp;
    }

    public void setDatetimestamp(Date datetimestamp) {
        this.datetimestamp = datetimestamp;
    }

    private Date datetimestamp;
}
