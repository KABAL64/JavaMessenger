import ru.bfgsoft.model.core.StringHelper;
import ru.bfgsoft.model.db.Db;
import ru.bfgsoft.model.db.DbEntity;
import ru.bfgsoft.model.db.DbException;

import java.util.*;

/**
 * Класс помошник для работы с тестами
 */
class TestsHelper {

    /**
     * Удаление записи из таблицы
     * @param db        Подключение к БД
     * @param tableName Наименование таблицы
     * @param id        Идентификатор записи
     */
    static void deleteFromTable(Db db, String tableName, UUID id) {
        try {
            db.deleteEntityById(tableName, id);
        } catch (DbException e) {
            e.printStackTrace();
        }
    }

    /**
     * Добавление записи в таблицу
     * @param db        Подключение к БД
     * @param tableName Наименование таблицы
     * @param entity    Сущность
     */
    static <T extends DbEntity> void addInTable(Db db, String tableName, T entity) {
        try {
            db.addEntity(entity, tableName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Получение произвольной строки
     * @param maxLength Максимальная длина строки
     * @return Произвольная строка
     */
    static String getRandomString(int maxLength) {
        String result = UUID.randomUUID().toString().replace("-", "");

        if (result.length() > maxLength)
            result = StringHelper.substring(result, 0, maxLength);

        return result;
    }

}
