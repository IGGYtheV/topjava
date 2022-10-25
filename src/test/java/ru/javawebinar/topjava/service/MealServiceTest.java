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

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;
import static ru.javawebinar.topjava.MealTestData.assertMatch;
import static ru.javawebinar.topjava.MealTestData.*;
import static ru.javawebinar.topjava.UserTestData.*;

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
        Meal meal = service.get(USER_MEAL_1_ID, USER_ID);
        assertMatch(meal, MealTestData.USER_MEAL_1);
    }

    @Test
    public void getNotFound() {
        assertThrows(NotFoundException.class, () -> service.get(NOT_EXISTING_MEAL_ID, USER_ID));
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_1_ID, ADMIN_ID));
    }

    @Test
    public void delete() {
        service.delete(USER_MEAL_1_ID, USER_ID);
        assertThrows(NotFoundException.class, () -> service.get(USER_MEAL_1_ID, USER_ID));
    }

    @Test
    public void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> service.delete(NOT_EXISTING_MEAL_ID, USER_ID));
        assertThrows(NotFoundException.class, () -> service.delete(USER_MEAL_1_ID, ADMIN_ID));
    }

    @Test
    public void getBetweenInclusive() {
        List<Meal> created = service.getBetweenInclusive(USER_MEAL_3.getDate(), USER_MEAL_5.getDate(), USER_ID);
        assertMatch(created, USER_MEAL_5, USER_MEAL_4, USER_MEAL_3);
    }

    @Test
    public void getAll() {
        List<Meal> created = service.getAll(USER_ID);
        assertMatch(created, USER_MEAL_5, USER_MEAL_4, USER_MEAL_3, USER_MEAL_2, USER_MEAL_1);
    }

    @Test
    public void getAllNotFound() {
        List<Meal> created = service.getAll(ADMIN_ID);
        assertMatch(created, Collections.emptyList());
    }

    @Test
    public void update() {
        Meal updated = MealTestData.getUpdated();
        service.update(updated, USER_ID);
        assertMatch(service.get(USER_MEAL_1_ID, USER_ID), MealTestData.getUpdated());
    }

    @Test
    public void updatedMealNotFound() {
        Meal test = new Meal(NOT_EXISTING_MEAL_ID, USER_MEAL_1.getDateTime(), "change description", USER_MEAL_1.getCalories());
        assertThrows(NotFoundException.class, () -> service.update(test, USER_ID));
    }

    @Test
    public void updatingUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.update(MealTestData.getUpdated(), NOT_FOUND));
    }

    @Test
    public void create() {
        Meal created = service.create(MealTestData.getNew(), ADMIN_ID);
        Integer createdId = created.getId();
        Meal expected = MealTestData.getNew();
        expected.setId(createdId);
        assertMatch(created, expected);
        assertMatch(service.get(createdId, ADMIN_ID), expected);
    }

    @Test
    public void duplicateDateCreate() {
        assertThrows(DataAccessException.class, () ->
                service.create(new Meal(null, USER_MEAL_1.getDateTime(), "test", 888), USER_ID));
    }

    @Test()
    public void differentUsersSameDateCreate() {
        service.create(new Meal(USER_MEAL_1.getDateTime(), USER_MEAL_1.getDescription(), USER_MEAL_1.getCalories()), ADMIN_ID);
    }
}