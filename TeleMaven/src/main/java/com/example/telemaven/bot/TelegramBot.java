package com.example.telemaven.bot;

import com.example.telemaven.bot.constans.Actions;
import com.example.telemaven.bot.services.MenuService;
import com.example.telemaven.bot.services.TelegramUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import java.util.List;

import java.time.*;
import java.time.temporal.WeekFields;
import java.util.*;

@Component
public class TelegramBot extends TelegramLongPollingCommandBot {

    private final String username;
    private final TelegramUserRepository userRepository;
    private final MenuService menuService;



    public TelegramBot(@Value("${bot.token}") String botToken, @Value("${bot.username}") String username, TelegramUserRepository userRepository, MenuService menuService) {
        super(botToken);
        this.username = username;
        this.userRepository = userRepository;
        this.menuService = menuService;
    }

    @Override
    public String getBotUsername() {
        return username;
    }

    public void registerUser(long userId) {
        userRepository.save(new TelegramUser(userId));
    }

    public void unregisterUser(long userId) {
        userRepository.deleteById(userId);
    }


    @Override
    public void processNonCommandUpdate(Update update) {
        if (update.hasCallbackQuery()) {
            String callbackData = update.getCallbackQuery().getData();
            long messageId = update.getCallbackQuery().getMessage().getMessageId();
            long chatId = update.getCallbackQuery().getMessage().getChatId();

            switch (callbackData) {
                case Actions.MONDAY_PAIR_ACTION, Actions.MONDAY_UNPAIR_ACTION, Actions.TUESDAY_PAIR_ACTION,
                        Actions.TUESDAY_UNPAIR_ACTION, Actions.WEDNESDAY_PAIR_ACTION, Actions.WEDNESDAY_UNPAIR_ACTION,
                        Actions.THURSDAY_PAIR_ACTION, Actions.THURSDAY_UNPAIR_ACTION, Actions.FRIDAY_PAIR_ACTION,
                        Actions.FRIDAY_UNPAIR_ACTION, Actions.HOMEWORK_ACTION -> handleScheduleAction(callbackData, chatId, messageId);
                case Actions.PAIR_ACTION, Actions.UNPAIR_ACTION, Actions.TIMETABLE_ACTION, Actions.WEEK_ACTION,
                        Actions.NOTIFICATION_ACTION, Actions.REGISTER_ACTION, Actions.UNREGISTER_ACTION -> handleCallbackDataAction(callbackData, chatId, messageId);
                case Actions.BACK_ACTION -> handleBackAction(update);
            }

            if (callbackData.equals(Actions.REGISTER_ACTION) || callbackData.equals(Actions.UNREGISTER_ACTION)) {
                toggleNotifications(chatId, messageId);
            }
        }
    }


    private void handleScheduleAction(String callbackData, long chatId, long messageId) {
        String title;
        List<String> buttonLabels;
        List<String> buttonUrls;

        // Заполним title, buttonLabels и buttonUrls в соответствии с callbackData
        switch (callbackData) {
            case Actions.MONDAY_PAIR_ACTION:
                title = "Розклад на парний Понеділок";
                buttonLabels = Arrays.asList("1.Психологія ( 8:00-9:35 / практ )", "2.ВЕБ-технології ( 9:50-11:25 / лекція )", "3.ІоТ ( 11:40-13:15 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://meet.google.com/xrd-zpbw-sbq?authuser=1&hs=179", "https://meet.google.com/wtb-igyr-gsc", "https://meet.google.com/jrf-dbjh-gab");
                break;
            case Actions.MONDAY_UNPAIR_ACTION:
                title = "Розклад на непарний Понеділок";
                buttonLabels = Arrays.asList("2.ВЕБ-технології ( 9:50-11:25 / лекція )", "3.ІоТ ( 11:40-13:15 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://meet.google.com/wtb-igyr-gsc", "https://meet.google.com/jrf-dbjh-gab");
                break;
            case Actions.TUESDAY_PAIR_ACTION:
                title = "Розклад на парний Вівторок";
                buttonLabels = Arrays.asList("2.ТКП ( 9:50-11:25 / лекція )", "3.ООП ( 11:40-13:15 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://us02web.zoom.us/j/86094594224?pwd=ZGJiR0lNQndPQVM3UG9GR28xSlZBQT09", "https://bit.ly/hodovychenko_zoom");
                break;
            case Actions.TUESDAY_UNPAIR_ACTION:
                title = "Розклад на непарний Вівторок";
                buttonLabels = Arrays.asList("1.Психологія ( 8:00-9:35 / лекція )", "2.ТКП ( 9:50-11:25 / лекція )", "3.ООП ( 11:40-13:15 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://meet.google.com/iws-doif-gtp", "https://us02web.zoom.us/j/86094594224?pwd=ZGJiR0lNQndPQVM3UG9GR28xSlZBQT09", "https://bit.ly/hodovychenko_zoom");
                break;
            case Actions.WEDNESDAY_PAIR_ACTION:
                title = "Розклад на парну Середу";
                buttonLabels = Arrays.asList("3.ООП ( 11:40-13:15 / практ )", "4.ТКП ( 13:30-15:05 / лаб )", "Назад");
                buttonUrls = Arrays.asList("https://bit.ly/hodovychenko_zoom", "https://us02web.zoom.us/j/82173249381?pwd=N2VZK0VLT3R4ZFRsM2QwZU9LSU4zZz09");
                break;
            case Actions.WEDNESDAY_UNPAIR_ACTION:
                title = "Розклад на непарну Середу";
                buttonLabels = Arrays.asList("3.Філософія ( 11:40-13:15 / лаб )", "4.ТКП ( 13:30-15:05 / лаб )", "Назад");
                buttonUrls = Arrays.asList("https://teams.microsoft.com/l/meetup-join/19%3aqTaNLuiLp5Vbi0lNt7gdRBej4PPi7C5f093O2zd7PMk1%40thread.tacv2/1708806011137?context=%7b%22Tid%22%3a%22bd2fa240-71fc-45bd-abce-f1bc80beeb0a%22%2c%22Oid%22%3a%22436e6b06-99a3-4fb7-9eab-a754c660d7ad%22%7d", "https://us02web.zoom.us/j/82173249381?pwd=N2VZK0VLT3R4ZFRsM2QwZU9LSU4zZz09");
                break;
            case Actions.THURSDAY_PAIR_ACTION:
                title = "Розклад на парний Четверг";
                buttonLabels = Arrays.asList("1.СУБД ( 8:00-9:35 / лаб )", "2.Філософія ( 9:50-11:25 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://us05web.zoom.us/j/5324030015?pwd=czMvN2pwbnlQK1gvNFFMZXE4L2lyUT09", "https://teams.microsoft.com/l/meetup-join/19%3aqTaNLuiLp5Vbi0lNt7gdRBej4PPi7C5f093O2zd7PMk1%40thread.tacv2/1708806011137?context=%7b%22Tid%22%3a%22bd2fa240-71fc-45bd-abce-f1bc80beeb0a%22%2c%22Oid%22%3a%22436e6b06-99a3-4fb7-9eab-a754c660d7ad%22%7d");
                break;
            case Actions.THURSDAY_UNPAIR_ACTION:
                title = "Розклад на непарний Четверг";
                buttonLabels = Arrays.asList("1.ІоТ ( 8:00-9:35 / лаб )", "2.Філософія ( 9:50-11:25 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://us05web.zoom.us/j/5324030015?pwd=czMvN2pwbnlQK1gvNFFMZXE4L2lyUT09", "https://teams.microsoft.com/l/meetup-join/19%3aqTaNLuiLp5Vbi0lNt7gdRBej4PPi7C5f093O2zd7PMk1%40thread.tacv2/1708806011137?context=%7b%22Tid%22%3a%22bd2fa240-71fc-45bd-abce-f1bc80beeb0a%22%2c%22Oid%22%3a%22436e6b06-99a3-4fb7-9eab-a754c660d7ad%22%7d");
                break;
            case Actions.FRIDAY_PAIR_ACTION:
                title = "Розклад на парну П'ятницю";
                buttonLabels = Arrays.asList("1.ВЕБ-технології ( 8:00-9:35 / лаб )", "2.СУБД ( 9:50-11:25 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://us02web.zoom.us/j/82173249381?pwd=N2VZK0VLT3R4ZFRsM2QwZU9LSU4zZz09", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179");
                break;
            case Actions.FRIDAY_UNPAIR_ACTION:
                title = "Розклад на непарну П'ятницю";
                buttonLabels = Arrays.asList("1.ВЕБ-технології ( 8:00-9:35 / лаб )", "2.СУБД ( 9:50-11:25 / лекція )", "Назад");
                buttonUrls = Arrays.asList("https://us02web.zoom.us/j/82173249381?pwd=N2VZK0VLT3R4ZFRsM2QwZU9LSU4zZz09", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179");
                break;
            case Actions.HOMEWORK_ACTION:
                title = "Дз";
                buttonLabels = Arrays.asList("1.ВЕБ-технології", "2.СУБД", "3.ІоТ", "4.Філософія", "5.ТКП", "6.ООП", "7.Психилогія", "Назад");
                buttonUrls = Arrays.asList("https://us02web.zoom.us/j/82173249381?pwd=N2VZK0VLT3R4ZFRsM2QwZU9LSU4zZz09", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179", "https://us02web.zoom.us/j/82173249381?pwd=N2VZK0VLT3R4ZFRsM2QwZU9LSU4zZz09", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179", "https://meet.google.com/lookup/dfqnnvhx3k?authuser=1&hs=179");
                break;
            default:
                return;
        }
        executeEditeMessage(title, chatId, messageId, buttonLabels, buttonUrls);
    }


    private void handleCallbackDataAction(String callbackData, long chatId, long messageId) {
        switch (callbackData) {
            case Actions.PAIR_ACTION:
            case Actions.UNPAIR_ACTION:
                // Обработка действия для парного или непарного тижня
                executeEditeMessageText("Оберіть день " + (callbackData.equals(Actions.PAIR_ACTION) ? "парного" : "непарного") + " тижня:", chatId, messageId, callbackData.equals(Actions.PAIR_ACTION));
                break;
            case Actions.TIMETABLE_ACTION:
                // Отображение меню с выбором типа недели
                showWeekTypeMenu(chatId, messageId);
                break;
            case Actions.WEEK_ACTION:
                // Отображение текущего дня недели и типа недели
                showCurrentWeekDayAndType(chatId, messageId);
                break;
            case Actions.NOTIFICATION_ACTION:
                // Включение/выключение уведомлений
                toggleNotifications(chatId, messageId);
                break;
            case Actions.REGISTER_ACTION:
                registerUser(chatId);
                break;
            case Actions.UNREGISTER_ACTION:
                unregisterUser(chatId);
                break;
        }
    }

    private void handleBackAction(Update update) {
        Message message = (Message) update.getCallbackQuery().getMessage();
        if (message != null) {
            long chatId = message.getChatId();
            int messageId = message.getMessageId();

            String text = message.getText();

            switch (text) {
                case "Оберіть день парного тижня:", "Оберіть день непарного тижня:" ->
                        showWeekTypeMenu(chatId, messageId);
                case "Розклад на парний Понеділок", "Розклад на парний Вівторок", "Розклад на парну Середу", "Розклад на парний Четверг", "Розклад на парну П'ятницю" ->
                        executeEditeMessageText("Оберіть день парного тижня:", chatId, messageId, true);
                case "Розклад на непарний Понеділок", "Розклад на непарний Вівторок", "Розклад на непарну Середу", "Розклад на непарний Четверг", "Розклад на непарну П'ятницю" ->
                        executeEditeMessageText("Оберіть день непарного тижня:", chatId, messageId, false);
                default -> showMainMenu(chatId, messageId);
            }
        }
    }

    private void showWeekTypeMenu(long chatId, long messageId) {
        var replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(List.of(InlineKeyboardButton.builder().text("Парний тиждень").callbackData(Actions.PAIR_ACTION).build(), InlineKeyboardButton.builder().text("Непарний тиждень").callbackData(Actions.UNPAIR_ACTION).build())).keyboardRow(List.of(InlineKeyboardButton.builder().text("Назад").callbackData(Actions.BACK_ACTION).build())).build();

        var editMessage = EditMessageText.builder().chatId(String.valueOf(chatId)).messageId((int) messageId).text("Який вам потрібен тиждень?").replyMarkup(replyMarkup).build();

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void showCurrentWeekDayAndType(long chatId, long messageId) {
        // Определение текущего дня недели и типа недели
        // и отправка сообщения с этой информацией
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int weekNumber = LocalDate.now().get(weekFields.weekOfWeekBasedYear());
        String messageText = getString(weekNumber, dayOfWeek);

        var replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(List.of(InlineKeyboardButton.builder().text("Назад").callbackData(Actions.BACK_ACTION).build())).build();

        var editMessage = EditMessageText.builder().chatId(String.valueOf(chatId)).messageId((int) messageId).text(messageText).replyMarkup(replyMarkup).build();
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getString(int weekNumber, DayOfWeek dayOfWeek) {
        String weekType = (weekNumber % 2 == 0) ? "Непарний тиждень" : "Парний тиждень";
        return switch (dayOfWeek) {
            case MONDAY -> "Зараз Понеділок, " + weekType;
            case TUESDAY -> "Зараз Вівторок, " + weekType;
            case WEDNESDAY -> "Зараз Середа, " + weekType;
            case THURSDAY -> "Зараз Четвер, " + weekType;
            case FRIDAY -> "Зараз П'ятниця, " + weekType;
            case SATURDAY -> "Зараз Субота, " + weekType;
            case SUNDAY -> "Зараз Неділя, " + weekType;
        };
    }

    private void toggleNotifications(long chatId, long messageId) {
        boolean userExists = userRepository.existsById(chatId);
        String notificationText = userExists ? "Повідомлення про пари: ✅" : "Повідомлення про пари: ❌";
        String buttonLabel = userExists ? "Вимкнути повідомлення" : "Увімкнути повідомлення";

        // Build the reply markup with the appropriate buttons
        var replyMarkup = InlineKeyboardMarkup.builder()
                .keyboardRow(
                        List.of(
                                InlineKeyboardButton.builder()
                                        .text(buttonLabel)
                                        .callbackData(userExists ? Actions.UNREGISTER_ACTION : Actions.REGISTER_ACTION)
                                        .build(),
                                InlineKeyboardButton.builder()
                                        .text("Назад")
                                        .callbackData(Actions.BACK_ACTION)
                                        .build()
                        )
                )
                .build();

        // Construct the message to be edited
        var editMessage = EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId((int) messageId)
                .text(notificationText)
                .replyMarkup(replyMarkup)
                .build();

        // Send the edited message
        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            // Handle exceptions appropriately
            throw new RuntimeException(e);
        }
    }

    private void showMainMenu(long chatId, long messageId) {
        var editMessage = EditMessageText.builder()
                .chatId(String.valueOf(chatId))
                .messageId((int) messageId)
                .text(" ㅤ  ㅤ  ㅤ  ㅤ  ㅤ  ㅤ ")
                .replyMarkup(menuService.createMainMenu())
                .build();

        try {
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private void executeEditeMessage(String text, long chatId, long messageId, List<String> buttonLabels, List<String> buttonUrls) {
        try {
            // Создаем новую клавиатуру с кнопками
            InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = getLists(buttonLabels, buttonUrls);

            keyboardMarkup.setKeyboard(keyboard);

            // Создаем сообщение с новой клавиатурой
            EditMessageText editMessage = EditMessageText.builder().chatId(String.valueOf(chatId)).messageId((int) messageId).text(text).replyMarkup(keyboardMarkup).build();

            // Отправляем обновленное сообщение с новой клавиатурой
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<List<InlineKeyboardButton>> getLists(List<String> buttonLabels, List<String> buttonUrls) {
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();

        for (int i = 0; i < buttonLabels.size(); i++) {
            List<InlineKeyboardButton> row = new ArrayList<>();

            InlineKeyboardButton button = new InlineKeyboardButton();
            button.setText(buttonLabels.get(i));

            // Проверяем, является ли текущая кнопка кнопкой "Назад"
            if (buttonLabels.get(i).equals("Назад")) {
                // Устанавливаем callbackData для кнопки "Назад"
                button.setCallbackData(Actions.BACK_ACTION);
            } else {
                button.setUrl(buttonUrls.get(i));
            }

            row.add(button);
            keyboard.add(row);
        }
        return keyboard;
    }


    private void executeEditeMessageText(String text, long chatId, long messageId, boolean isPairWeek) {
        try {
            // Создаем новую клавиатуру с дополнительной кнопкой
            var replyMarkup = InlineKeyboardMarkup.builder().keyboardRow(List.of(InlineKeyboardButton.builder().text("Пн").callbackData(isPairWeek ? Actions.MONDAY_PAIR_ACTION : Actions.MONDAY_UNPAIR_ACTION).build(), InlineKeyboardButton.builder().text("Вт").callbackData(isPairWeek ? Actions.TUESDAY_PAIR_ACTION : Actions.TUESDAY_UNPAIR_ACTION).build(), InlineKeyboardButton.builder().text("Ср").callbackData(isPairWeek ? Actions.WEDNESDAY_PAIR_ACTION : Actions.WEDNESDAY_UNPAIR_ACTION).build(), InlineKeyboardButton.builder().text("Чт").callbackData(isPairWeek ? Actions.THURSDAY_PAIR_ACTION : Actions.THURSDAY_UNPAIR_ACTION).build(), InlineKeyboardButton.builder().text("Пт").callbackData(isPairWeek ? Actions.FRIDAY_PAIR_ACTION : Actions.FRIDAY_UNPAIR_ACTION).build())).keyboardRow(List.of(InlineKeyboardButton.builder().text("Назад").callbackData(Actions.BACK_ACTION).build())).build();


            // Создаем сообщение с новой клавиатурой
            var editMessage = EditMessageText.builder().chatId(String.valueOf(chatId)).messageId((int) messageId).text(text).replyMarkup(replyMarkup).build();

            // Отправляем обновленное сообщение с новой клавиатурой
            execute(editMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}