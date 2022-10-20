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
import java.util.stream.Stream;

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
        log.info("save meal {} for user with id {}", meal, userId);

        checkIfUserExistsAndCreateSubMapIfNot(userId);

        if (!repository.get(userId).isEmpty()) {
            return meal.isNew() ? setIdAndPutMealIntoRepository(meal, userId)
                    : updateForExistingUser(meal, userId);
        } else {
            return meal.isNew() ? setIdAndPutMealIntoRepository(meal, userId) : null;
        }
    }

    private synchronized void checkIfUserExistsAndCreateSubMapIfNot(int userId) {
       if( repository.get(userId) == null ) {
           repository.put(userId, new ConcurrentHashMap<>());
       }
    }

    private Meal updateForExistingUser(Meal meal, int userId) {
        return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
    }

    private Meal setIdAndPutMealIntoRepository(Meal meal, int userId) {
        meal.setId(counter.incrementAndGet());
        repository.get(userId).put(meal.getId(), meal);
        return meal;
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("delete meal {} for user {}", id, userId);
        return repository.get(userId) != null && repository.get(userId).remove(id) != null;
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("get meal {} for user {}", id, userId);
        return repository.get(userId) == null ? null : repository.get(userId).get(id);
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll for user with id {}", userId);
        return repository.get(userId) == null ? Collections.emptyList() :
                Optional.of(repository.get(userId).values())
                        .map(Collection::stream)
                        .orElseGet(Stream::empty)
                        .sorted(Comparator.comparing(Meal::getDateTime).reversed())
                        .collect(Collectors.toList());
    }
}

