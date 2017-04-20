import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.bfgsoft.model.core.BlException;
import ru.bfgsoft.model.core.CollectionsHelper;
import ru.bfgsoft.model.db.Db;
import ru.bfgsoft.model.message.Message;
import ru.bfgsoft.model.message.MessageManager;

import java.util.List;
import java.util.UUID;

/**
 * Тесты сообщений
 */
public class MessageTests extends Assert {

    // Параметры сообщения
    private static final UUID messageId;
    private static final UUID messageSenderId;
    private static final UUID messageReceiverId;
    private static final String messageText;


    //Параметры добавляемого/обновляемого сообщения
    private static final UUID messageIdNext;
    private static final UUID messageSenderIdNext;
    private static final UUID messageReceiverIdNext;
    private static final String messageTextNext;

    // Задаем параметры для текущего запуска
    static {
        messageId = UUID.randomUUID();
        messageSenderId = UserTests.getUserId();
        messageReceiverId = UserTests.getUserIdSecond();
        messageText = TestsHelper.getRandomString(20);

        messageIdNext = UUID.randomUUID();
        messageSenderIdNext = UserTests.getUserIdSecond();
        messageReceiverIdNext = UserTests.getUserId();
        messageTextNext = TestsHelper.getRandomString(20);
    }

    /** Подключение к БД */
    private Db db;

    /**
     * Менеджер по работе с сообщениями
     */
    private MessageManager messageManager;

    /**
     * Тесты пользователей
     */
    private UserTests userTests = new UserTests();

    /**
     * Инициализация тестов
     */
    @Before
    public void init() {
        db = Db.getNewInstance();
        userTests.init();
        messageManager = new MessageManager(db);

        // Добавляем 
        Message message = new Message();
        message.setId(messageId);
        message.setText(messageText);
        message.setSenderId(messageSenderId);
        message.setReceiverId(messageReceiverId);
        TestsHelper.addInTable(db, "message", message);
    }

    /**
     * Тест на получение сообщения по идентификатору
     */
    @Test
    public void testMessageGetById() {
        try {
            Message message = messageManager.getById(messageId);
            checkMessage(message, true);

        } catch (BlException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * Тест на получение списка сообщений
     */
    @Test
    public void testMessageGetList() {
        try {
            List<Message> messages = messageManager.getList();
            Message message = CollectionsHelper.firstOrNull(messages, x -> x.getId().equals(messageId));
            checkMessage(message, true);
        } catch (BlException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * Тест на добавление сообщения
     */
    @Test
    public void testMessageAdd() {
        try {
            // Создаем
            Message newMessage = new Message();
            newMessage.setId(messageIdNext);
            newMessage.setText(messageTextNext);
            newMessage.setSenderId(messageSenderIdNext);
            newMessage.setReceiverId(messageReceiverIdNext);

            //region Проверки перед добавлением

            newMessage.setSenderId(null);
            try {
                messageManager.add(newMessage);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Необходимо заполнить отправителя"));
            }
            newMessage.setSenderId(messageSenderIdNext);

            newMessage.setReceiverId(null);
            try {
                messageManager.add(newMessage);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Необходимо заполнить получателя"));
            }

            newMessage.setReceiverId(UUID.randomUUID());
            try {
                messageManager.add(newMessage);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Получатель не найден"));
            }
            newMessage.setReceiverId(messageReceiverIdNext);

            newMessage.setSenderId(UUID.randomUUID());
            try {
                messageManager.add(newMessage);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Отправитель не найден"));
            }
            newMessage.setSenderId(messageSenderIdNext);

            newMessage.setReceiverId(messageSenderIdNext);
            try {
                messageManager.add(newMessage);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Нельзя отправлять сообщения себе"));
            }
            newMessage.setReceiverId(messageReceiverIdNext);

            newMessage.setText("");
            try {
                messageManager.add(newMessage);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Сообщение не может быть пустым"));
            }
            newMessage.setText(messageTextNext);

            //endregion

            // Добавляем
            messageManager.add(newMessage);

            // Ищем  и проверям
            Message newMessageInBd = messageManager.getById(messageIdNext);
            checkMessage(newMessageInBd, false);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }


    /**
     * Тест на удаление сообщения
     */
    @Test
    public void testMessageDelete() {
        try {

            // Удаление сообщения
            Message message = messageManager.getById(messageId);
            assertNotNull(message);
            messageManager.delete(message);

            //Проверяем удаление сообщения
            message = messageManager.getById(messageId);
            assertNull(message);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }

    /**
     * Проверка сообщения
     * @param message        Сообщение
     * @param isCheckDefault Проверка первоначального сообщения
     */
    private void checkMessage(Message message, boolean isCheckDefault) {
        assertNotNull(message);
        assertEquals(isCheckDefault ? messageText : messageTextNext, message.getText());
        assertEquals(isCheckDefault ? messageSenderId : messageSenderIdNext, message.getSenderId());
        assertEquals(isCheckDefault ? messageReceiverId : messageReceiverIdNext, message.getReceiverId());
    }


    /**
     * Очистка БД после тестов
     */
    @After
    public void clear() {
        TestsHelper.deleteFromTable(db, "message", messageId);
        TestsHelper.deleteFromTable(db, "message", messageIdNext);

        userTests.clear();
    }
}
