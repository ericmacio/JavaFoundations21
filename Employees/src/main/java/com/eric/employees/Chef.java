package com.eric.employees;

public interface Chef {

    String favoriteFood = "Hamburger";

    default void cook(String food) {
        System.out.println("I'm now cooking " + food);
    }
    
    default String cleanup() {
        return "I'm done cleaning up";
    }

    default String getFavoriteFood() {
        return favoriteFood;
    }

}
