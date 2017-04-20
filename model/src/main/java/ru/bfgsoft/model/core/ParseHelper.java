package ru.bfgsoft.model.core;

import java.util.regex.Pattern;

/**
 * Класс-помощник для работы с парсингом данных
 */
public class ParseHelper {

    /**
     * Определить является ли строка числом
     * @param s Строка
     * @return True-является, иначе false
     */
    public static boolean isInteger(String s) {

        if (StringHelper.isNullOrWhiteSpace(s))
            return false;

        Pattern pattern = Pattern.compile("[-+]?[0-9]+");
        return pattern.matcher(s).matches();
    }

}
