package ru.bfgsoft.model.db;


import java.util.UUID;

/**
 * Интерфейс сущности БД
 */
public interface DbEntity {

    /**
     * Получить идентификатор
     * @return Идентификатор
     */
    UUID getId();

}
