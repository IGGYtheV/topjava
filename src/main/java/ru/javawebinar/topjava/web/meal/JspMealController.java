package ru.javawebinar.topjava.web.meal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.service.MealService;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalDate;
import static ru.javawebinar.topjava.util.DateTimeUtil.parseLocalTime;

@Controller
public class JspMealController extends AbstractMealController {

    public JspMealController(MealService service) {
        super(service);
    }

    @GetMapping("meals/delete")
    public String doDelete(@RequestParam("mealId") int id) {
        super.delete(id);
        return "redirect:/meals";
    }

    @GetMapping("meals/showMealForm")
    public String updateForm(@RequestParam("mealId") int id, Model model) {
        Meal meal = super.get(id);
        model.addAttribute(meal);
        return "mealForm";
    }

    @GetMapping("meals/create")
    public String createForm(Model model) {
        Meal meal = new Meal();
        model.addAttribute("meal", meal);
        model.addAttribute("create", "create");
        return "mealForm";
    }

    @PostMapping("/meals")
    public String save(HttpServletRequest request) throws UnsupportedEncodingException {
        Meal meal = new Meal(
                LocalDateTime.parse(request.getParameter("dateTime")),
                request.getParameter("description"),
                Integer.parseInt(request.getParameter("calories")));

        if (StringUtils.hasLength(request.getParameter("id"))) {
            meal.setId(Integer.parseInt(request.getParameter("id")));
        } else {
            meal.setId(null);
        }

        if (meal.isNew()) {
            super.create(meal);
        } else {
            super.update(meal, meal.id());
        }
        return "redirect:/meals";
    }

    @GetMapping("/meals")
    public String doGetAll(Model model) {
        model.addAttribute("meals", super.getAll());
        return "meals";
    }

    @GetMapping("/meals/between")
    public String doGetBetween(HttpServletRequest request, Model model) {
        LocalDate startDate = parseLocalDate(request.getParameter("startDate"));
        LocalDate endDate = parseLocalDate(request.getParameter("endDate"));
        LocalTime startTime = parseLocalTime(request.getParameter("startTime"));
        LocalTime endTime = parseLocalTime(request.getParameter("endTime"));
        model.addAttribute("meals", super.getBetween(startDate, startTime, endDate, endTime));
        return "meals";
    }
}