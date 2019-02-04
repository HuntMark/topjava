package ru.javawebinar.topjava.util;

import ru.javawebinar.topjava.model.UserMeal;
import ru.javawebinar.topjava.model.UserMealWithExceed;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.util.*;

import static java.util.stream.Collectors.*;
import static ru.javawebinar.topjava.util.TimeUtil.isBetween;

public class UserMealsUtil {

    public static void main(String[] args) {

        List<UserMeal> meals = Arrays.asList(
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 10, 0), "Завтрак", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 13, 0), "Обед", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 30, 20, 0), "Ужин", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 10, 0), "Завтрак", 1000),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 13, 0), "Обед", 500),
                new UserMeal(LocalDateTime.of(2015, Month.MAY, 31, 20, 0), "Ужин", 510)
        );

        getFilteredWithExceededViaFor(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

        System.out.println();

        getFilteredWithExceededViaFor(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 1900)
                .forEach(System.out::println);

        System.out.println("==========");

        getFilteredWithExceededViaStream(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 2000)
                .forEach(System.out::println);

        System.out.println();

        getFilteredWithExceededViaStream(meals, LocalTime.of(7, 0), LocalTime.of(12, 0), 1900)
                .forEach(System.out::println);

    }

    private static List<UserMealWithExceed> getFilteredWithExceededViaFor(List<UserMeal> meals,
                                                                          LocalTime startTime,
                                                                          LocalTime endTime,
                                                                          int caloriesPerDay) {

        Map<LocalDate, Integer> exceedsByDay = new HashMap<>();

        for (UserMeal meal : meals) {
            exceedsByDay.merge(meal.getLocalDate(), meal.getCalories(), Integer::sum);
        }

        List<UserMealWithExceed> exceededMeals = new ArrayList<>();

        List<UserMeal> filteredMeals = meals
                .stream()
                .filter(meal -> isBetween(meal.getLocalTime(), startTime, endTime))
                .collect(toList());

        for (UserMeal meal : filteredMeals) {
            exceededMeals.add(new UserMealWithExceed(meal, exceedsByDay.get(meal.getLocalDate()) > caloriesPerDay));
        }

        return exceededMeals;
    }

    private static List<UserMealWithExceed> getFilteredWithExceededViaStream(List<UserMeal> meals,
                                                                             LocalTime startTime,
                                                                             LocalTime endTime,
                                                                             int caloriesPerDay) {

        Map<LocalDate, Integer> exceedsByDay = meals
                .stream()
                .collect(groupingBy(UserMeal::getLocalDate, summingInt(UserMeal::getCalories)));

        return meals
                .stream()
                .filter(meal -> isBetween(meal.getLocalTime(), startTime, endTime))
                .map(meal -> new UserMealWithExceed(meal, exceedsByDay.get(meal.getLocalDate()) > caloriesPerDay))
                .collect(toList());
    }
}
