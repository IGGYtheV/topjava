package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.MealTo;

import java.util.List;

public interface Storage {
    void clear();

    void update(MealTo mealTo);

    void save(MealTo mealTo);

    MealTo get(Integer uuid);

    void delete(Integer id);

    List<MealTo> getAll();
}
