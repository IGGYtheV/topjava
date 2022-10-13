package ru.javawebinar.topjava.model;

import java.time.LocalDateTime;
import java.util.Objects;


public class MealTo {
    private final Integer id;
    private final LocalDateTime dateTime;
    private final String description;
    private final int calories;
    public static final int CALORIES_PER_DAY = 2000;
    private boolean excess;

    public MealTo(Integer id, LocalDateTime dateTime, String description, int calories, boolean excess) {
        this.dateTime = dateTime;
        this.id = id;
        this.description = description;
        this.calories = calories;
        this.excess = excess;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public boolean isExcess() {
        return excess;
    }

    public void setExcess(boolean excess) {
        this.excess = excess;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MealTo mealTo = (MealTo) o;
        return calories == mealTo.calories && excess == mealTo.excess && Objects.equals(id, mealTo.id) && dateTime.equals(mealTo.dateTime) && Objects.equals(description, mealTo.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dateTime, description, calories, excess);
    }

    @Override
    public String toString() {
        return "MealTo{" +
                "dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                ", excess=" + excess +
                '}';
    }
}
