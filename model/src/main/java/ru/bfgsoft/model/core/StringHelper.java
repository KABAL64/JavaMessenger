package ru.bfgsoft.model.core;

/**
 * Класс помошник для работы со строками
 */
public class StringHelper {

    /**
     * Проверка на то что строка null или заполнена пробелами
     * @param value Проверяемая строка
     * @return true, если строка null или заполнена пробелами, иначе - false
     */
    public static boolean isNullOrWhiteSpace(String value) {
        if (value == null)
            return true;
        for (int index = 0; index < value.length(); ++index) {
            if (!Character.isWhitespace(value.charAt(index)))
                return false;
        }
        return true;
    }


    /**
     * Проверка на то что строка null или пустая
     * @param str Проверяемая строка
     * @return true, если строка null или пустая, иначе - false
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }


    /**
     * Получение подстроки (по стартовому индексу и длине)
     * @param string Строка
     * @param start  Стартовая позиция в строке
     * @param length Длина подстроки
     * @return Подстрока
     */
    public static String substring(String string, int start, int length) {
        return string.substring(start, Math.min(start + length, string.length()));
    }

}
