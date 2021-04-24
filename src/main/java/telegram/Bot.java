package telegram;

import telegram.Weather.Parsing;
import lombok.SneakyThrows;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    Parsing Weather = new Parsing();
    ReadProperties prop = new ReadProperties();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        long chat_id = update.getMessage().getChatId();
        String message = update.getMessage().getText();
        if (message.equals("weather")) {
            System.out.println(Weather.getTemperatureCelsium("Minsk"));
        }
    }

    @SneakyThrows
    public synchronized void sendMsg(String s, long chat_id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        sendMessage.setText(s);
        execute(sendMessage);
    }


    @Override
    public String getBotUsername() {
        return System.getenv("BOT.MASTER.NAME");
    }

    @Override
    public String getBotToken() {
        return prop.getProp("BOT.MASTER.TOKEN");
    }
}