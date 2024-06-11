package com.eric.oop;

public class Exercise1 {
    enum DayOfWeek {
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
        SUNDAY;
    }

    public static void main(String[] args) {
        for(DayOfWeek day : DayOfWeek.values()) {
            System.out.println(day);
        }
        System.out.println("Ex1.1");
        for(DayOfWeek day : DayOfWeek.values()) {
            String dayText = day.toString().toLowerCase();
            System.out.println(dayText.substring(0,1).toUpperCase() + dayText.substring(1));
        }
        System.out.println("Ex1.2");
        for(DayOfWeek day : DayOfWeek.values()) {
            String dayText = day.toString().toLowerCase();
            int middle = dayText.length() / 2;
            System.out.println(dayText.substring(0,middle) + dayText.substring(middle,middle+1).toUpperCase() + dayText.substring(middle+1));
        }
    }
}
