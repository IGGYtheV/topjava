package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            save(meal, 1);
        }
        for (Meal meal : MealsUtil.meals2) {
            save(meal, 2);
        }
    }

    @Override
    public Meal save(Meal meal, int userId) {
        log.info("save {} for user with id {}", meal, userId);
        if (repository.containsKey(userId)) {
            if (meal.isNew()) {
                return setIdAndPutMealIntoRepository(meal, userId);
            }
            return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
        } else {
            repository.put(userId, new ConcurrentHashMap<>());
            if (meal.isNew()) {
                return setIdAndPutMealIntoRepository(meal, userId);
            } else {
                log.debug("Update: meal with id" + meal.getId() + " doesn't belong to user " + userId);
                return null;
            }
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("delete {} for user {}", id, userId);
        return repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("get {} for user {}", id, userId);
        return repository.get(userId).get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll for user with id {}", userId);
        return repository.get(userId) == null ? Collections.emptyList() :
                repository.get(userId).values().stream()
                        .sorted(Comparator.comparing(Meal::getDate).thenComparing(Meal::getTime).reversed())
                        .collect(Collectors.toList());
    }

    private Meal setIdAndPutMealIntoRepository(Meal meal, int userId) {
        meal.setId(counter.incrementAndGet());
        repository.get(userId).put(meal.getId(), meal);
        return meal;
    }
}

