package com.eric.employees;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Programmer extends Employee implements Chef{
    private int linesOfCode = 0;
    private int yearsOfExp = 0;
    private int iq = 0;
    private final String progRegex = "\\w+\\=(?<locpd>\\w+)\\,\\w+\\=(?<yoe>\\w+)\\,\\w+\\=(?<iq>\\w+)";
    private final Pattern progPat = Pattern.compile(progRegex);

    public Programmer(String personText) {
        super(personText);
        Matcher progMat = progPat.matcher(peopleMat.group("details"));
        if(progMat.find()) {
            this.linesOfCode = Integer.parseInt(progMat.group("locpd"));
            this.yearsOfExp = Integer.parseInt(progMat.group("yoe"));
            this.iq = Integer.parseInt(progMat.group("iq"));
        }
    }

    @Override
    public int getSalary() {
        return 3000 + linesOfCode + yearsOfExp + iq;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Programmer that = (Programmer) o;
        return iq == that.iq;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), iq);
    }
}
