package Main;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import Dictionary.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class MyTelegramBot extends TelegramLongPollingBot {
    private PartOfWord partOfWord;
    private int correctAnswers;
    private Long chatId;
    private int wordsToRepeat;
    private int quantityUserToRepeat;
    private String correctWord;
    private boolean isWork = false;
    private String categoryOfWords;
    private String randomNumber = "";
    private String maxOfdictionaryNumber = "";
    private int userRightAnswersStats;
    private int userAllQuantityQuestionsStats;

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            chatId = update.getMessage().getChatId();

            if ("/start".equals(messageText)) {
                sendTextMessage(chatId, "Привет! Давай проверим твои знания итальянского языка. " +
                        "\nЕсли хочешь выйти, введи /exit" +
                        "\nЕсли хочешь посмотреть список команд, введи /help");
                wordsToRepeat = 1;
                correctAnswers = 0;
                askForCategory();
            } else if ("/exit".equalsIgnoreCase(messageText)) {
                sendTextMessage(chatId, "До свидания! \nЧтобы начать работу, напиши /start");
                return;
            } else if ("/stats".equalsIgnoreCase(messageText)){
                sendTextMessage(chatId, "На данный момент ты дал " +
                        userRightAnswersStats +
                        " правильных ответов из " +
                        userAllQuantityQuestionsStats);
            } else if ("/wordsleft".equalsIgnoreCase(messageText)){
                sendTextMessage(chatId, "На данный момент осталось слов для повторения " +
                        wordsToRepeat);
            } else if ("/help".equalsIgnoreCase(messageText)){
                sendTextMessage(chatId, """
                        /start - Начало работы
                        /exit - Выход
                        /stats - Статистика
                        /wordsleft - Осталось слов
                        /help - Помощь""");
            } else if (isWork) {
                if (partOfWord.dictionary().get(correctWord).equalsIgnoreCase(messageText)) {
                    sendTextMessage(chatId, "Верно!");
                    correctAnswers++;
                    askNextWord();
                } else {
                    sendTextMessage(chatId, "Неверно! \nПравильный ответ: " + partOfWord.dictionary().get(correctWord));
                    askNextWord();
                }
            }
        }  else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String data = callbackQuery.getData();
            if (data.matches("\\d+")) {
                handlerUserChoiceOfQuantityOfQuestions(data);
                isWork = true;
            } else {
                handlerUserChoiceOfCategory(data);
            }
        }
    }
    private void handlerUserChoiceOfCategory(String data) {
        switch (data) {
            case "verbs":
                sendTextMessage(chatId, "Ты выбрал глаголы!");
                partOfWord = new Verbs();
                askForQuantityOfQuestions(data);
                categoryOfWords = data;
                break;
            case "nouns":
                sendTextMessage(chatId, "Ты выбрал существительные!");
                partOfWord = new Nouns();
                askForQuantityOfQuestions(data);
                categoryOfWords = data;
                break;
            case "adjectives":
                sendTextMessage(chatId, "Ты выбрал прилагательные и наречия!");
                partOfWord = new Adjectives();
                askForQuantityOfQuestions(data);
                categoryOfWords = data;
                break;
            case "pronouns":
                sendTextMessage(chatId, "Ты выбрал местоимения!");
                partOfWord = new Pronouns();
                askForQuantityOfQuestions(data);
                categoryOfWords = data;
                break;
            case "phrases":
                sendTextMessage(chatId, "Ты выбрал фразы!");
                partOfWord = new Phrases();
                askForQuantityOfQuestions(data);
                categoryOfWords = data;
                break;
            case "allWords":
                sendTextMessage(chatId, "Ты выбрал все слова!");
                partOfWord = new AllWords();
                askForQuantityOfQuestions(data);
                categoryOfWords = data;
                break;
            default:
                break;
        }
    }
    private void handlerUserChoiceOfQuantityOfQuestions(String data) {
        sendTextMessage(chatId, "Отлично! приступим!");
        wordsToRepeat = Integer.parseInt(data);
        quantityUserToRepeat = Integer.parseInt(data);
        askNextWord();
        userAllQuantityQuestionsStats += quantityUserToRepeat;
    }
    private void askNextWord() {
        if (wordsToRepeat == 0) {
            showQuizResult();
            isWork = false;
            return;
        }

        String word = getRandomWord(partOfWord.dictionary());
        if (!word.isEmpty()) {
            sendTextMessage(chatId, "Как переводится слово \"" + word + "\"?");
            correctWord = word;
            wordsToRepeat--;
        } else {
            sendTextMessage(chatId, "Произошла ошибка при выборе слова. Пожалуйста, попробуй еще раз.");
        }
    }

    private String getRandomWord(Map<String, String> dictionary) {
        String[] keys = dictionary.keySet().toArray(new String[0]);
        return keys[new Random().nextInt(keys.length)];
    }

    private void showQuizResult() {
        userRightAnswersStats += correctAnswers;
        sendTextMessage(chatId, "Тест завершен. Правильных ответов: " +
                correctAnswers +
                " из " +
                quantityUserToRepeat +
                "\nЧтобы начать работу, напиши /start");
    }

    private void askForCategory() {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выбери категорию слов:");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Row1 = new InlineKeyboardButton();
        inlineKeyboardButton1Row1.setText("Глаголы");
        inlineKeyboardButton1Row1.setCallbackData("verbs");
        InlineKeyboardButton inlineKeyboardButton2Row1 = new InlineKeyboardButton();
        inlineKeyboardButton2Row1.setText("Существительные");
        inlineKeyboardButton2Row1.setCallbackData("nouns");
        keyboardButtonsRow1.add(inlineKeyboardButton1Row1);
        keyboardButtonsRow1.add(inlineKeyboardButton2Row1);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Row2 = new InlineKeyboardButton();
        inlineKeyboardButton1Row2.setText("Прилагательные и наречия");
        inlineKeyboardButton1Row2.setCallbackData("adjectives");
        InlineKeyboardButton inlineKeyboardButton2Row2 = new InlineKeyboardButton();
        inlineKeyboardButton2Row2.setText("Местоимения");
        inlineKeyboardButton2Row2.setCallbackData("pronouns");
        keyboardButtonsRow2.add(inlineKeyboardButton1Row2);
        keyboardButtonsRow2.add(inlineKeyboardButton2Row2);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Row3 = new InlineKeyboardButton();
        inlineKeyboardButton1Row3.setText("Фразы");
        inlineKeyboardButton1Row3.setCallbackData("phrases");
        InlineKeyboardButton inlineKeyboardButton2Row3 = new InlineKeyboardButton();
        inlineKeyboardButton2Row3.setText("Все слова");
        inlineKeyboardButton2Row3.setCallbackData("allWords");
        keyboardButtonsRow3.add(inlineKeyboardButton1Row3);
        keyboardButtonsRow3.add(inlineKeyboardButton2Row3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rowList);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void askForQuantityOfQuestions(String data) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Выбери количество слов для повторения:");

        randomNumber = String.valueOf(new Random().nextInt(partOfWord.dictionary().size()));
        maxOfdictionaryNumber = String.valueOf(partOfWord.dictionary().size());

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Row1 = new InlineKeyboardButton();
        inlineKeyboardButton1Row1.setText("5 слов");
        inlineKeyboardButton1Row1.setCallbackData("5");
        InlineKeyboardButton inlineKeyboardButton2Row1 = new InlineKeyboardButton();
        inlineKeyboardButton2Row1.setText("10 слов");
        inlineKeyboardButton2Row1.setCallbackData("10");
        keyboardButtonsRow1.add(inlineKeyboardButton1Row1);
        keyboardButtonsRow1.add(inlineKeyboardButton2Row1);

        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Row2 = new InlineKeyboardButton();
        inlineKeyboardButton1Row2.setText("15 слов");
        inlineKeyboardButton1Row2.setCallbackData("15");
        InlineKeyboardButton inlineKeyboardButton2Row2 = new InlineKeyboardButton();
        inlineKeyboardButton2Row2.setText("20 слов");
        inlineKeyboardButton2Row2.setCallbackData("20");
        keyboardButtonsRow2.add(inlineKeyboardButton1Row2);
        keyboardButtonsRow2.add(inlineKeyboardButton2Row2);

        List<InlineKeyboardButton> keyboardButtonsRow3 = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton1Row3 = new InlineKeyboardButton();
        inlineKeyboardButton1Row3.setText("Случайное число");
        inlineKeyboardButton1Row3.setCallbackData(randomNumber);
        InlineKeyboardButton inlineKeyboardButton2Row3 = new InlineKeyboardButton();
        inlineKeyboardButton2Row3.setText("Все слова словаря");
        inlineKeyboardButton2Row3.setCallbackData(maxOfdictionaryNumber);
        keyboardButtonsRow3.add(inlineKeyboardButton1Row3);
        keyboardButtonsRow3.add(inlineKeyboardButton2Row3);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);
        rowList.add(keyboardButtonsRow3);

        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        keyboardMarkup.setKeyboard(rowList);

        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    private void sendTextMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\User\\IdeaProjects\\tglearnluckybot\\src\\main\\resources\\tgBotInfo\\tgBotName.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            sendTextMessage(chatId, "Файл не найден");
        }
        String botUsername = content.toString();
        return botUsername;
    }

    @Override
    public String getBotToken() {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader("C:\\Users\\User\\IdeaProjects\\tglearnluckybot\\src\\main\\resources\\tgBotInfo\\tgBotToken.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            sendTextMessage(chatId, "Файл не найден");
        }
        String botToken = content.toString();
        return botToken;
    }
}
