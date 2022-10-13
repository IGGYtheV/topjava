package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.model.MealTo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @see <a href="http://topjava.herokuapp.com">Demo application</a>
 * @see <a href="https://github.com/JavaOPs/topjava">Initial project</a>
 */
public class RamStorageMealInitializer {
    private static final AtomicInteger count = new AtomicInteger(0);

    public static Map<Integer, Meal> createMeal() {
        Map<Integer, Meal> map = new LinkedHashMap<>();
        map.put(count.incrementAndGet(), new Meal(count.intValue(),
                LocalDateTime.of(2020, Month.JANUARY, 30, 10, 0),
                "Завтрак", 500)
        );
        map.put(count.incrementAndGet(), new Meal(count.intValue(),
                LocalDateTime.of(2020, Month.JANUARY, 30, 13, 0), "Обед", 1000)
        );
        map.put(count.incrementAndGet(), new Meal(count.intValue(),
                LocalDateTime.of(2020, Month.JANUARY, 30, 20, 0), "Ужин", 500)
        );
        map.put(count.incrementAndGet(), new Meal(count.intValue(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 0, 0), "Еда на граничное значение", 100)

        );
        map.put(count.incrementAndGet(), new Meal(count.intValue(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 10, 0), "Завтрак", 1000)

        );
        map.put(count.incrementAndGet(), new Meal(count.intValue(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 13, 0), "Обед", 500)

        );
        map.put(count.incrementAndGet(), new Meal(count.intValue(),
                LocalDateTime.of(2020, Month.JANUARY, 31, 20, 0), "Ужин", 410)

        );
        return map;
    }

    public static List<MealTo> createMealsTo(List<Meal> meals) {
        Map<LocalDate, Integer> caloriesSumByDate = meals.stream()
                .collect(
                        Collectors.groupingBy(Meal::getDate, Collectors.summingInt(Meal::getCalories))
//                                              Collectors.toMap(Meal::getDate, Meal::getCalories, Integer::sum)
                );

        return meals.stream()
                .map(meal -> createTo(meal, caloriesSumByDate.get(meal.getDate()) > MealTo.CALORIES_PER_DAY))
                .collect(Collectors.toList());
    }

    private static MealTo createTo(Meal meal, boolean excess) {
        return new MealTo(meal.getId(), meal.getDateTime(), meal.getDescription(), meal.getCalories(), excess);
    }
}
