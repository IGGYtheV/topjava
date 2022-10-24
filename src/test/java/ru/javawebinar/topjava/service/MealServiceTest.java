package ru.javawebinar.topjava.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit4.SpringRunner;
import ru.javawebinar.topjava.MealTestData;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.exception.NotFoundException;

import java.util.List;

import static org.junit.Assert.*;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.GUEST_ID;
import static ru.javawebinar.topjava.UserTestData.USER_ID;

@ContextConfiguration({
        "classpath:spring/spring-app.xml",
        "classpath:spring/spring-db.xml"
})
@RunWith(SpringRunner.class)
@Sql(scripts = "classpath:db/populateDB.sql", config = @SqlConfig(encoding = "UTF-8"))

public class MealServiceTest {

    static {
        SLF4JBridgeHandler.install();
    }

    @Autowired
    private MealService service;

    @Test
    public void get() {
        Meal meal = service.get(MEAL_ID1, USER_ID);
        assertMatch(meal, MealTestData.meal1);
    }
    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID4, USER_ID));
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID1, GUEST_ID));
    }

    @Test
    public void delete() {
        service.delete(MEAL_ID1, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(MEAL_ID1, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(MEAL_ID4, USER_ID));
        assertThrows(NotFoundException.class, () -> service.delete(MEAL_ID1, GUEST_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> created = service.getBetweenInclusive(meal3.getDate(), meal3.getDate(), USER_ID);
        assertMatch(created, meal3);
    }

    @Test
    public void getAll() {
        List<Meal> created = service.getAll(USER_ID);
        assertMatch(created, meal3, meal2, meal1);
    }

    @Test
    public void update() {
        Meal updated = getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(MEAL_ID1, USER_ID), getUpdated());
    }

    @Test
    public void updatedMealNotFound() {
        Meal test = new Meal(MEAL_ID4, meal1.getDateTime(), "change description", meal1.getCalories());
        assertThrows(NotFoundException.class, () -> service.update(test, USER_ID));
    }

    @Test
    public void updatingUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(getUpdated(), GUEST_ID));
    }

    @Test
    public void create() {
        Meal created = service.create(getNew(), USER_ID);
        Integer createdId = created.getId();
        Meal expected = getNew();
        expected.setId(createdId);
        assertMatch(created, expected);
        assertMatch(service.get(createdId, USER_ID), expected);
    }

    @Test
    public void duplicateDateCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, meal2.getDateTime(), "test", 888), USER_ID));
    }
}