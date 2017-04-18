package ru.bfgsoft.model.core;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Класс-помошник для работы с коллекциями.
 */
public class CollectionsHelper {

    /**
     * Получение первого элемента коллекции, удовлетворящего условию
     * @param c         Коллекция
     * @param predicate Условие
     * @param <T>       Тип данных коллекции
     * @return Первый элемент, удовлетворяющий условию, или null
     */
    public static <T> T firstOrNull(Collection<T> c, Predicate<T> predicate) {
        return c.stream().filter(predicate).findFirst().orElse(null);
    }


    /**
     * Получение списка элементов коллекции, удовлетворяющих условию
     * @param c         Коллекция
     * @param predicate Условие
     * @param <T>       Тип данных коллекции
     * @return Список записей, удовлетворяющих условию
     */
    public static <T> List<T> findAll(Collection<T> c, Predicate<T> predicate) {
        return c.stream().filter(predicate).collect(Collectors.toList());
    }


    /**
     * Получение списка записей с преобразованием с помощью функции
     * @param c       Коллекция
     * @param mapFunc Функция преобразования
     * @param <T1>    Тип данных коллекции
     * @param <T2>    Тип данных результирующего списка
     * @return Список преобразованных записей
     */
    public static <T1, T2> List<T2> select(Collection<T1> c, Function<T1, T2> mapFunc) {
        return c.stream().map(mapFunc).collect(Collectors.toList());
    }

}
