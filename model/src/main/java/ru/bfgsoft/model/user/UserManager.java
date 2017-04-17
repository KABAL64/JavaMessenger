package ru.bfgsoft.model.user;


import ru.bfgsoft.model.core.BlException;
import ru.bfgsoft.model.core.CollectionsHelper;
import ru.bfgsoft.model.core.EntityManager;
import ru.bfgsoft.model.core.StringHelper;
import ru.bfgsoft.model.db.Db;

import java.util.List;
import java.util.UUID;

/**
 * Менеджер по работе с пользователями
 */
public class UserManager extends EntityManager<User> {

    /**
     * Наименование таблицы
     */
    private static final String userTableName = "user";

    /**
     * Конструктор. Создается менеджер с подключением к БД по умолчанию.
     */
    public UserManager() {
        super();
    }

    /**
     * Конструктор. Создается менеджер с выбранным подключением к БД.
     * @param db Подключение к БД.
     */
    public UserManager(Db db) {
        super(db);
    }

    @Override
    public List<User> getList() throws BlException {
        return getDb().getEntities(User.class, userTableName);
    }

    @Override
    public User getById(UUID id) throws BlException {
        return getDb().getEntityById(User.class, userTableName, id);
    }

    /**
     * Получение пользователя по логину.
     * @param login Логин.
     * @return Пользователь. Если не найден - null.
     */
    public User getByLogin(String login) throws BlException {
        return CollectionsHelper.firstOrNull(getList(), x -> x.getLogin().equalsIgnoreCase(login));
    }

    @Override
    public UUID add(User entity) throws BlException {

        if (entity.getId() == null)
            entity.setId(UUID.randomUUID());

        checkData(entity);

        getDb().addEntity(entity, userTableName);
        return entity.getId();
    }

    @Override
    public void delete(User entity) throws BlException {
        getDb().deleteEntityById(userTableName, entity.getId());
    }

    /**
     * Провряем корректность данных
     * @param entity Пользователь
     */
    private void checkData(User entity) throws BlException {

        // Для пользователя логин не может быть пустым
        if (StringHelper.isNullOrWhiteSpace(entity.getLogin()))
            throw new BlException("Логин пользователя не может быть пустым");

        // Проверка на существование пользователя с таким же логином
        User existUser = getByLogin(entity.getLogin());
        if ((existUser != null) && (!existUser.getId().equals(entity.getId())))
            throw new BlException("Пользователь с указанным логином уже существует");

        // ФИО
        if (StringHelper.isNullOrWhiteSpace(entity.getFio()))
            throw new BlException("Необходимо указать ФИО пользователя.");
    }

}
