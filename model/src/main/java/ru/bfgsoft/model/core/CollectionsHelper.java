package ru.bfgsoft.model.core;

import java.util.*;
import java.util.function.Predicate;

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
}
