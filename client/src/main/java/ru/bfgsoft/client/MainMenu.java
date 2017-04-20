package ru.bfgsoft.client;

import com.google.gson.Gson;
import javafx.util.Pair;
import redis.clients.jedis.JedisPubSub;
import ru.bfgsoft.model.core.BlException;
import ru.bfgsoft.model.core.ParseHelper;
import ru.bfgsoft.model.db.Db;
import ru.bfgsoft.model.message.Message;
import ru.bfgsoft.model.message.MessageManager;
import ru.bfgsoft.model.user.User;
import ru.bfgsoft.model.user.UserManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import org.apache.commons.io.FilenameUtils;

/**
 * Основное меню
 */
public class MainMenu {

    /**
     * Элементы основного меню
     */
    private List<Pair<String, Runnable>> mainMenuItems = new ArrayList<Pair<String, Runnable>>() {{
        add(new Pair<>("Вход", () -> authUser()));
        add(new Pair<>("Регистрация пользователя", () -> regUser()));
        add(new Pair<>("Список пользователей", () -> showUsers()));
        add(new Pair<>("Выход", null));
    }};

    /**
     * Элементы меню личного кабинета
     */
    private List<Pair<String, Runnable>> authMenuItems = new ArrayList<Pair<String, Runnable>>() {
        {
            add(new Pair<>("Просмотр сообщений", () -> showMessages()));
            add(new Pair<>("Отправить сообщение", () -> sendMessage()));
            add(new Pair<>("Экспорт сообщений в файл", () -> exportMessages()));
            add(new Pair<>("Назад", null));
        }
    };

    /** Менеджер пользователей */
    private UserManager userManager;

    /** Менеджер сообщений */
    private MessageManager messageManager;

    /** Сканр консоли */
    private Scanner scanner = new Scanner(System.in);

    /** Текущий пользователь */
    private User currentUser;

    /** Подписчик на сообщения */
    private JedisPubSub subscriber;

    /**
     * Конструктор
     */
    public MainMenu() {
        userManager = new UserManager();
        messageManager = new MessageManager(userManager.getDb());

        subscriber = new JedisPubSub() {
            @Override
            public void onMessage(String channel, String message) {
                super.onMessage(channel, message);
                System.out.println(message);
            }
        };
    }

    /**
     * Показать основное меню
     */
    public void show() {
        showMenu(mainMenuItems);
    }

    /**
     * Показать меню
     * @param menuItems Элементы меню
     */
    private void showMenu(List<Pair<String, Runnable>> menuItems) {

        Pair<String, Runnable> selectedMenuUItem = null;
        //Продолжаем пока не выбран последний пункт меню
        while (selectedMenuUItem == null || menuItems.indexOf(selectedMenuUItem) != menuItems.size() - 1) {
            clearConsole();
            for (int i = 0; i < menuItems.size(); i++) {
                Pair<String, Runnable> menuItem = menuItems.get(i);
                System.out.println(String.format("%1$s. %2$s", i + 1, menuItem.getKey()));
            }
            System.out.println("Введите номер команды ...");

            String selectedItemIdx = scanner.next();
            try {
                if (!ParseHelper.isInteger(selectedItemIdx) || (Integer.parseInt(selectedItemIdx)) > menuItems.size() ||
                    Integer.parseInt(selectedItemIdx) < 1) {
                    throw new Exception("Ошибка: неверный номер команды.");
                }

                selectedMenuUItem = menuItems.get(Integer.parseInt(selectedItemIdx) - 1);
                if (selectedMenuUItem.getValue() != null)
                    selectedMenuUItem.getValue().run();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                waitUser();
            }
        }

    }

    /**
     * Авторизация пользователя
     */
    private void authUser() {
        clearConsole();
        System.out.println("Вход");

        System.out.print("Логин пользователя: ");
        String userLogin = scanner.next();

        try {
            User user = userManager.getByLogin(userLogin);
            if (user == null)
                throw new Exception("Пользователь не найден");

            currentUser = user;
            showMenu(authMenuItems);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            waitUser();
        }
    }

    /**
     * Регистрация нового пользователя"
     */
    private void regUser() {

        clearConsole();
        System.out.println("Регистрация нового пользователя");
        System.out.print("Логин пользователя: ");
        String userLogin = scanner.next();
        scanner.nextLine();
        System.out.print("ФИО пользователя: ");
        String userFio = scanner.next();

        try {
            User newUser = new User();
            newUser.setLogin(userLogin);
            newUser.setFio(userFio);
            userManager.add(newUser);
            System.out.println("Регистрация прошла успешно");
        } catch (BlException e) {
            System.out.println(e.getMessage());
        }
        waitUser();
    }

    /**
     * Список пользователей
     */
    private void showUsers() {

        clearConsole();
        System.out.println("Список пользователей:");
        try {
            List<User> users = userManager.getList();
            if (users.isEmpty())
                throw new Exception("Список пуст");

            int maxLoginLength = users.stream().map(x -> x.getLogin().length()).max(Integer::compareTo).get() + 5;
            System.out.println(String.format("%1$-" + maxLoginLength + "s ФИО", "Логин"));
            for (User user : users) {
                System.out.println(String.format("%1$-" + maxLoginLength + "s %2$s", user.getLogin(), user.getFio()));
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        waitUser();
    }

    /**
     * Ожидание отклика пользователя
     */
    private void waitUser() {
        System.out.println("Нажмите Enter для продолжения...");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Очистка консоли
     */
    private void clearConsole() {
        try {

            final String os = System.getProperty("os.name");
            if (os.contains("Windows"))
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            else
                Runtime.getRuntime().exec("clear");

        } catch (Exception ignore) {
        }
    }

    /**
     * Показать сообщения
     */
    private void showMessages() {
        if (currentUser == null) return;

        clearConsole();
        System.out.println("История сообщений");

        try {
            List<Message> messages = messageManager.getListByUserId(currentUser.getId());

            if (messages.isEmpty())
                throw new Exception("Нет сообщений");

            for (Message message : messages) {
                if (message.getSenderId().equals(currentUser.getId())) {
                    System.out.println("Отправлено пользователю: " + message.getReceiverLogin());
                } else
                    System.out.println("Получено от: " + message.getSenderLogin());
                System.out.println(message.getText());
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Мониторим сообщения этого пользователя
        new Thread(() -> {
            try {
                Db.getNewInstance().subscribe(subscriber, currentUser.getId().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        waitUser();
        subscriber.unsubscribe();
    }

    /**
     * Экспорт сообщений в файл
     */
    private void exportMessages() {
        if (currentUser == null) return;

        clearConsole();
        System.out.println("Выберите файл для сохранения...");
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Сохранить файл");

        FileNameExtensionFilter filter = new FileNameExtensionFilter("Файлы JSON (*.json)", "json");
        chooser.setFileFilter(filter);
        chooser.addChoosableFileFilter(filter);

        int result = chooser.showSaveDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (!FilenameUtils.getExtension(file.getName()).equalsIgnoreCase("json"))
                file = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName()) + ".json");

            try (FileWriter fileWriter = new FileWriter(file)) {
                Gson gson = new Gson();
                List<Message> userMessages = messageManager.getListByUserId(currentUser.getId());
                fileWriter.write(gson.toJson(userMessages));
                System.out.println("Сохранено");
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            waitUser();
        }
    }


    /**
     * Отправить сообщение
     */
    private void sendMessage() {
        if (currentUser == null) return;

        clearConsole();
        System.out.println("Отправить сообщение");

        try {
            System.out.print("Логин получателя: ");
            String receiverLogin = scanner.next();

            if (Objects.equals(receiverLogin, currentUser.getLogin()))
                throw new Exception("Нельзя отправлять сообщения себе");

            User receiver = userManager.getByLogin(receiverLogin);
            if (receiver == null)
                throw new Exception("Получатель не найден");

            scanner.nextLine();
            System.out.print("Текст сообщения: ");
            String messageText = scanner.nextLine();

            Message message = new Message();
            message.setSenderId(currentUser.getId());
            message.setReceiverId(receiver.getId());
            message.setReceiverLogin(receiverLogin);
            message.setText(messageText);

            messageManager.add(message);
            System.out.println("Сообщение отправлено");

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        waitUser();
    }

}
