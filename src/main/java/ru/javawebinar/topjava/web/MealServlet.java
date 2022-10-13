package ru.javawebinar.topjava.web;

import org.slf4j.Logger;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.util.RamStorageMealInitializer;
import ru.javawebinar.topjava.storage.RamStorageMeal;
import ru.javawebinar.topjava.storage.Storage;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

public class MealServlet extends HttpServlet {
    private static final Logger log = getLogger(MealServlet.class);
    //    List<Meal> meals = RamStorageMealInitializer.createMeal();
    Map<Integer, Meal> meals = RamStorageMealInitializer.createMeal();
    private Storage storage = new RamStorageMeal(meals);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        log.debug("redirect to meals");
        String id = request.getParameter("id");
        String action = request.getParameter("action");
        if (action == null) {
            request.setAttribute("mealsTo", storage.getAll());
            request.getRequestDispatcher("meals.jsp").forward(request, response);
            return;
        }
        switch (action) {
            case "delete":
                storage.delete(Integer.parseInt(id));
                response.sendRedirect("meals");
                return;
            default:
                throw new IllegalArgumentException("Action " + action + " is illegal");
        }
    }
}
