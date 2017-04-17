package ru.bfgsoft.model.core;

/**
 * Ошибка в бизнес-логике
 */
public class BlException extends Exception {

    /**
     * Конструктор
     * @param message Сообщение об ошибке
     */
    public BlException(String message) {
        super(message);
    }
}
