package com.example.telemaven.bot.services;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class TelegramUser {
    @Id
    private Long userId;

    public TelegramUser() {
    }

    public TelegramUser(Long userId) {
        this.userId = userId;
    }

}
