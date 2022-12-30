package net.buscompany.exception;

public enum ErrorCode {
    USER_ALREADY_EXISTS("Пользователь с таким логином уже зарегистрирован", "user"),
    INVALID_LOGIN_OR_PASSWORD("Неверно указан логин или пароль", "login"),
    USER_NOT_EXISTS("Такого пользователя нет","user"),
    ADMIN_COUNT("Хотя бы 1 админ должен остаться","admin"),
    PERMISSION_DENIED("Отказано в доступе, действие доступно только админу","user"),
    INVALID_PASSWORD("Неверный пароль","user"),
    ERROR_CREATE_DATES("Не удалось получить даты из расписания","schedule"),
    BUS_NOT_FOUND("Автобус с таким именем не найден","bus"),
    TRIP_NOT_EXISTS("Такого trip не существует","trip"),
    TRIP_ALREADY_APPROVED("Этот рейс уже утверждён","trip"),
    TRIP_NOT_APPROVED("Данный рейс не утверждён","trip"),
    TRIP_NOT_FOUND_AT_THIS_DATE("Рейс на такую дату не найден","trip"),
    NO_FREE_PLACES("Недостаточно свободных мест","bus"),
    WRONG_PARSE_ID("Передан некорректный ID","user"),
    ORDER_NOT_EXISTS("Такого заказа не существует","order"),
    NOT_CLIENT_ORDER("Такой заказ принадлежит не этому клиенту","order"),
    NOT_FREE_PLACE("Это место в автобусе уже занято","bus"),
    WRONG_PASSENGER("Такого пассажира в заказе нет","passenger");

    private final String message;
    private final String field;

    ErrorCode(String message, String field){
        this.message = message;
        this.field = field;
    }

    public String getMessage() {
        return message;
    }

    public String getField() {
        return field;
    }
}
