package com.eric.employees;

public interface IEmployee extends Comparable<IEmployee> {
    int getSalary();
    int compareTo(IEmployee o);
}
