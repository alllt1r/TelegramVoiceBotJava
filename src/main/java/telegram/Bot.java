package telegram;

import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import telegram.area.AreaFinding;
import telegram.coronavirus.CoronavirusParsing;
import telegram.weather.WeatherParsing;
import lombok.SneakyThrows;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

import java.util.ArrayList;
import java.util.Locale;

public class Bot extends TelegramLongPollingBot {

    WeatherParsing Weather = new WeatherParsing();
    AreaFinding Area = new AreaFinding();
    CoronavirusParsing Covid = new CoronavirusParsing();
    ReadProperties prop = new ReadProperties();

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        long chat_id = update.getMessage().getChatId();
        String message = update.getMessage().getText();

        if (update.getMessage().getLocation() != null){
            String longitude = update.getMessage().getLocation().getLongitude().toString();
            String latitude = update.getMessage().getLocation().getLatitude().toString();
            String city = Area.getCity(longitude, latitude);
            String country = Area.getCountry(longitude, latitude);
            sendMsg("Вы находитесь в городе " + city +
                    "\nПогода в городе " + city + " равна " + Weather.getTemperatureCelsium(city) + "℃" +
                    "\nКоличество заболевших в стране " + country + " за сегодня " +
                    "\nВалюта: ", chat_id);
        }
        if (message != null) {
            if (message.startsWith("/weather")) {
                String city = "";
                for (int i = 0; i < message.length(); i++) {
                    if (i >= 9) {
                        city += message.charAt(i);
                        System.out.println(city);
                    }
                }
                if (Weather.validateCity(city) == true) {
                    sendMsg("Погода в городе " + city + " равна " + Weather.getTemperatureCelsium(city) + "℃", chat_id);
                } else {
                    sendMsg("К сожалению мы не смогли найти город " + city + ". Введите команду ещё раз", chat_id);
                }
                city = "";
                /*
                System.out.println(update.getMessage().getLocation());
                KeyboardButton rew = new KeyboardButton();
                rew.getRequestLocation();
                 */
            }
            if (message.startsWith("/covid")) {
                String country = "";
                for (int i = 0; i < message.length(); i++) {
                    if (i >= 7) {
                        country += message.charAt(i);
                    }
                }
                if (Covid.getValidateCountry(country) == true) {
                    sendMsg("Количество заболевших в стране " + country + " за сегодня равно " + Covid.getConfirmedByCountry(country), chat_id);
                } else {
                    sendMsg("К сожалению мы не смогли найти страну " + country + ". Введите команду ещё раз", chat_id);
                }
            }
            if (message.equals("/set")) {
                sendMsgWithReply("Отправьте пожалуйста геолокацию", chat_id);
            }
            if (message.equals("/start")) {
                sendMsg("Здравствуйте, " + update.getMessage().getFrom().getFirstName() + "\nВот команды, которые я могу для вас выполнить:\n/set\n/weather + город\n/covid + страна", chat_id);
            }
        }

    }

    @SneakyThrows
    public synchronized void sendMsg(String s, long chat_id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        sendMessage.setText(s);
        sendMessage.setReplyMarkup(null);
        execute(sendMessage);
    }

    @SneakyThrows
    public synchronized void sendMsgWithReply(String s, long chat_id) {
        ReplyKeyboardMarkup key = new ReplyKeyboardMarkup();
        ArrayList<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardFirstRow = new KeyboardRow();
        KeyboardButton btn = new KeyboardButton();
        btn.setText("Send Location");
        btn.setRequestLocation(true);
        keyboardFirstRow.add(btn);
        keyboard.add(keyboardFirstRow);
        key.setKeyboard(keyboard);
        key.setResizeKeyboard(true);
        key.setOneTimeKeyboard(true);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chat_id);
        sendMessage.setText(s);
        sendMessage.setReplyMarkup(key);
        execute(sendMessage);
    }


    @Override
    public String getBotUsername() {
        return prop.getProp(("BOT.MASTER.NAME"));
    }

    @Override
    public String getBotToken() {
        return prop.getProp("BOT.MASTER.TOKEN");
    }
}