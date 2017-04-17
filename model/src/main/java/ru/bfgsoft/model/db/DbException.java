package ru.bfgsoft.model.db;

import ru.bfgsoft.model.core.BlException;

/**
 * Класс ошибки БД
 */
public class DbException extends BlException {

    /**
     * Конструктор
     */
    public DbException() {
        super("Ошибка выполнения операции в БД");
    }
}
