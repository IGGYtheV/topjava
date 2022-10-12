package ru.javawebinar.topjava.storage;

import ru.javawebinar.topjava.model.MealTo;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

public class RamStorageMeal implements Storage {

    List<MealTo> list = new CopyOnWriteArrayList<>();
    private static final AtomicInteger count = new AtomicInteger(0);


    public RamStorageMeal(List<MealTo> list) {
        this.list = list;
    }


    @Override
    public void update(MealTo mealTo) {

    }

    @Override
    public void save(MealTo mealTo) {

    }

    @Override
    public MealTo get(Integer id) {
        return null;
    }

    @Override
    public void delete(Integer id) {
        list.remove(getIndex(id));
    }

    @Override
    public List<MealTo> getAll() {
        return list;
    }

    private int getIndex(int searchID) {
        return IntStream.range(0, list.size())
                .filter(i -> list.get(i).getId() == searchID)
                .findFirst()
                .orElse(-1);
    }

}
