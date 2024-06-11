package com.eric.employees;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Manager extends Employee {
    private int orgSize = 0;
    private int directReports = 0;
    private final String mgrRegex = "\\w+=(?<orgSize>\\w+),\\w+=(?<dr>\\w+)";
    private final Pattern mgrPat = Pattern.compile(mgrRegex);

    public Manager(String personText) {
        super(personText);
        Matcher mgrMat = mgrPat.matcher(peopleMat.group("details"));
        if(mgrMat.find()) {
            this.orgSize = Integer.parseInt(mgrMat.group("orgSize"));
            this.directReports = Integer.parseInt(mgrMat.group("dr"));
        }
    }

    public int getSalary() {
        return 3500 + orgSize + directReports;
    }
}
