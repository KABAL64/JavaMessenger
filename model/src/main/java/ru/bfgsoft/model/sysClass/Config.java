package ru.bfgsoft.model.sysClass;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Класс для работы с настройками приложения
 */
public class Config {

    /**
     * Экземпляр для работы с настройками приложения
     */
    private static Config instance;

    /**
     * Настройки приложения
     */
    private Properties properties;

    /**
     * Конструктор
     */
    private Config() {
        load();
    }

    /**
     * Получение экземпляра для работы с настройками приложения
     * @return Экземпляр для работы с настройками приложения
     */
    public static Config getInstance() {
        if (instance == null)
            instance = new Config();

        return instance;
    }

    /**
     * Получить путь к файлу конфигурации
     * @return Путь к файлу конфигурации
     */
    private String getConfigPath() {
        String result = "";
        try {
            String runPath = getClass().getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            if (runPath.endsWith(".jar")) {
                // release
                runPath = runPath.substring(0, runPath.lastIndexOf("/"));
                result = runPath + "/config.xml";
            } else {
                // debug
                result = runPath.replace("build/classes/main/", "src/main/resources/config.xml");
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Загрузить настройки
     */
    private void load() {
        try {
            FileInputStream inputStream = new FileInputStream(getConfigPath());
            properties = new Properties();
            properties.loadFromXML(inputStream);
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Сохранить настройки
     */
    public void save() {
        try {
            FileOutputStream out = new FileOutputStream(getConfigPath());
            properties.storeToXML(out, "config");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Установить значение свойства
     * @param name  Наименование свойства
     * @param value Значение
     */
    public void setProperty(String name, String value) {
        properties.setProperty(name, value);
    }

    /**
     * Получить значение свойства
     * @param name Наименование свойства
     */
    public String getProperty(String name) {
        return properties.getProperty(name);
    }

    /**
     * Получить все настройки
     * @return Настройки
     */
    public Properties getProperties() {
        return properties;
    }

}
