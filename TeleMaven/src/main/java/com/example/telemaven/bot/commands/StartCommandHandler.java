package com.example.telemaven.bot.commands;

import com.example.telemaven.bot.services.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StartCommandHandler extends BotCommand {

    private final MenuService menuService;

    @Autowired
    public StartCommandHandler(@Value("start") String commandIdentifier, @Value("") String description, MenuService menuService) {
        super(commandIdentifier, description);
        this.menuService = menuService;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        try {
            var sendMessage = SendMessage.builder()
                    .chatId(chat.getId())
                    .text(" ㅤ  ㅤ  ㅤ  ㅤ  ㅤ  ㅤ ")
                    .replyMarkup(menuService.createMainMenu())
                    .build();
            absSender.execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
