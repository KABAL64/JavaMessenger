package ru.bfgsoft.model.message;


import ru.bfgsoft.model.core.BlException;
import ru.bfgsoft.model.core.CollectionsHelper;
import ru.bfgsoft.model.core.EntityManager;
import ru.bfgsoft.model.core.StringHelper;
import ru.bfgsoft.model.db.Db;
import ru.bfgsoft.model.user.UserManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Менеджер по работе с сообщениями
 */
public class MessageManager extends EntityManager<Message> {

    /**
     * Наименование таблицы
     */
    private static final String messageTableName = "message";

    /**
     * Конструктор. Создается менеджер с подключением к БД по умолчанию.
     */
    public MessageManager() {
        super();
    }

    /**
     * Конструктор. Создается менеджер с выбранным подключением к БД.
     * @param db Подключение к БД.
     */
    public MessageManager(Db db) {
        super(db);
    }

    @Override
    public List<Message> getList() throws BlException {
        return getDb().getEntities(Message.class, messageTableName);
    }

    @Override
    public Message getById(UUID id) throws BlException {
        return getDb().getEntityById(Message.class, messageTableName, id);
    }

    /**
     * Получение списка сообщений пользователя (в качестве отправителя или получателя)
     * @param userId Идентификатор пользователя
     * @return Список сообщений пользователя
     */
    public List<Message> getListByUserId(UUID userId) throws BlException {
        return CollectionsHelper.findAll(getList(),
                x -> Objects.equals(x.getSenderId(), userId) || Objects.equals(x.getReceiverId(), userId));
    }


    @Override
    public UUID add(Message entity) throws BlException {

        if (entity.getId() == null)
            entity.setId(UUID.randomUUID());

        checkData(entity);

        entity.setSenderLogin(new UserManager(getDb()).getById(entity.getSenderId()).getLogin());
        entity.setReceiverLogin(new UserManager(getDb()).getById(entity.getReceiverId()).getLogin());

        getDb().addEntity(entity, messageTableName);

        getDb().publish(entity.getReceiverId().toString(),
                String.format("Получено от: %1$s\n%2$s", entity.getSenderLogin(), entity.getText()));

        return entity.getId();
    }

    @Override
    public void delete(Message entity) throws BlException {
        deleteList(new ArrayList<Message>() {{add(entity);}});
    }

    /**
     * Удаление списка сообщений
     * @param entities Сообщения
     */
    public void deleteList(List<Message> entities) throws BlException {
        getDb().deleteEntities(messageTableName, CollectionsHelper.select(entities, Message::getId));
    }

    /**
     * Провряем корректность данных
     * @param entity Сообщение
     */
    private void checkData(Message entity) throws BlException {

        if (entity.getSenderId() == null)
            throw new BlException("Необходимо заполнить отправителя");

        if (entity.getReceiverId() == null)
            throw new BlException("Необходимо заполнить получателя");

        if (entity.getReceiverId().equals(entity.getSenderId()))
            throw new BlException("Нельзя отправлять сообщения себе");

        UserManager userManager = new UserManager(getDb());
        if (userManager.getById(entity.getReceiverId()) == null)
            throw new BlException("Получатель не найден");

        if (userManager.getById(entity.getSenderId()) == null)
            throw new BlException("Отправитель не найден");

        if (StringHelper.isNullOrEmpty(entity.getText()))
            throw new BlException("Сообщение не может быть пустым");
    }

}
