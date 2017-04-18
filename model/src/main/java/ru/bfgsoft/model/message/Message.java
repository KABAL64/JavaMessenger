package ru.bfgsoft.model.message;


import ru.bfgsoft.model.db.DbEntity;

import java.util.UUID;

/**
 * Сообщение
 */
public class Message implements DbEntity {

    /** Идентификтаор */
    private UUID id;

    /** Идентификтаор отправителя */
    private UUID senderId;

    /** Идентификтаор получетеля */
    private UUID receiverId;

    /** Сообщение */
    private String text;

    /** Логин получателя */
    private String receiverLogin;

    //region Getters/setters

    /**
     * Получить {@link #id}
     * @return {@link #id}
     */
    @Override
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
     * Получить {@link #senderId}
     * @return {@link #senderId}
     */
    public UUID getSenderId() {
        return senderId;
    }

    /**
     * Установить {@link #senderId}
     * @param senderId {@link #senderId}
     */
    public void setSenderId(UUID senderId) {
        this.senderId = senderId;
    }

    /**
     * Получить {@link #receiverId}
     * @return {@link #receiverId}
     */
    public UUID getReceiverId() {
        return receiverId;
    }

    /**
     * Установить {@link #receiverId}
     * @param receiverId {@link #receiverId}
     */
    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * Получить {@link #text}
     * @return {@link #text}
     */
    public String getText() {
        return text;
    }

    /**
     * Установить {@link #text}
     * @param text {@link #text}
     */
    public void setText(String text) {
        this.text = text;
    }

    /**
     * Получить {@link #receiverLogin}
     * @return {@link #receiverLogin}
     */
    public String getReceiverLogin() {
        return receiverLogin;
    }

    /**
     * Установить {@link #receiverLogin}
     * @param receiverLogin {@link #receiverLogin}
     */
    public void setReceiverLogin(String receiverLogin) {
        this.receiverLogin = receiverLogin;
    }

    //endregion
}
