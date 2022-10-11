<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html lang="ru">
<head>
    <meta charset="utf-8">
    <title>Meals</title>
</head>

<body>
<h3><a href="index.html">Home</a></h3>
<ul>
    <li><a href="meals">Add Meal</a></li>
</ul>

<style>
    .green {
        color: green;
    }

    .red {
        color: red;
    }
</style>

<table border="1" cellpadding="8" cellspacing="0" style="margin: auto">
    <caption>Meals</caption>
    <tr>
        <th>Date</th>
        <th>Description</th>
        <th>Calories</th>
        <th>Action</th>
        <th>Action</th>

    </tr>

    <c:forEach items="${mealsTo}" var="mealTo">
        <jsp:useBean id="mealTo" type="ru.javawebinar.topjava.model.MealTo"/>
        <tr>
            <td class="${mealTo.excess ? 'red':'green'}">
                    ${mealTo.getDateTime()}
            </td>

            <td class="${mealTo.excess ? 'red':'green'}">
                    ${mealTo.getDescription()}
            </td>

            <td class="${mealTo.excess ? 'red':'green'}">
                    ${mealTo.getCalories()}
            </td>

            <td>
                <button onclick="location.href='./'">Update</button>
            </td>

            <td>
                <button onclick="location.href='meals?id=${mealTo.id.toString()}&action=delete'">Delete</button>
            </td>
        </tr>
    </c:forEach>
</table>
</body>
</html>