import org.junit.*;
import ru.bfgsoft.model.core.BlException;
import ru.bfgsoft.model.core.CollectionsHelper;
import ru.bfgsoft.model.db.Db;
import ru.bfgsoft.model.user.User;
import ru.bfgsoft.model.user.UserManager;


import java.util.*;

/**
 * Тесты пользователей
 */
public class UserTests extends Assert {

    // Параметры пользователя
    private static final UUID userId;
    private static final String userLogin;
    private static final String userFio;

    //Параметры добавляемого/обновляемого пользователя
    private static final UUID userIdNext;
    private static final String userLoginNext;
    private static final String userFioNext;

    // Задаем параметры для текущего запуска
    static {
        userId = UUID.randomUUID();
        userLogin = TestsHelper.getRandomString(20);
        userFio = TestsHelper.getRandomString(10);

        userIdNext = UUID.randomUUID();
        userLoginNext = TestsHelper.getRandomString(20);
        userFioNext = TestsHelper.getRandomString(10);
    }

    /** Подключение к БД */
    private Db db;

    /**
     * Менеджер по работе с пользователями
     */
    private UserManager userManager;

    /**
     * Инициализация тестов
     */
    @Before
    public void init() {
        db = Db.getNewInstance();
        userManager = new UserManager(db);

        // Добавляем пользователя
        User user = new User();
        user.setId(userId);
        user.setLogin(userLogin);
        user.setFio(userFio);
        TestsHelper.addInTable(db, "user", user);
    }

    /**
     * Тест на получение пользователя по идентификатору
     */
    @Test
    public void testUserGetById() {
        try {
            User user = userManager.getById(userId);
            checkUser(user, true);

        } catch (BlException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * Тест на получение списка пользователей
     */
    @Test
    public void testUserGetList() {
        try {
            List<User> users = userManager.getList();
            User user = CollectionsHelper.firstOrNull(users, x -> x.getId().equals(userId));
            checkUser(user, true);
        } catch (BlException e) {
            e.printStackTrace();
            fail();
        }
    }

    /**
     * Тест на получение пользователя по логину
     */
    @Test
    public void testUserGetByLogin() {
        try {
            User user = userManager.getByLogin(userLogin);
            checkUser(user, true);
        } catch (BlException e) {
            e.printStackTrace();
            fail();
        }
    }


    /**
     * Тест на добавление пользователя
     */
    @Test
    public void testUserAdd() {
        try {
            // Создаем
            User newUser = new User();
            newUser.setId(userIdNext);
            newUser.setLogin(userLoginNext);
            newUser.setFio(userFioNext);

            //region Проверки перед добавлением

            newUser.setLogin("");
            try {
                userManager.add(newUser);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Логин пользователя не может быть пустым"));
            }

            newUser.setLogin(userLogin);
            try {
                userManager.add(newUser);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Пользователь с указанным логином уже существует"));
            }
            newUser.setLogin(userLoginNext);


            newUser.setFio("");
            try {
                userManager.add(newUser);
                fail();
            } catch (Exception ex) {
                assertTrue(ex.getMessage().contains("Необходимо указать ФИО пользователя"));
            }
            newUser.setFio(userFioNext);

            //endregion

            // Добавляем
            userManager.add(newUser);

            // Ищем  и проверям
            User newUserInBd = userManager.getById(userIdNext);
            checkUser(newUserInBd, false);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }


    /**
     * Тест на удаление пользователя
     */
    @Test
    public void testUserDelete() {
        try {

            // Удаление пользователя
            User user = userManager.getById(userId);
            assertNotNull(user);
            userManager.delete(user);

            //Проверяем удаление пользователя
            user = userManager.getById(userId);
            assertNull(user);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail();
        }
    }


    /**
     * Проверка пользователя
     * @param user Пользователь
     * @param isCheckDefault Проверка первоначального пользователя
     */
    private void checkUser(User user, boolean isCheckDefault) {
        assertNotNull(user);
        assertEquals(isCheckDefault ? userLogin : userLoginNext, user.getLogin());
        assertEquals(isCheckDefault ? userFio : userFioNext, user.getFio());
    }


    /**
     * Очистка БД после тестов
     */
    @After
    public void clear() {
        TestsHelper.deleteFromTable(db, "user", userId);
        TestsHelper.deleteFromTable(db, "user", userIdNext);
    }
}
