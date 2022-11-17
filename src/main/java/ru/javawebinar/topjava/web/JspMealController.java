package ru.javawebinar.topjava.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;
import ru.javawebinar.topjava.util.MealsUtil;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
public class JspMealController {
    private final MealService service;

    public JspMealController(MealService service) {
        this.service = service;
    }

    @GetMapping("delete&id={id}")
    public String deleteMeal(@PathVariable int id) {
        int userId = SecurityUtil.authUserId();
        service.delete(id, userId);
        return "redirect:/meals";
    }

    @GetMapping("/showMealForm")
    public String updateMapper(@RequestParam("mealId") int id, Model model) {
        int userId = SecurityUtil.authUserId();
        Meal meal = service.get(id, userId);
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @GetMapping("/create")
    public String createMapper(Model model) {
        Meal meal = new Meal();
        model.addAttribute("meal", meal);
        return "mealForm";
    }

    @PostMapping("/meals")
    public String doUpdateCreate(HttpServletRequest request) throws UnsupportedEncodingException {
        int userId = SecurityUtil.authUserId();
        request.setCharacterEncoding("UTF-8");
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));
        if (StringUtils.hasLength(request.getParameter("id"))) {
            meal.setId(Integer.parseInt(request.getParameter("id")));
        } else  meal.setId(null);

        if (meal.isNew()) {
            service.create(meal, userId);
        } else {
            service.update(meal, userId);
        }
        return "redirect:/meals";
    }

    @GetMapping("/meals")
    public String getAll(Model model) {
        int userId = SecurityUtil.authUserId();
        model.addAttribute("meals", MealsUtil.getTos(service.getAll(userId), SecurityUtil.authUserCaloriesPerDay()));
        return "meals";
    }

    @GetMapping("/getAllWithinTime")
    public String getAllWithinTime(HttpServletRequest request, Model model) {
        int userId = SecurityUtil.authUserId();
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        List<Meal> list = service.getBetweenInclusive(startDate, endDate, userId );
        model.addAttribute("meals", MealsUtil.getFilteredTos(list, SecurityUtil.authUserCaloriesPerDay(), startTime, endTime));
        return "meals";
    }
}