package com.example.telemaven.bot.services;

import com.example.telemaven.bot.constans.Actions;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class MenuService {

    public InlineKeyboardMarkup createMainMenu() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder()
                                .text("Розклад")
                                .callbackData(Actions.TIMETABLE_ACTION)
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Який зараз тиждень?")
                                .callbackData(Actions.WEEK_ACTION)
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Д/З")
                                .callbackData(Actions.HOMEWORK_ACTION)
                                .build()
                ))
                .keyboardRow(List.of(
                        InlineKeyboardButton.builder()
                                .text("Модулі")
                                .callbackData(Actions.MODULE_ACTION)
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Сесія")
                                .callbackData(Actions.SESSION_ACTION)
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("\uD83D\uDCE2")
                                .callbackData(Actions.NOTIFICATION_ACTION)
                                .build()
                ))
                .build();
    }
}
