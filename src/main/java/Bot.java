import lombok.SneakyThrows;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

public class Bot extends TelegramLongPollingBot {

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

    }

    public synchronized void sendMsg(String s, long chat_id) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        sendMessage.setText(s);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //log.info( "Exception: " + e.toString());
        }
    }

    public synchronized void sendMsgWithReplyButton(ReplyKeyboardMarkup keyboardMarkup, String s, long chat_id) {

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        sendMessage.setText(s);
        sendMessage.setReplyMarkup(keyboardMarkup);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            //log.info("Exception: " + e.toString());
        }
    }

    @Override
    public String getBotUsername() {
        return System.getenv("TELEGRAMBOT_NAME");
    }

    @Override
    public String getBotToken() {
        return System.getenv("TOKEN");
    }
}