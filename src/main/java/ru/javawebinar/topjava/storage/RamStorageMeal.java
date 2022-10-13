package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;
import ru.javawebinar.topjava.util.RamStorageMealInitializer;

import java.util.*;

public class RamStorageMeal implements Storage {
    Map<Integer, Meal> linkedMap;

    public RamStorageMeal(Map<Integer, Meal> linkedMap) {
        this.linkedMap = linkedMap;
    }


    @Override
    public void create(Meal mealTo) {

    }

    @Override
    public void update(Meal mealTo) {

    }

    @Override
    public MealTo get(int id) {
        return null;
    }

    @Override
    public void delete(int id) {
        linkedMap.remove(id);
    }

    @Override
    public List<MealTo> getAll() {
        return RamStorageMealInitializer.createMealsTo(new ArrayList<>(linkedMap.values()));
    }
}
