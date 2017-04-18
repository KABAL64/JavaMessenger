package ru.bfgsoft.model.db;

import com.google.gson.Gson;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;
import ru.bfgsoft.model.core.CollectionsHelper;
import ru.bfgsoft.model.sysClass.Config;

import java.util.*;

/**
 * Класс взаимодействия с БД.
 */
public class Db {

    /**
     * Драйвер Redis
     */
    private Jedis jedis;

    /**
     * Сериализатор в JSON
     */
    private Gson gson = new Gson();

    /**
     * Текущая транзакция.
     */
    private Transaction transaction;


    /**
     * Конструктор.
     */
    private Db() {
        jedis = new Jedis(Config.getInstance().getProperty("host"),
                Integer.parseInt(Config.getInstance().getProperty("port")));
    }


    /**
     * Получение нового подключения к БД
     * @return Новое подключение
     */
    public static Db getNewInstance() {
        return new Db();
    }


    /**
     * Запуск транзакции.
     */
    public void beginTransaction() {
        transaction = jedis.multi();
    }

    /**
     * Откат транзакции
     */
    public void rollbackTransaction() {
        transaction.discard();
        transaction = null;
    }

    /**
     * Подтверждение транзакции
     */
    public void commitTransaction() {
        transaction.exec();
        transaction = null;
    }


    /**
     * Добавление сущности в БД
     * @param entity    Сущность
     * @param tableName Наименование таблицы
     * @param <T>       Тип сущности
     */
    public <T extends DbEntity> void addEntity(T entity, String tableName) throws DbException {

        try {
            if (transaction != null) {
                transaction.set(String.format("%1$s:%2$s", tableName, entity.getId()), gson.toJson(entity));
            } else {
                String result = jedis.set(String.format("%1$s:%2$s", tableName, entity.getId()), gson.toJson(entity));
                if (!Objects.equals(result, "OK"))
                    throw new Exception(result);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DbException();
        }
    }

    /**
     * Получение сущности по идентификатору
     * @param clazz     Класс сущности
     * @param tableName Наименование таблицы
     * @param id        Идентификатор
     * @param <T>       Тип сущности
     * @return Сущность
     */
    public <T extends DbEntity> T getEntityById(Class<T> clazz, String tableName, UUID id) throws DbException {
        try {
            String result = jedis.get(String.format("%1$s:%2$s", tableName, id));
            return result == null ? null : gson.fromJson(result, clazz);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DbException();
        }
    }

    /**
     * Получение списка записей сущности.
     * @param clazz Класс сущности.
     * @param <T>   Тип сущности.
     * @return Список записей сущности.
     */
    public <T extends DbEntity> List<T> getEntities(Class<T> clazz, String tableName) throws DbException {

        try {
            List<T> result = new ArrayList<>();

            Set<String> keySet = jedis.keys(tableName + ":*");
            for (String key : keySet) {
                String id = key.substring(tableName.length() + 1);
                T entity = getEntityById(clazz, tableName, UUID.fromString(id));
                if (entity != null)
                    result.add(entity);
            }

            return result;

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DbException();
        }
    }

    /**
     * Удаление сущности по идентификатору
     * @param tableName Наименование таблицы
     * @param id        Идентификатор
     */
    public void deleteEntityById(String tableName, UUID id) throws DbException {
        deleteEntities(tableName, new ArrayList<UUID>() {{add(id);}});
    }

    /**
     * Удаление списка сущностей
     * @param tableName Наименование таблицы
     * @param idList    Список идентификаторов
     */
    public void deleteEntities(String tableName, List<UUID> idList) throws DbException {
        if (idList.isEmpty()) return;

        try {
            String keys =
                    String.join(", ", CollectionsHelper.select(idList, x -> String.format("%1$s:%2$s", tableName, x)));

            if (transaction != null)
                transaction.del(keys);
            else
                jedis.del(keys);

        } catch (Exception ex) {
            ex.printStackTrace();
            throw new DbException();
        }
    }
}
