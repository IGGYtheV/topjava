package ru.javawebinar.topjava.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "meals", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "date_time"})})
@NamedQueries(value = {
        @NamedQuery(name = Meal.DELETE_MEAL, query = "DELETE FROM Meal m WHERE m.id =:id AND m.user.id =:userId"),
        @NamedQuery(name = Meal.GET_MEAL, query = "SELECT m FROM Meal m WHERE m.id =:id AND m.user.id =:userId"),
        @NamedQuery(name = Meal.GET_ALL_MEAL, query = "SELECT m FROM Meal m WHERE m.user.id =:userId ORDER BY m.dateTime DESC"),
        @NamedQuery(name = Meal.GET_BETWEEN_TIME_MEAL, query = "SELECT m FROM Meal m WHERE m.user.id =:userId " +
                "AND m.dateTime >=:startTime AND m.dateTime <:endTime ORDER BY m.dateTime DESC")
})
public class Meal extends AbstractBaseEntity {

    public static final String DELETE_MEAL = "Meal.delete";
    public static final String GET_MEAL = "Meal.get";
    public static final String GET_ALL_MEAL = "Meal.getAll";
    public static final String GET_BETWEEN_TIME_MEAL = "Meal.getBetweenHalfOpen";

    @Column(name = "date_time")
    @NotNull
    private LocalDateTime dateTime;

    @Column(name = "description", nullable = false)
    @NotBlank
    private String description;

    @Column(name = "calories", nullable = false)
    @NotNull
    private int calories;

    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    public Meal() {
    }

    public Meal(LocalDateTime dateTime, String description, int calories) {
        this(null, dateTime, description, calories);
    }

    public Meal(Integer id, LocalDateTime dateTime, String description, int calories) {
        super(id);
        this.dateTime = dateTime;
        this.description = description;
        this.calories = calories;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public LocalDate getDate() {
        return dateTime.toLocalDate();
    }

    public LocalTime getTime() {
        return dateTime.toLocalTime();
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "Meal{" +
                "id=" + id +
                ", dateTime=" + dateTime +
                ", description='" + description + '\'' +
                ", calories=" + calories +
                '}';
    }
}
