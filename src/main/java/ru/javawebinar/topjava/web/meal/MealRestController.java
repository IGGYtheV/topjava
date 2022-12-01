package ru.javawebinar.topjava.web.meal;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.javawebinar.topjava.model.Meal;
import ru.javawebinar.topjava.to.MealTo;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping(value = MealRestController.REST_URL, produces = MediaType.APPLICATION_JSON_VALUE)
public class MealRestController extends AbstractMealController {

    static final String REST_URL = "/rest/meals";

    @Override
    @GetMapping
    public List<MealTo> getAll() {
        return super.getAll();
    }

    @Override
    @GetMapping("/{id}")
    public Meal get(@PathVariable int id) {
        return super.get(id);
    }

    @Override
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable int id) {
        super.delete(id);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Meal> createWithLocation(@RequestBody Meal meal) {
        Meal created = super.create(meal);
        URI newResourceUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId()).toUri();
        return ResponseEntity.created(newResourceUri).body(created);
    }

    @Override
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@RequestBody Meal meal, @PathVariable int id) {
        super.update(meal, id);
    }



    @GetMapping("/filter")
    public List<MealTo> getBetween(@DateTimeFormat @Nullable LocalDate startDate,
                                   @Nullable LocalTime startTime,
                                   @Nullable LocalDate endDate,
                                   @Nullable LocalTime endTime) {


//    public List<MealTo> getBetween(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("startDateTime") LocalDateTime startDateTime,
//                                   @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) @RequestParam("endDateTime") LocalDateTime endDateTime) {
//        LocalDate startDate = startDateTime.toLocalDate();
//        LocalTime startTime = startDateTime.toLocalTime();
//        LocalDate endDate = endDateTime.toLocalDate();
//        LocalTime endTime = endDateTime.toLocalTime();
        return super.getBetween(startDate, startTime, endDate, endTime);
    }
}