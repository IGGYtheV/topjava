package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public interface Storage {

    void update(MealTo mealTo);

    void create(MealTo mealTo);

    MealTo get(Integer id);

    void delete(Integer id);

    List<MealTo> getAll();
}
