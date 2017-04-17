package ru.bfgsoft.model.user;


import ru.bfgsoft.model.db.DbEntity;

import java.util.UUID;

/**
 * Пользователь
 */
public class User  implements DbEntity{

    /** Идентификтаор */
    private UUID id;

    /** Логин */
    private String login;

    /** ФИО */
    private String fio;

    //region Getters/setters

    /**
     * Получить {@link #id}
     * @return {@link #id}
     */
    public UUID getId() {
        return id;
    }

    /**
     * Установить {@link #id}
     * @param id {@link #id}
     */
    public void setId(UUID id) {
        this.id = id;
    }

    /**
     * Получить {@link #login}
     * @return {@link #login}
     */
    public String getLogin() {
        return login;
    }

    /**
     * Установить {@link #login}
     * @param login {@link #login}
     */
    public void setLogin(String login) {
        this.login = login;
    }

    /**
     * Получить {@link #fio}
     * @return {@link #fio}
     */
    public String getFio() {
        return fio;
    }

    /**
     * Установить {@link #fio}
     * @param fio {@link #fio}
     */
    public void setFio(String fio) {
        this.fio = fio;
    }

    //endregion
}
