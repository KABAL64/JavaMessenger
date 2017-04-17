package ru.bfgsoft.model.core;


import ru.bfgsoft.model.db.Db;
import ru.bfgsoft.model.db.DbEntity;

import java.util.List;
import java.util.UUID;

/**
 * Менеджер для управления сущностями БЛ
 *
 * @param <T> Тип сущности
 */
public abstract class EntityManager<T extends DbEntity> {

    /**
     * Соединение с БД, используемое в менеджере
     */
    private Db db;

    /**
     * Конструктор
     *
     * @param db Соединение с БД
     */
    protected EntityManager(Db db) {
        this.db = db;
    }

    /**
     * Конструктор по умолчанию
     */
    protected EntityManager() {
        this.db = Db.getNewInstance();
    }

    /**
     * Получение списка записей сущности, обслуживаемой менеджером
     *
     * @return Список записей сущности, обслуживаемой менеджером
     */
    public abstract List<T> getList() throws BlException;

    /**
     * Получение записи сущности по ее идентификатору
     *
     * @param id Идентификатор записи
     * @return Запись сущности
     */
    public abstract T getById(UUID id) throws BlException;

    /**
     * Добавление записи сущности
     *
     * @param entity Добавляемая запись сущности
     * @return Идентификатор добавленной записи
     */
    public abstract UUID add(T entity) throws BlException;

    /**
     * Удаление записи сущности
     *
     * @param entity Удаляемая запись сущности
     */
    public abstract void delete(T entity) throws BlException;

    /**
     * Получение соединения с БД
     *
     * @return Соединение с БД
     */
    public Db getDb() {
        return db;
    }

    /**
     * Установка соединения с БД
     *
     * @param db Соединение с БД
     */
    public void setDb(Db db) {
        this.db = db;
    }

}
