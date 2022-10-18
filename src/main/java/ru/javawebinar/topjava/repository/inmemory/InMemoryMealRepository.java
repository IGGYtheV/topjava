package ru.javawebinar.topjava.repository.inmemory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.repository.MealRepository;
import ru.javawebinar.topjava.util.MealsUtil;
import ru.javawebinar.topjava.web.SecurityUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Repository
public class InMemoryMealRepository implements MealRepository {
    private static final Logger log = LoggerFactory.getLogger(InMemoryMealRepository.class);

    private final Map<Integer, Meal> repositoryWithoutUserId = new ConcurrentHashMap<>();
    private final Map<Integer, Map<Integer, Meal>> repository = new ConcurrentHashMap<>();
    private final AtomicInteger counter = new AtomicInteger(0);

    {
        for (Meal meal : MealsUtil.meals) {
            save(meal, SecurityUtil.authUserId());
        }
    }

    @Override
    public Meal save(Meal meal, int userId) {
        log.info("save {}", meal);
        if (repository.containsKey(userId)) {
            if (meal.isNew()) {
                return getMeal(meal, userId);
            }
            return repository.get(userId).computeIfPresent(meal.getId(), (id, oldMeal) -> meal);
            // handle case: update, but not present in storage
        } else {
            if (meal.isNew()) {
                return getMeal(meal, userId);
            } else {
                log.debug("Update: meal with id" + meal.getId() + " doesn't belong to user " + userId);
                return null;
            }
        }
    }

    @Override
    public boolean delete(int id, int userId) {
        log.info("delete {}", id);
        if (repository.containsKey(userId)) {
            return repository.get(userId).remove(id) != null;
        } else {
            log.debug("Delete meal with id " + id + ". There is no user with id " + userId + " in storage.");
            return false;
        }
    }

    @Override
    public Meal get(int id, int userId) {
        log.info("get {}", id);
        if (repository.containsKey(userId)) {
            return repository.get(userId).get(id);
        } else {
            log.debug("Get meal with id " + id + ". There is no user with id " + userId + " in storage.");
            return null;
        }
    }

    @Override
    public List<Meal> getAll(int userId) {
        log.info("getAll");
        if (repository.containsKey(userId)) {
            Collection<Meal> list =  repository.get(userId).values();
            return list.stream()
                    .sorted(Comparator.comparing(Meal::getDate).thenComparing(Meal::getTime).reversed())
                    .collect(Collectors.toList());
        } else {
            log.debug("getAll: " + ". There is no user with id " + userId + " in storage.");
            return Collections.emptyList();
        }
    }

    private Meal getMeal(Meal meal, int userId) {
        meal.setId(counter.incrementAndGet());
        repositoryWithoutUserId.put(meal.getId(), meal);
        repository.put(userId, repositoryWithoutUserId);
        return meal;
    }
}

